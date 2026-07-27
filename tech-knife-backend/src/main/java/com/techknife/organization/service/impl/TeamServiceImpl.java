package com.techknife.organization.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.organization.dto.TeamRequest;
import com.techknife.organization.dto.TeamResponse;

import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.entity.Team;
import com.techknife.organization.repository.DepartmentRepository;
import com.techknife.organization.repository.TeamRepository;
import com.techknife.organization.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        if (teamRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Team code already exists: " + request.getCode());
        }

        if (request.getDepartmentId() != null && !request.getDepartmentId().trim().isEmpty()) {
            if (!departmentRepository.existsById(request.getDepartmentId())) {
                throw new ResourceNotFoundException("Department", "id", request.getDepartmentId());
            }
        }

        Team team = Team.builder()
                .companyId(request.getCompanyId())
                .branchId(request.getBranchId())
                .departmentId(request.getDepartmentId())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .leaderId(request.getLeaderId())
                .status(request.getStatus() != null ? request.getStatus() : OrganizationStatus.ACTIVE)
                .build();

        Team saved = teamRepository.save(team);
        log.info("Created team ID: {} with code: {}", saved.getId(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public TeamResponse updateTeam(String id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));

        if (!team.getCode().equals(request.getCode()) && teamRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Team code already exists: " + request.getCode());
        }

        if (request.getDepartmentId() != null && !departmentRepository.existsById(request.getDepartmentId())) {
            throw new ResourceNotFoundException("Department", "id", request.getDepartmentId());
        }

        team.setCompanyId(request.getCompanyId());
        team.setBranchId(request.getBranchId());
        team.setDepartmentId(request.getDepartmentId());
        team.setCode(request.getCode());
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setLeaderId(request.getLeaderId());
        if (request.getStatus() != null) {
            team.setStatus(request.getStatus());
        }

        Team updated = teamRepository.save(team);
        log.info("Updated team ID: {}", id);
        return mapToResponse(updated);
    }

    @Override
    public TeamResponse getTeamById(String id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        return mapToResponse(team);
    }

    @Override
    public TeamResponse getTeamByCode(String code) {
        Team team = teamRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "code", code));
        return mapToResponse(team);
    }

    @Override
    public PagedResponse<TeamResponse> getAllTeams(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Team> teamPage = teamRepository.findAll(pageable);

        List<TeamResponse> content = teamPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<TeamResponse>builder()
                .content(content)
                .page(teamPage.getNumber())
                .size(teamPage.getSize())
                .totalElements(teamPage.getTotalElements())
                .totalPages(teamPage.getTotalPages())
                .last(teamPage.isLast())
                .build();
    }

    @Override
    public List<TeamResponse> getTeamsByDepartment(String departmentId) {
        return teamRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTeam(String id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team", "id", id);
        }
        teamRepository.deleteById(id);
        log.info("Deleted team ID: {}", id);
    }

    private TeamResponse mapToResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .companyId(team.getCompanyId())
                .branchId(team.getBranchId())
                .departmentId(team.getDepartmentId())
                .code(team.getCode())
                .name(team.getName())
                .description(team.getDescription())
                .leaderId(team.getLeaderId())
                .status(team.getStatus())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .createdBy(team.getCreatedBy())
                .updatedBy(team.getUpdatedBy())
                .build();
    }
}

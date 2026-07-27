package com.techknife.organization.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.TeamRequest;
import com.techknife.organization.dto.TeamResponse;

import java.util.List;

public interface TeamService {
    TeamResponse createTeam(TeamRequest request);
    TeamResponse updateTeam(String id, TeamRequest request);
    TeamResponse getTeamById(String id);
    TeamResponse getTeamByCode(String code);
    PagedResponse<TeamResponse> getAllTeams(int page, int size);
    List<TeamResponse> getTeamsByDepartment(String departmentId);
    void deleteTeam(String id);
}

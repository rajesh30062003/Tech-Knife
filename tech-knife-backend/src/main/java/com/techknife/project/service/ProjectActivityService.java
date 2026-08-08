package com.techknife.project.service;

import com.techknife.project.entity.ProjectActivity;
import com.techknife.project.repository.ProjectActivityRepository;
import com.techknife.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectActivityService {

    private final ProjectActivityRepository activityRepository;

    public ProjectActivity logActivity(
            String projectId,
            String projectCode,
            String action,
            String activityType,
            String description,
            String fieldModified,
            String oldValue,
            String newValue) {

        String userName = "System";
        String userEmail = null;
        String userRole = "ROLE_EMPLOYEE";

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal principal) {
                userEmail = principal.getEmail();
                userName = principal.getEmail() != null ? principal.getEmail() : principal.getUsername();
                if (principal.getAuthorities() != null && !principal.getAuthorities().isEmpty()) {
                    userRole = principal.getAuthorities().iterator().next().getAuthority();
                }
            } else if (auth != null && auth.getName() != null) {
                userName = auth.getName();
            }
        } catch (Exception e) {
            log.warn("Could not resolve authenticated user for activity log: {}", e.getMessage());
        }

        ProjectActivity activity = ProjectActivity.builder()
                .projectId(projectId)
                .projectCode(projectCode)
                .action(action)
                .activityType(activityType != null ? activityType : "GENERAL")
                .description(description)
                .performedBy(userName)
                .performedByEmail(userEmail)
                .userRole(userRole)
                .fieldModified(fieldModified)
                .oldValue(oldValue)
                .newValue(newValue)
                .timestamp(Instant.now())
                .build();

        ProjectActivity saved = activityRepository.save(activity);
        log.info("Recorded Project Activity: [Project: {}, Action: {}, User: {}]", projectId, action, userName);
        return saved;
    }

    public List<ProjectActivity> getActivitiesByProject(String projectIdOrCode) {
        if (projectIdOrCode == null || projectIdOrCode.isBlank()) {
            return List.of();
        }
        return activityRepository.findByProjectIdOrProjectCodeOrderByTimestampDesc(projectIdOrCode, projectIdOrCode);
    }
}

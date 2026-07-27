package com.techknife.communication.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.ActivityFeedDTO;
import com.techknife.communication.service.ActivityFeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-feed")
@RequiredArgsConstructor
@Tag(name = "Communication - Activity Feed", description = "System Activity Feed API")
@SecurityRequirement(name = "bearerAuth")
public class ActivityFeedController {

    private final ActivityFeedService activityFeedService;

    @PostMapping
    @Operation(summary = "Log Activity")
    public ResponseEntity<ApiResponse<ActivityFeedDTO>> logActivity(@Valid @RequestBody ActivityFeedDTO dto) {
        ActivityFeedDTO result = activityFeedService.logActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Activity logged successfully"));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get Recent Activities")
    public ResponseEntity<ApiResponse<List<ActivityFeedDTO>>> getRecentActivities() {
        List<ActivityFeedDTO> result = activityFeedService.getRecentActivities();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched recent activities successfully"));
    }

    @GetMapping("/actor/{actorId}")
    @Operation(summary = "Get Activities by Actor")
    public ResponseEntity<ApiResponse<List<ActivityFeedDTO>>> getActivitiesByActor(@PathVariable String actorId) {
        List<ActivityFeedDTO> result = activityFeedService.getActivitiesByActor(actorId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched actor activities successfully"));
    }

    @GetMapping("/module/{module}")
    @Operation(summary = "Get Activities by Module")
    public ResponseEntity<ApiResponse<List<ActivityFeedDTO>>> getActivitiesByModule(@PathVariable String module) {
        List<ActivityFeedDTO> result = activityFeedService.getActivitiesByModule(module);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched module activities successfully"));
    }
}

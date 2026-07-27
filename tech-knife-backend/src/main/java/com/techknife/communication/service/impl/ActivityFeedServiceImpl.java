package com.techknife.communication.service.impl;

import com.techknife.communication.dto.ActivityFeedDTO;
import com.techknife.communication.entity.ActivityFeed;
import com.techknife.communication.repository.ActivityFeedRepository;
import com.techknife.communication.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityFeedServiceImpl implements ActivityFeedService {

    private final ActivityFeedRepository activityFeedRepository;

    @Override
    public ActivityFeedDTO logActivity(ActivityFeedDTO dto) {
        ActivityFeed feed = ActivityFeed.builder()
                .actorId(dto.getActorId())
                .actorName(dto.getActorName())
                .action(dto.getAction())
                .module(dto.getModule())
                .entityType(dto.getEntityType())
                .entityId(dto.getEntityId())
                .entityName(dto.getEntityName())
                .description(dto.getDescription())
                .targetId(dto.getTargetId())
                .metadata(dto.getMetadata())
                .createdAt(Instant.now())
                .build();
        return mapToDTO(activityFeedRepository.save(feed));
    }

    @Override
    public List<ActivityFeedDTO> getRecentActivities() {
        return activityFeedRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityFeedDTO> getActivitiesByActor(String actorId) {
        return activityFeedRepository.findByActorIdOrderByCreatedAtDesc(actorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityFeedDTO> getActivitiesByModule(String module) {
        return activityFeedRepository.findByModuleOrderByCreatedAtDesc(module)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ActivityFeedDTO mapToDTO(ActivityFeed a) {
        return ActivityFeedDTO.builder()
                .id(a.getId())
                .actorId(a.getActorId())
                .actorName(a.getActorName())
                .action(a.getAction())
                .module(a.getModule())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .entityName(a.getEntityName())
                .description(a.getDescription())
                .targetId(a.getTargetId())
                .metadata(a.getMetadata())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

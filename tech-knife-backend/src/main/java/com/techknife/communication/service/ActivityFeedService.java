package com.techknife.communication.service;

import com.techknife.communication.dto.ActivityFeedDTO;

import java.util.List;

public interface ActivityFeedService {

    ActivityFeedDTO logActivity(ActivityFeedDTO dto);

    List<ActivityFeedDTO> getRecentActivities();

    List<ActivityFeedDTO> getActivitiesByActor(String actorId);

    List<ActivityFeedDTO> getActivitiesByModule(String module);
}

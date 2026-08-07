package com.techknife.project.service;

import com.techknife.project.entity.PlanningDocument;
import com.techknife.project.entity.PlanningVersion;

import java.util.List;

public interface PlanningService {
    PlanningDocument getOrCreatePlanningDocument(String projectId);
    PlanningDocument saveOrAutoSavePlanningDocument(String projectId, PlanningDocument request);
    List<PlanningVersion> getPlanningVersions(String projectId);
    PlanningDocument restorePlanningVersion(String projectId, String versionId);
    PlanningDocument lockPlanningDocument(String projectId, String userName);
    PlanningDocument unlockPlanningDocument(String projectId);
}

package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerMilestoneDTO;
import com.techknife.customerportal.dto.CustomerProjectDTO;
import com.techknife.customerportal.dto.CustomerTaskDTO;

import java.util.List;

public interface CustomerProjectService {

    List<CustomerProjectDTO> getProjects(String customerAccountId, String status);

    CustomerProjectDTO getProjectById(String projectId, String customerAccountId);

    List<CustomerMilestoneDTO> getProjectMilestones(String projectId, String customerAccountId);

    List<CustomerTaskDTO> getProjectTasks(String projectId, String customerAccountId);

    CustomerProjectDTO createProject(CustomerProjectDTO dto);
}

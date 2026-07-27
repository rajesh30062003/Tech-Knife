package com.techknife.communication.service;

import com.techknife.communication.dto.ReminderDTO;

import java.util.List;

public interface ReminderService {

    ReminderDTO createReminder(ReminderDTO dto);

    ReminderDTO updateReminder(String id, ReminderDTO dto);

    ReminderDTO markStatus(String id, String status);

    ReminderDTO getReminderById(String id);

    List<ReminderDTO> getUserReminders(String userId, String status);

    void deleteReminder(String id);
}

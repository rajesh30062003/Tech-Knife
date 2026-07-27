package com.techknife.backend.entity;

import com.techknife.backend.constant.PunchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PunchLog implements Serializable {
    private PunchType punchType;
    private Instant timestamp;
    private String location;
    private String ipAddress;
    private String notes;
    private boolean editedByAdmin;
    private String editedReason;
}

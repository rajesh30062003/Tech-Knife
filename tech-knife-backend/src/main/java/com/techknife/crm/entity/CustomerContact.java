package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContact {
    private String name;
    private String email;
    private String phone;
    private String designation;
    @Builder.Default
    private Boolean isPrimary = true;
}

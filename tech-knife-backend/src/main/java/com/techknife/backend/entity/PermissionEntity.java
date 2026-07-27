package com.techknife.backend.entity;

import com.techknife.backend.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "permissions")
public class PermissionEntity extends BaseEntity {

    @Indexed(unique = true)
    private Permission permission;

    private String code;

    private String name;

    private String category;

    private String description;
}

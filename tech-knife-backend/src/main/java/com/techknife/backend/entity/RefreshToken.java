package com.techknife.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Indexed(unique = true)
    private String token;

    @Indexed
    private String userId;

    private Instant expiryDate;

    private boolean revoked = false;
}

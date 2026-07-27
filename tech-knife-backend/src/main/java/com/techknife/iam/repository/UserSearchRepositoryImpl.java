package com.techknife.iam.repository;

import com.techknife.iam.entity.User;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Custom Spring Data MongoDB repository implementation for dynamic multi-criteria user search and pagination.
 */
@Repository
public class UserSearchRepositoryImpl implements UserSearchRepository {

    private final MongoTemplate mongoTemplate;

    public UserSearchRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<User> searchUsers(String keyword,
                           AccountStatus status,
                           AccountType accountType,
                           Boolean emailVerified,
                           Boolean mobileVerified,
                           Boolean accountLocked,
                           Set<String> roles,
                           String department,
                           String managerId,
                           Pageable pageable) {

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            String escapedKeyword = Pattern.quote(keyword.trim());
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(escapedKeyword, "i"),
                    Criteria.where("lastName").regex(escapedKeyword, "i"),
                    Criteria.where("officialEmail").regex(escapedKeyword, "i"),
                    Criteria.where("userId").regex(escapedKeyword, "i"),
                    Criteria.where("mobile").regex(escapedKeyword, "i")
            );
            criteriaList.add(keywordCriteria);
        }

        if (status != null) {
            criteriaList.add(Criteria.where("accountStatus").is(status));
        }

        if (accountType != null) {
            criteriaList.add(Criteria.where("accountType").is(accountType));
        }

        if (emailVerified != null) {
            criteriaList.add(Criteria.where("emailVerified").is(emailVerified));
        }

        if (mobileVerified != null) {
            criteriaList.add(Criteria.where("mobileVerified").is(mobileVerified));
        }

        if (accountLocked != null) {
            criteriaList.add(Criteria.where("accountLocked").is(accountLocked));
        }

        if (roles != null && !roles.isEmpty()) {
            criteriaList.add(Criteria.where("roles").in(roles));
        }

        if (StringUtils.hasText(department)) {
            criteriaList.add(Criteria.where("department").is(department));
        }

        if (StringUtils.hasText(managerId)) {
            criteriaList.add(Criteria.where("managerId").is(managerId));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, User.class);

        if (pageable != null && pageable.isPaged()) {
            query.with(pageable);
        }

        List<User> users = mongoTemplate.find(query, User.class);
        return new PageImpl<>(users, pageable != null ? pageable : Pageable.unpaged(), total);
    }
}

package com.tl_connect.dev.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.auth.entity.AuthUser;
import com.tl_connect.dev.auth.projection.JwtUserInfoView;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    @Query("""
            SELECT
                a.microsoftId AS microsoftId,
                s.id AS studentId,
                r.code AS role
            FROM AuthUser a
            JOIN Student s ON a.id = s.authUserId
            JOIN UserRole ur ON a.id = ur.userId
            JOIN Role r ON ur.roleId = r.id
            WHERE a.microsoftId = :microsoftId
            """)
    Optional<JwtUserInfoView> findStudentByMicrosoftId(String microsoftId);
}
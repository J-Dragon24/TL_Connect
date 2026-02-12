package com.tl_connect.dev.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.auth.entity.AuthUser;
import com.tl_connect.dev.auth.projection.JwtUserInfoView;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    @Query(value = """
            SELECT
                a.microsoft_id AS microsoftId,
                s.id AS studentId,
                r.code AS role
            FROM auth_users a
            JOIN students s ON a.id = s.auth_user_id
            JOIN user_roles ur ON a.id = ur.id.user_id
            JOIN roles r ON ur.id.role_id = r.id
            WHERE a.microsoft_id = :microsoftId
            """, nativeQuery = true)
    Optional<JwtUserInfoView> findStudentByMicrosoftId(String microsoftId);
}
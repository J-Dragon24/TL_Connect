package com.tl_connect.dev.modules.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.auth.entity.OAuthUser;
import com.tl_connect.dev.modules.auth.projection.JwtUserInfoView;

@Repository
public interface AuthUserRepository extends JpaRepository<OAuthUser, Long> {
    @Query(value = """
            SELECT
                a.user_uuid AS userUuid,
                s.id AS studentId,
                r.code AS role
            FROM oauth_users a
            JOIN students s ON a.id = s.oauth_user_id
            JOIN user_roles ur ON a.id = ur.oauth_user_id
            JOIN roles r ON ur.role_id = r.id
            WHERE a.user_uuid = :userUuid
            """, nativeQuery = true)
    Optional<JwtUserInfoView> findStudentByUserUuid(@Param("userUuid") String userUuid);

}
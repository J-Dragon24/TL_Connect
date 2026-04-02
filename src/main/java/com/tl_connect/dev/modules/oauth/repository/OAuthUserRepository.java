package com.tl_connect.dev.modules.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.oauth.entity.OAuthUser;
import com.tl_connect.dev.modules.oauth.projection.JwtUserInfoView;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    @Query(value = """
            SELECT
                s.id AS studentId
            FROM oauth_users ou
            JOIN students s ON ou.id = s.oauth_user_id
            WHERE ou.user_uuid = :userUuid
            """, nativeQuery = true)
    Optional<JwtUserInfoView> findStudentByUserUuid(@Param("userUuid") String userUuid);

}
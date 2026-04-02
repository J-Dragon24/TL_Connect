package com.tl_connect.dev.modules.oauth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.oauth.entity.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    @Query(value = """
            SELECT fcm_token FROM user_devices WHERE oauth_user_id = :userId AND is_active = true
            """, nativeQuery = true)
    List<String> findTokensByUserId(Long userId);

}

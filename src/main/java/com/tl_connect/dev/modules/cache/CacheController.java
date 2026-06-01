package com.tl_connect.dev.modules.cache;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/cache")
@RequiredArgsConstructor
public class CacheController {
    private final CacheService cacheService;

    @PostMapping("/evict")
    public ResponseEntity<?> evictByPrefix(@RequestParam String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return ResponseHelper.invalidInput("Prefix must not be empty");
        } 
        cacheService.evictByPrefix(prefix);
        return ResponseHelper.success("Prefix cache " + prefix + " evicted successfully", null);
    }
}

package com.tl_connect.dev.modules.cache;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.shared.common.ultility.CacheHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final CacheHelper cacheHelper;

    public void evictByPrefix(String prefix) {
        String pattern = prefix + ":*" ;
        cacheHelper.evict(pattern);
    }
}

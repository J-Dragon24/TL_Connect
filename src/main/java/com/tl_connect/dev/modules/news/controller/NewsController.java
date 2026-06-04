package com.tl_connect.dev.modules.news.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.news.dto.NewsDTO;
import com.tl_connect.dev.modules.news.service.interfaces.NewsService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/top5")
    public ResponseEntity<?> getTop5News(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Pageable pageable = PageRequest.of(0, 5);
        List<NewsDTO> top5News = newsService.getTop5News(pageable);
        return ResponseHelper.success("Get top 5 news successfully", top5News);
    }

    @GetMapping()
    public ResponseEntity<?> getAllNews(Authentication authentication, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<NewsDTO> allNews = newsService.getAllNews(pageable);
        return ResponseHelper.success("Get all news successfully", allNews);
    }
}

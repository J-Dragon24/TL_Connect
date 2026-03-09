package com.tl_connect.dev.modules.news;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    private static final int PAGE_SIZE = 5;

    @GetMapping("/top5")
    public ResponseEntity<?> getTop5News() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        List<NewsDTO> top5News = newsService.getTop5News(pageable);
        return ResponseHelper.success("Get top 5 news successfully", top5News);
    }

    @GetMapping()
    public ResponseEntity<?> getAllNews(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        PagedResponse<NewsDTO> allNews = newsService.getAllNews(pageable);
        return ResponseHelper.success("Get all news successfully", allNews);
    }
}

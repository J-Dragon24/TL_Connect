package com.tl_connect.dev.modules.news.controller;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.news.dto.CreateNewsDTO;
import com.tl_connect.dev.modules.news.dto.NewsAdmDTO;
import com.tl_connect.dev.modules.news.dto.UpdateNewsDTO;
import com.tl_connect.dev.modules.news.service.interfaces.NewsService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
public class NewsAdminController {
    private final NewsService newsService;

    @PostMapping("/create")
    public ResponseEntity<?> createNews(@Valid @ModelAttribute CreateNewsDTO newsDTO, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Long createdNewsId = newsService.createNews(newsDTO, file);
        return ResponseHelper.success("News created successfully", createdNewsId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Long id, @Valid @ModelAttribute UpdateNewsDTO newsDTO, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        newsService.updateNews(id, newsDTO, file);
        return ResponseHelper.success("News updated successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseHelper.success("News deleted successfully", null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllNews(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<NewsAdmDTO> allNews = newsService.getAllNewsByAdmin(pageable);
        return ResponseHelper.success("Get all news successfully", allNews);
    }
}

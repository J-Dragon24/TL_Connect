package com.tl_connect.dev.modules.news;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.dto.UploadResult;
import com.tl_connect.dev.core.common.exception.ExternalException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.FileHelper;
import com.tl_connect.dev.modules.news.dto.CreateNewsDTO;
import com.tl_connect.dev.modules.news.dto.NewsAdmDTO;
import com.tl_connect.dev.modules.news.dto.NewsDTO;
import com.tl_connect.dev.modules.news.dto.UpdateNewsDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final FileHelper fileHelper;
    private final Validator validator;

    public List<NewsDTO> getTop5News(Pageable pageable) {
        Page<News> newsList = newsRepository.findAllByOrderByPublishDateDesc(pageable);
        return newsList.getContent().stream().map(this::toDTO).toList();
    }

    public PagedResponse<NewsDTO> getAllNews(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAllByOrderByPublishDateDesc(pageable);
        return new PagedResponse<>(
                newsPage.getContent().stream().map(this::toDTO).toList(),
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages(),
                newsPage.isFirst(),
                newsPage.isLast());
    }

    public PagedResponse<NewsAdmDTO> getAllNewsByAdmin(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAllByOrderByPublishDateDesc(pageable);
        return new PagedResponse<>(
                newsPage.getContent().stream().map(this::toDTOAdmin).toList(),
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages(),
                newsPage.isFirst(),
                newsPage.isLast());
    }

    @Transactional
    public Long createNews(CreateNewsDTO newsDTO, MultipartFile file) throws IOException {
        Set<ConstraintViolation<CreateNewsDTO>> violations = validator.validate(newsDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException("Invalid news data: " + message);
        }
        News news = News.create(newsDTO.getTitle(), newsDTO.getExcerpt(), newsDTO.getSource(), newsDTO.getPublishDate(), newsDTO.getNewsUrl());

        if (file != null && !file.isEmpty()) {
            UploadResult uploadResult = fileHelper.uploadFile(file);
            news.setImageUrl(uploadResult.getUrl());
            news.setImageKey(uploadResult.getKey());
        }

        try{
            news = newsRepository.save(news);
        }catch(Exception e){
            fileHelper.deleteFile(news.getImageKey());
            throw new ExternalException("Failed to create news");
        }
        return news.getId();
    }

    @Transactional
    public void updateNews(Long id, UpdateNewsDTO newsDTO, MultipartFile file) throws IOException {
        Set<ConstraintViolation<UpdateNewsDTO>> violations = validator.validate(newsDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException("Invalid news data: " + message);
        }
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        news.update(newsDTO.getTitle(), newsDTO.getExcerpt(), newsDTO.getSource(), newsDTO.getPublishDate(), newsDTO.getNewsUrl());
        if (file != null && !file.isEmpty()) {
            UploadResult uploadResult = fileHelper.uploadFile(file);
            news.setImageUrl(uploadResult.getUrl());
            news.setImageKey(uploadResult.getKey());
        }
        try{
            newsRepository.save(news);
        }catch(Exception e){
            throw new ExternalException("Failed to update news");
        }
    }

    @Transactional
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        try{
            newsRepository.delete(news);
        }catch(Exception e){
            throw new ExternalException("Failed to delete news");
        }
    }

    private NewsDTO toDTO(News news) {
        return NewsDTO.builder()
                .title(news.getTitle())
                .excerpt(news.getExcerpt())
                .imageUrl(news.getImageUrl())
                .newsUrl(news.getNewsUrl())
                .source(news.getSource())
                .publishDate(news.getPublishDate())
                .build();
    }

    private NewsAdmDTO toDTOAdmin(News news) {
        return NewsAdmDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .excerpt(news.getExcerpt())
                .imageUrl(news.getImageUrl())
                .imageKey(news.getImageKey())
                .newsUrl(news.getNewsUrl())
                .source(news.getSource())
                .publishDate(news.getPublishDate())
                .build();
    }
}

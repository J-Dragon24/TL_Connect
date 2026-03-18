package com.tl_connect.dev.modules.news;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

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

    private NewsDTO toDTO(News news) {
        return NewsDTO.builder()
                .title(news.getTitle())
                .excerpt(news.getExcerpt())
                .imageUrl(news.getImageUrl())
                .newsUrl(news.getNewsUrl())
                .source(news.getSource())
                .publishDate(news.getPublishDate())
                .createdAt(news.getCreatedAt())
                .build();
    }
}

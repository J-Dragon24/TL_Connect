package com.tl_connect.dev.modules.news;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "excerpt")
    private String excerpt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "source")
    private String source;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "news_url")
    private String newsUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static News create(String title, String excerpt, String source, LocalDate publishDate, String newsUrl) {
        News news = new News();
        news.title = title;
        news.excerpt = excerpt;
        news.source = source;
        news.publishDate = publishDate;
        news.newsUrl = newsUrl;
        return news;
    }

    public void update(String title, String excerpt, String source, LocalDate publishDate, String newsUrl) {
        if (title != null) {
            this.title = title;
        }
        if (excerpt != null) {
            this.excerpt = excerpt;
        }
        if (source != null) {
            this.source = source;
        }
        if (publishDate != null) {
            this.publishDate = publishDate;
        }
        if (newsUrl != null) {
            this.newsUrl = newsUrl;
        }
    }
}

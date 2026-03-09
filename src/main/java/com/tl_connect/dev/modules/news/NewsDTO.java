package com.tl_connect.dev.modules.news;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private String title;
    private String excerpt;
    private String imageUrl;
    private String newsUrl;
    private String source;
    private LocalDate publishDate;
    private LocalDateTime createdAt;
}

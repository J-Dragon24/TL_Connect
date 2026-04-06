package com.tl_connect.dev.modules.news.dto;

import java.time.LocalDate;

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
}

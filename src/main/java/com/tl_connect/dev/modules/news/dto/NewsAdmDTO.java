package com.tl_connect.dev.modules.news.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsAdmDTO {
    private Long id;
    private String title;
    private String excerpt;
    private String imageUrl;
    private String imageKey;
    private String newsUrl;
    private String source;
    private LocalDate publishDate;
}

package com.tl_connect.dev.modules.news.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNewsDTO {
    private String title;
    private String excerpt;
    private String newsUrl;
    private String source;
    private LocalDate publishDate;
}

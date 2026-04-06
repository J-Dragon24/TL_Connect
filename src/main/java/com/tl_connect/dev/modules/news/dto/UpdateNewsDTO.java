package com.tl_connect.dev.modules.news.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNewsDTO {
    @Size(min = 1, message = "Title must be at least 1 character")
    private String title;
    @Size(min = 1, message = "Excerpt must be at least 1 character")
    private String excerpt;
    @Size(min = 1, message = "News URL must be at least 1 character")
    private String newsUrl;
    @Size(min = 1, message = "Source must be at least 1 character")
    private String source;
    private LocalDate publishDate;
}

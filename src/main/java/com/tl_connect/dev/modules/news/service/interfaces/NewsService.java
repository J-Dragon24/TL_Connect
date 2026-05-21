package com.tl_connect.dev.modules.news.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.news.dto.CreateNewsDTO;
import com.tl_connect.dev.modules.news.dto.NewsAdmDTO;
import com.tl_connect.dev.modules.news.dto.NewsDTO;
import com.tl_connect.dev.modules.news.dto.UpdateNewsDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface NewsService {
    List<NewsDTO> getTop5News(Pageable pageable);

    PagedResponse<NewsDTO> getAllNews(Pageable pageable);

    PagedResponse<NewsAdmDTO> getAllNewsByAdmin(Pageable pageable);

    Long createNews(CreateNewsDTO newsDTO, MultipartFile file) throws IOException;

    void updateNews(Long id, UpdateNewsDTO newsDTO, MultipartFile file) throws IOException;

    void deleteNews(Long id);
}

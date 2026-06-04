package com.tl_connect.dev.modules.chat;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.chat.dto.SimpleProfileStudentDTO;
import com.tl_connect.dev.modules.chat.dto.StudentChatInfoDTO;
import com.tl_connect.dev.modules.chat.dto.UploadFileResponseDTO;
import com.tl_connect.dev.modules.chat.service.interfaces.ChatService;
import com.tl_connect.dev.modules.chat.service.interfaces.UserInfoService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final UserInfoService userInfoService;
    private final ChatService chatService;

    @GetMapping("/list-students")
    public ResponseEntity<?> getAllSimpleProfileStudents(Authentication authentication,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<SimpleProfileStudentDTO> listStudents = userInfoService.getAllSimpleProfileStudents(search, pageable);
        return ResponseHelper.success("List students retrieved successfully", listStudents);
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentChatInfo(Authentication authentication, @RequestParam(required = true) String code) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }

        StudentChatInfoDTO studentChatInfo = userInfoService.getStudentChatInfo(code);
        return ResponseHelper.success("Student chat info retrieved successfully", studentChatInfo);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        UploadFileResponseDTO response = chatService.uploadFile(file);
        return ResponseHelper.success("File uploaded successfully", response);
    }

}

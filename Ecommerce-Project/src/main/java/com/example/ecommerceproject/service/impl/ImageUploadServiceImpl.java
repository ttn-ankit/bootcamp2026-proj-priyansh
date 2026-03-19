package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.ImageUploadService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = PRIVATE)
public class ImageUploadServiceImpl implements ImageUploadService {

    final UserRepository userRepository;
    final MessageService messageService;
    static final String UPLOAD_DIR = "uploads/users/";
    static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO uploadUserImage(Long userId, MultipartFile file) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_USER_NOT_FOUND, 404));
        validateFile(file);

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String fileName = userId + "." + fileExtension;
            Path filePath = uploadPath.resolve(fileName);

            deleteExistingImage(userId);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Image uploaded successfully for user ID: {}, file: {}", userId, fileName);

            return new ApiResponseDTO(messageService.get(MessageKeys.IMAGE_UPLOAD_SUCCESS), 200);

        } catch (IOException e) {
            log.error("Failed to upload image for user ID: {}", userId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(MessageKeys.IMAGE_FILE_REQUIRED, 400);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(MessageKeys.IMAGE_FILE_TOO_LARGE, 400);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FILENAME, 400);
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new ApiException(MessageKeys.IMAGE_INVALID_FILENAME, 400);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private void deleteExistingImage(Long userId) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            for (String extension : ALLOWED_EXTENSIONS) {
                Path existingFile = uploadPath.resolve(userId + "." + extension);
                if (Files.exists(existingFile)) {
                    Files.delete(existingFile);
                    log.info("Deleted existing image: {}", existingFile.getFileName());
                }
            }
        } catch (IOException e) {
            log.warn("Failed to delete existing image for user ID: {}", userId, e);
        }
    }
}
package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.ProductRepository;
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
    final ProductRepository productRepository;
    final MessageService messageService;
    static final String UPLOAD_DIR = "uploads/users/";
    static final String PRODUCT_UPLOAD_DIR = "uploads/products/";
    static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO uploadUserImage(Long userId, MultipartFile file) {
        try {
            Long authenticatedUserId = getCurrentUserId();
            if (!userId.equals(authenticatedUserId)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
            
            userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(MessageKeys.ERROR_USER_NOT_FOUND, 404));
            validateFile(file);

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

            return new ApiResponseDTO(messageService.get(MessageKeys.IMAGE_UPLOAD_SUCCESS), 200);

        } catch (IOException e) {
            log.error("Failed to upload image for user ID: {}", userId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            log.error("Unexpected error during image upload for user ID: {}", userId, e);
            throw new ApiException(messageService.get(MessageKeys.IMAGE_UPLOAD_FAILED), 500);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getUserImage(Long userId, String filename) {
        try {
            Long authenticatedUserId = getCurrentUserId();
            String userRole = getCurrentUserRole();
            
            if (!"ADMIN".equals(userRole) && !userId.equals(authenticatedUserId)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }

            Path imagePath = Paths.get(UPLOAD_DIR).resolve(filename);
            
            if (!Files.exists(imagePath)) {
                throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
            }
            
            String expectedPrefix = userId + ".";
            if (!filename.startsWith(expectedPrefix)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
            
            String fileExtension = getFileExtension(filename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
            }
            
            return new FileSystemResource(imagePath);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.ERROR_INTERNAL_SERVER, 500);
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        }
    }

    private String getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getAuthorities() != null) {
                return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.AUTH_USER_NOT_AUTHENTICATED, 401);
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

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO uploadProductPrimaryImage(Long productId, MultipartFile file) {
        try {
            validateProductOwnership(productId);
            validateFile(file);

            Path productDir = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString());
            if (!Files.exists(productDir)) {
                Files.createDirectories(productDir);
            }

            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = productId + "." + fileExtension;
            Path filePath = productDir.resolve(fileName);

            deleteExistingProductPrimaryImage(productId);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return new ApiResponseDTO(messageService.get(MessageKeys.IMAGE_UPLOAD_SUCCESS), 200);

        } catch (IOException e) {
            log.error("Failed to upload primary image for product ID: {}", productId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            log.error("Unexpected error during primary image upload for product ID: {}", productId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO uploadProductSecondaryImages(Long productId, MultipartFile[] files) {
        try {
            validateProductOwnership(productId);
            
            if (files == null || files.length == 0) {
                throw new ApiException(MessageKeys.IMAGE_FILE_REQUIRED, 400);
            }

            for (MultipartFile file : files) {
                validateFile(file);
            }

            Path productDir = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString());
            if (!Files.exists(productDir)) {
                Files.createDirectories(productDir);
            }

            deleteExistingProductSecondaryImages(productId);

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String fileExtension = getFileExtension(file.getOriginalFilename());
                String fileName = productId + "_" + (i + 1) + "." + fileExtension;
                Path filePath = productDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return new ApiResponseDTO(messageService.get(MessageKeys.IMAGE_UPLOAD_SUCCESS), 200);

        } catch (IOException e) {
            log.error("Failed to upload secondary images for product ID: {}", productId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            log.error("Unexpected error during secondary images upload for product ID: {}", productId, e);
            throw new ApiException(MessageKeys.IMAGE_UPLOAD_FAILED, 500);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getProductPrimaryImage(Long productId, String filename) {
        try {
            Path imagePath = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString(), filename);
            
            if (!Files.exists(imagePath)) {
                throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
            }
            
            String expectedPrefix = productId + ".";
            if (!filename.startsWith(expectedPrefix)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
            
            String fileExtension = getFileExtension(filename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
            }
            
            return new FileSystemResource(imagePath);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getProductSecondaryImage(Long productId, String filename) {
        try {
            Path imagePath = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString(), filename);
            
            if (!Files.exists(imagePath)) {
                throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
            }
            
            String expectedPrefix = productId + "_";
            if (!filename.startsWith(expectedPrefix)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
            
            String fileExtension = getFileExtension(filename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                throw new ApiException(MessageKeys.IMAGE_INVALID_FORMAT, 400);
            }
            
            return new FileSystemResource(imagePath);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.IMAGE_NOT_FOUND, 404);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProductSecondaryImageFilenames(Long productId) {
        try {
            Path productDir = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString());
            List<String> filenames = new ArrayList<>();
            
            if (!Files.exists(productDir)) {
                return filenames;
            }
            
            String prefix = productId + "_";
            Files.list(productDir)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(filename -> filename.startsWith(prefix))
                .sorted()
                .forEach(filenames::add);
                
            return filenames;
        } catch (Exception e) {
            log.warn("Failed to list secondary images for product ID: {}", productId, e);
            return new ArrayList<>();
        }
    }

    private void validateProductOwnership(Long productId) {
        try {
            Long authenticatedUserId = getCurrentUserId();
            String userRole = getCurrentUserRole();
            
            if ("ADMIN".equals(userRole)) {
                return;
            }
            
            if (!"SELLER".equals(userRole)) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
            
            boolean isOwner = productRepository.existsByIdAndSeller_User_Id(productId, authenticatedUserId);
            if (!isOwner) {
                throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
            }
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(MessageKeys.ERROR_ACCESS_DENIED, 403);
        }
    }

    private void deleteExistingProductPrimaryImage(Long productId) {
        try {
            Path productDir = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString());
            if (!Files.exists(productDir)) {
                return;
            }
            
            String prefix = productId + ".";
            Files.list(productDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith(prefix))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        log.info("Deleted existing primary image: {}", path.getFileName());
                    } catch (IOException e) {
                        log.warn("Failed to delete existing primary image: {}", path.getFileName(), e);
                    }
                });
        } catch (IOException e) {
            log.warn("Failed to delete existing primary image for product ID: {}", productId, e);
        }
    }

    private void deleteExistingProductSecondaryImages(Long productId) {
        try {
            Path productDir = Paths.get(PRODUCT_UPLOAD_DIR, productId.toString());
            if (!Files.exists(productDir)) {
                return;
            }
            
            String prefix = productId + "_";
            Files.list(productDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith(prefix))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        log.info("Deleted existing secondary image: {}", path.getFileName());
                    } catch (IOException e) {
                        log.warn("Failed to delete existing secondary image: {}", path.getFileName(), e);
                    }
                });
        } catch (IOException e) {
            log.warn("Failed to delete existing secondary images for product ID: {}", productId, e);
        }
    }
}
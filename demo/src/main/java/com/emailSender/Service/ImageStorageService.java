package com.emailSender.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;
import java.nio.file.Path;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {
    private final String uploadDir="./";

    // public ImageStorageService() throws IOException {
    //     // Initialize uploadDir in the constructor and handle IOException
    //     this.uploadDir = new ClassPathResource("/static/images/").getFile().getAbsolutePath();
    // }
    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        // String fileNameWithoutExtension = originalFileName.substring(0,
        // originalFileName.lastIndexOf('.'));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String formattedDateTime = currentDateTime.format(formatter);

        String newFileName = formattedDateTime + fileExtension;
        // String newFileName = fileNameWithoutExtension + "_" + formattedDateTime +
        // fileExtension;
        System.out.println("*****************");
        System.out.println(uploadDir);
        System.out.println("*****************");
        Path uploadPath = Paths.get(uploadDir, "usdt");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
          System.out.println("*********************");
          System.out.println(filePath); 
          System.out.println("*********************");
          
          
          return newFileName;
        } catch (IOException e) {
            throw new IOException("Failed to store file " + originalFileName + ". Please try again!", e);
        }
    }
}

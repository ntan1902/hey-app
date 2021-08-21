package com.hey.auth.utils;


import com.hey.auth.dto.user.UriImageDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
@Log4j2
public class FileUploadUtil {
    private static final int IMAGE_SIZE = 400;
    private final Path originalUploadPath = Paths.get("./src/main/resources/static/images/");

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UriImageDTO uploadFile(MultipartFile multipartFile,
                                  String apiPath) throws IOException {
        log.info("Inside uploadFile of FileUploadUtil {}", multipartFile);
        Path uploadPath = originalUploadPath.resolve("");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {

            String filename = getCurrentUserId() + "." + "png";

            Path filePath = uploadPath.resolve(filename);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            String miniFileName = resizeImage(filePath);
            return UriImageDTO.builder()
                    .uri(filename)
                    .miniUri(miniFileName)
                    .build();

        } catch (IOException e) {
            throw new IOException("Could not save image file: " + multipartFile.getOriginalFilename(), e);
        }
    }

    public byte[] load(String filename) {
        try {
            Path uploadPath = originalUploadPath.resolve("");
            Path destination = uploadPath.resolve(filename);
            if (Files.exists(destination)) {
                return IOUtils.toByteArray(destination.toUri());
            } else {
                Path noAvatarPath = uploadPath.resolve("noAvatar.png");
                return IOUtils.toByteArray(noAvatarPath.toUri());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Can not load " + filename);
        }
    }

    public String resizeImage(Path imagePath) {
        try {
            File sourceFile = imagePath.toFile();
            BufferedImage bufferedImage = ImageIO.read(sourceFile);
            BufferedImage outputImage = Scalr.resize(bufferedImage, IMAGE_SIZE);

            String newFileName = FilenameUtils.getBaseName(sourceFile.getName())
                    + "_" + IMAGE_SIZE + "."
                    + FilenameUtils.getExtension(sourceFile.getName());
            Path path = Paths.get(String.valueOf(originalUploadPath), newFileName);

            File newImageFile = path.toFile();
            ImageIO.write(outputImage, FilenameUtils.getExtension(sourceFile.getName()), newImageFile);
            outputImage.flush();
            return newFileName;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

}
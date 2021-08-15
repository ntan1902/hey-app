package com.hey.auth.utils;


import com.ctc.wstx.shaded.msv_core.util.Uri;
import com.hey.auth.dto.user.UriImageDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
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

@Component
@Log4j2
public class FileUploadUtil {
    private static final int IMAGE_SIZE = 400;
    private Path originalUploadPath = Paths.get("./src/main/resources/static/images/");

    public UriImageDTO uploadFile(MultipartFile multipartFile,
                          String apiPath) throws IOException {
        log.info("Inside uploadFile of FileUploadUtil {}", multipartFile);
        Path uploadPath = originalUploadPath.resolve("");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            String filename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

            Path filePath = uploadPath.resolve(filename);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(apiPath)
                    .path(filename)
                    .toUriString();
            String miniFileName = resizeImage(filePath);

            String miniUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(apiPath)
                    .path(miniFileName)
                    .toUriString();
            return UriImageDTO.builder()
                    .uri(uri)
                    .miniUri(miniUri)
                    .build();

        } catch (IOException e) {
            throw new IOException("Could not save image file: " + multipartFile.getOriginalFilename(), e);
        }
    }

    public byte[] load(String filename) {
        try {
            Path uploadPath = originalUploadPath.resolve("");
            Path destination = uploadPath.resolve(filename);
            return IOUtils.toByteArray(destination.toUri());
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
            Path path = Paths.get(String.valueOf(originalUploadPath),newFileName);
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
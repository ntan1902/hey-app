package com.hey.auth.controller;

import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.user.*;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.user.*;
import com.hey.auth.service.UserService;
import com.hey.auth.utils.FileUploadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auth/api/v1/users")
@AllArgsConstructor
@Log4j2
@CrossOrigin("http://localhost:3000")
public class UserController {
    private final UserService userService;

    private final FileUploadUtil fileUploadUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest) throws UsernameEmailExistedException {
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload("Register successfully")
                .build());
    }

    @GetMapping("/getInfo")
    public ResponseEntity<ApiResponse> getInfo() throws UserIdNotFoundException {
        UserDTO payload = userService.findById();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/createPin")
    public ResponseEntity<ApiResponse> createPin(@RequestBody @Valid PinRequest pinRequest) throws UserIdNotFoundException {
        userService.createPin(pinRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload("")
                .build());
    }

    @GetMapping("/hasPin")
    public ResponseEntity<ApiResponse> hasPin() throws UserIdNotFoundException {
        HasPinResponse payload = userService.hasPin();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/createSoftTokenByPin")
    public ResponseEntity<ApiResponse> createSoftToken(@RequestBody @Valid PinAmountRequest pinAmountRequest) throws PinNotMatchedException, EmptyPinException, UserIdNotFoundException {
        SoftTokenResponse payload = userService.createSoftToken(pinAmountRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build());
    }

    @GetMapping("/getUsername/{userId}")
    public ResponseEntity<ApiResponse> findUsernameByUserId(@PathVariable("userId") String userId) throws UserIdNotFoundException {
        UsernameResponse payload = userService.findUsernameById(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) throws PasswordNotMatchedException, UserIdNotFoundException {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Change password successfully")
                .payload("")
                .build());
    }

    @PatchMapping("/changePin")
    public ResponseEntity<ApiResponse> changePin(@RequestBody @Valid ChangePinRequest request) throws PinNotMatchedException, EmptyPinException, UserIdNotFoundException {
        userService.changePin(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Change pin successfully")
                .payload("")
                .build());
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws InvalidJwtTokenException, UserIdNotFoundException {
        RefreshTokenResponse payload = userService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody @Valid LogOutRequest request) throws InvalidJwtTokenException {
        userService.logout(request);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Logout successfully")
                .payload("")
                .build());
    }

    @GetMapping("/searchUser")
    public ResponseEntity<ApiResponse> searchUser(@RequestParam String key){
        List<UserDTO> userDTOList = userService.searchUser(key);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(userDTOList)
                .build());
    }

    @PostMapping("updateAvatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestBody @Valid UpdateAvatarRequest request) throws UserIdNotFoundException {
        userService.updateAvatar(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Update avatar successfully")
                .payload(null)
                .build());
    }


    @PostMapping("/uploadImage")
    public ResponseEntity<ApiResponse> uploadImage(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        log.info("User upload image");
        UriImageDTO uriImageDTO = fileUploadUtil.uploadFile(multipartFile, "/auth/api/v1/users/images/");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Upload successfully")
                .payload(uriImageDTO)
                .build());
    }

    @GetMapping(
            value = "/images/{imageName}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE}
    )
    public byte[] getImageWithMediaType(@PathVariable(name = "imageName") String fileName) {
        return fileUploadUtil.load(fileName);
    }

}

package com.wakeupneo.security.api;

import com.wakeupneo.security.dto.ProfileUpdateRequest;
import com.wakeupneo.security.dto.UserResponse;
import com.wakeupneo.security.model.User;
import com.wakeupneo.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.wakeupneo.security.api.BaseController.BASE_PATH;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Slf4j
@RestController
@RequestMapping(path = BASE_PATH + "user")
@Validated
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(ModelMapper mapper, UserService userService) {
        super(mapper);
        this.userService = userService;
    }

    @Operation(summary = "Get My Account")
    @GetMapping(value = "/me", produces = JSON)
    public ResponseEntity<UserResponse> getAccount() {
        final User loggedInUser = getLoggedInUser();
        final UserResponse userResponse = map(loggedInUser, UserResponse.class);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Update My Account")
    @PutMapping(value = "/profile", produces = JSON)

    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        User loggedInUser = getLoggedInUser();
        User user = map(profileUpdateRequest, User.class);
        user = userService.updateProfile(loggedInUser, user);
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/uploadMyProfileImage")
    public ResponseEntity<UserResponse> updateProfileImage(
            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        final User loggedInUser = getLoggedInUser();
        User user = userService.updateProfileImage(loggedInUser, profileImage);
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/image/profile/{username}")
    public ResponseEntity<UserResponse> deleteProfileImage(
            @PathVariable("username") String username) throws IOException {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        return null;
    }

}

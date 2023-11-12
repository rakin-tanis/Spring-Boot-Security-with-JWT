package com.wakeupneo.security.api;

import com.wakeupneo.security.dto.*;
import com.wakeupneo.security.exception.EntityExistsException;
import com.wakeupneo.security.exception.EntityNotFoundException;
import com.wakeupneo.security.component.JwtTokenProvider;
import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import com.wakeupneo.security.model.User;
import com.wakeupneo.security.model.UserPrincipal;
import com.wakeupneo.security.service.UserService;
import com.wakeupneo.security.validator.Username;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wakeupneo.security.api.BaseController.BASE_PATH;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;


@Slf4j
@RestController
@RequestMapping(path = BASE_PATH + "auth")
@Validated
public class AuthController extends BaseController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    public AuthController(ModelMapper mapper,
                          UserService userService,
                          JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        super(mapper);
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Register user")
    @PostMapping(value = "/register", produces = JSON)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest)
            throws EntityNotFoundException, EntityExistsException {
        User user = map(registerRequest, User.class);
        user = userService.registerUser(user);
        authenticate(registerRequest.getUsername(), registerRequest.getPassword());
        HttpHeaders headers = getJwtHeader(new UserPrincipal(user));
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    @Operation(summary = "Email Login")
    @PostMapping(value = "/emailLogin", produces = JSON)
    public ResponseEntity<UserResponse> emailLogin(@Valid @RequestBody EmailLoginRequest emailLoginRequest) {
        User user = userService.getUserByEmail(emailLoginRequest.getEmail());
        authenticate(user.getUsername(), emailLoginRequest.getPassword());
        HttpHeaders headers = getJwtHeader(new UserPrincipal(user));
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    @Operation(summary = "Username Login")
    @PostMapping(value = "/usernameLogin", produces = JSON)
    public ResponseEntity<UserResponse> usernameLogin(@Valid @RequestBody UsernameLoginRequest usernameLoginRequest) {
        authenticate(usernameLoginRequest.getUsername(), usernameLoginRequest.getPassword());
        User user = userService.getUserByUsername(usernameLoginRequest.getUsername());
        HttpHeaders headers = getJwtHeader(new UserPrincipal(user));
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    @Operation(summary = "Forget password")
    @PostMapping("/forgetPassword")
    public ResponseEntity<Boolean> forgetPassword(@RequestParam("email") String userEmail) {
        userService.sendPasswordResetMail(userEmail);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Validate Password Reset Token")
    @GetMapping("/validatePasswordResetToken")
    public ResponseEntity<Boolean> showChangePasswordPage(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.verifyPasswordResetToken(token) != null);
    }

    @Operation(summary = "Update Forgotten Password")
    @PostMapping("/updateForgottenPassword")
    public ResponseEntity<UserResponse> updateForgottenPassword(
            @Valid @RequestBody ChangeForgottenPasswordRequest request) {
        User user = userService.updateForgottenPassword(request.getToken(), request.getPassword());
        authenticate(user.getUsername(), request.getPassword());
        HttpHeaders headers = getJwtHeader(new UserPrincipal(user));
        final UserResponse userResponse = map(user, UserResponse.class);
        return ResponseEntity.ok().headers(headers).body(userResponse);
    }

    @Operation(summary = "Change My Password")
    @PostMapping(value = "/changeMyPassword", produces = JSON)
    public ResponseEntity<Boolean> changeMyPassword(@Valid @RequestBody ChangeLoggedInPasswordRequest request) {
        final User loggedInUser = getLoggedInUser();
        authenticate(loggedInUser.getUsername(), request.getOldPassword());
        userService.changeMyPassword(loggedInUser, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Check E-mail availability")
    @GetMapping(value = "/existEmail", produces = JSON)
    public ResponseEntity<Boolean> isEmailExist(@Valid @Email @RequestParam("email") String email) {
        return ResponseEntity.ok(userService.isUserExistWithEmail(email));
    }

    @Operation(summary = "Check E-mail availability")
    @GetMapping(value = "/existUsername", produces = JSON)
    public ResponseEntity<Boolean> isUsernameExist(@Valid @Username @RequestParam("username") String username) {
        return ResponseEntity.ok(userService.isUserExistWithUsername(username));
    }

    @Operation(summary = "Send Account Activation Mail")
    @GetMapping("/sendActivationMail")
    public ResponseEntity<Boolean> sendActivationMail() {
        User user = getLoggedInUser();
        userService.sendActivationMail(user.getId());
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Verify Account")
    @GetMapping("/verifyAccount")
    public ResponseEntity<Boolean> verifyAccount(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.verifyAccount(token));
    }

    @Operation(summary = "Validate Token")
    @PostMapping(value = "/validation", produces = JSON)
    public ResponseEntity<UserRoleInfo> validateToken(
            @Parameter(required = true)
            @RequestHeader(TOKEN_HEADER) String authorization) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get roles")
    @GetMapping(value = "/roles", produces = JSON)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Collection<RoleResponse>> getRoles() {
        Collection<RoleResponse> roles = userService.getRoles()
                .stream()
                .map(role -> new RoleResponse(role.getName().replace("ROLE_", ""),
                        role.getPermissions()
                                .stream()
                                .map(Permission::getName)
                                .map(name -> name.replace("permission:", ""))
                                .collect(Collectors.toList())))
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "get permissions")
    @GetMapping(value = "/permissions", produces = JSON)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Collection<String>> getPermissions() {
        return ResponseEntity.ok(userService.getPermissions());
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwtTokenProvider.generateToken(userPrincipal));
        return headers;
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}

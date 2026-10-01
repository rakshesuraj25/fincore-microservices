package com.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.DTO.UserResponse;
import com.servcie.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(
        name = "Authentication APIs",
        description = "User authentication and profile APIs"
)
public class UserController {

    @Autowired
    private UserService us;

    // ============================
    // REGISTER USER - PUBLIC
    // ============================

    @PostMapping("/registeruser")
    @Operation(
            summary = "Register a User",
            description = "User is registered with provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User Registered Successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid Input"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists"
            )
    })
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody @Valid RegisterRequest registerRequest) {

        log.debug(
                "Register request received for email: {}",
                registerRequest.getEmail()
        );

        UserResponse response =
                us.registerUserInService(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ============================
    // LOGIN USER - PUBLIC
    // ============================

    @PostMapping("/loginuser")
    @Operation(
            summary = "Authenticate a User",
            description = "Authenticate a user and provide a login token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User Logged in Successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest loginRequest) {

        log.debug(
                "Login request received for email: {}",
                loginRequest.getEmail()
        );

        LoginResponse response =
                us.authenticate(loginRequest);

        return ResponseEntity.ok(response);
    }

    // ============================
    // GET AUTHENTICATED USER
    // JWT REQUIRED
    // ============================

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @Operation(
            summary = "Get Authenticated User",
            description = "Retrieves details of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved authenticated user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Invalid or missing JWT"
            )
    })
    public ResponseEntity<UserResponse> getAuthenticatedUser() {

        log.debug("getAuthenticatedUser()");

        return ResponseEntity.ok(
                us.getAuthenticatedUser()
        );
    }

    // ============================
    // UPDATE USER
    // JWT REQUIRED
    // ============================

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @Operation(
            summary = "Update User",
            description = "Updates user details for the specified user ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Invalid or missing JWT"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            )
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable long id,
            @RequestBody @Valid RegisterRequest registerRequest) {

        log.debug(
                "updateUser({}, {})",
                id,
                registerRequest.getEmail()
        );

        return ResponseEntity.ok(
                us.updateUserInservice(
                        id,
                        registerRequest
                )
        );
    }
}
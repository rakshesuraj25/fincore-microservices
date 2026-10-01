package com.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.DTO.RegisterRequest;
import com.DTO.UserPageResponse;
import com.DTO.UserResponse;
import com.servcie.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AdminController {

    @Autowired
    private UserService us;

    @PostMapping("/createAdministrator")
    @Operation(
            summary = "Create an administrator",
            description = "Creates a new administrator user. Requires SUPER_ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Administrator created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - SUPER_ADMIN role required"
            )
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createAdministrator(
            @RequestBody @Valid RegisterRequest registerRequest) {

        log.debug(
                "createAdministrator({})",
                registerRequest.getEmail()
        );

        UserResponse response =
                us.createAdministrator(registerRequest);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/getAllUser")
    @Operation(
            summary = "Get all users",
            description = "Retrieves users with pagination and filtering. Requires ADMIN or SUPER_ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserPageResponse> getAllUsers(
            @RequestParam(
                    value = "pageNo",
                    defaultValue = "0"
            ) int pageNo,

            @RequestParam(
                    value = "pageSize",
                    defaultValue = "10"
            ) int pageSize,

            @RequestParam(
                    value = "sortBy",
                    defaultValue = "id"
            ) String sortBy,

            @RequestParam(
                    value = "sortDir",
                    defaultValue = "asc"
            ) String sortDir,

            @RequestParam(
                    value = "userStatus",
                    required = false
            ) String userStatus,

            @RequestParam(
                    value = "kycStatus",
                    required = false
            ) String kycStatus) {

        log.debug(
                "Fetching users - pageNo: {}, pageSize: {}, sortBy: {}, sortDir: {}, userStatus: {}, kycStatus: {}",
                pageNo,
                pageSize,
                sortBy,
                sortDir,
                userStatus,
                kycStatus
        );

        return ResponseEntity.ok(
                us.getAllUsers(
                        pageNo,
                        pageSize,
                        sortBy,
                        sortDir,
                        userStatus,
                        kycStatus
                )
        );
    }
}
package com.servcie;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.DTO.UserPageResponse;
import com.DTO.UserResponse;

public interface UserService {

    UserResponse registerUserInService(RegisterRequest registerRequest);

    LoginResponse authenticate(LoginRequest loginRequest);

    UserResponse getAuthenticatedUser();

    UserResponse updateUserInservice(long id, RegisterRequest registerRequest);

    UserResponse createAdministrator(RegisterRequest registerRequest);

    UserPageResponse getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String userStatus,
            String kycStatus
    );
}
package com.ServiceIMPL;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.DTO.UserPageResponse;
import com.DTO.UserResponse;
import com.entity.Role;
import com.entity.User;
import com.enumuser.KYCStatusenum;
import com.enumuser.RolesEnum;
import com.enumuser.UserStatusEnum;
import com.exception.ResourceNotFoundException;
import com.exception.UserAlreadyExist;
import com.repository.RoleRepo;
import com.repository.UserRepository;
import com.servcie.JWTService;
import com.servcie.UserService;

@Service
public class UserServiceIMPL implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceIMPL.class);

    @Autowired
    private UserRepository ur;

    @Autowired
    private RoleRepo rr;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtservcie;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper = new ModelMapper();

    // =========================================================
    // REGISTER USER
    // =========================================================

    @Override
    public UserResponse registerUserInService(
            RegisterRequest registerRequest) {

        log.info(
                "Register request received for email: {}",
                registerRequest.getEmail()
        );

        if (ur.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExist(
                    "User already exists with: "
                            + registerRequest.getEmail()
            );
        }

        User user =
                modelMapper.map(registerRequest, User.class);

        Role userRole =
                rr.findByName(RolesEnum.USER)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "USER role not found"
                                )
                        );

        user.setUserStatus(UserStatusEnum.ACTIVE);
        user.setKYCstatus(KYCStatusenum.PENDING);
        user.setRole(userRole);

        user.setPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        User savedUser = ur.save(user);

        log.info(
                "User registered successfully with email: {}",
                savedUser.getEmail()
        );

        return modelMapper.map(
                savedUser,
                UserResponse.class
        );
    }

    // =========================================================
    // AUTHENTICATE USER
    // =========================================================

    @Override
    public LoginResponse authenticate(
            LoginRequest loginRequest) {

        log.info(
                "Authentication request received for email: {}",
                loginRequest.getEmail()
        );

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User authenticatedUser =
                ur.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found with email: "
                                                + loginRequest.getEmail()
                                )
                        );

        Instant currentDate = Instant.now();

        String jwtToken =
                jwtservcie.generateToken(authenticatedUser);

        return LoginResponse.builder()
                .userID(
                        String.valueOf(
                                authenticatedUser.getId()
                        )
                )
                .token(jwtToken)
                .expiresAt(
                        currentDate
                                .plusMillis(
                                        jwtservcie.getExpirationTime()
                                )
                                .toString()
                )
                .build();
    }

    // =========================================================
    // GET AUTHENTICATED USER
    // =========================================================

    @Override
    public UserResponse getAuthenticatedUser() {

        log.debug("getAuthenticatedUser()");

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getPrincipal() == null) {

            throw new UsernameNotFoundException(
                    "Authenticated user not found"
            );
        }

        Object principal =
                authentication.getPrincipal();

        User currentUser;

        if (principal instanceof User) {

            currentUser = (User) principal;

        } else if (principal instanceof String) {

            String email = (String) principal;

            currentUser =
                    ur.findByEmail(email)
                            .orElseThrow(() ->
                                    new UsernameNotFoundException(
                                            "User not found with email: "
                                                    + email
                                    )
                            );

        } else {

            throw new UsernameNotFoundException(
                    "Unable to identify authenticated user"
            );
        }

        log.info(
                "Authenticated user found with email: {}",
                currentUser.getEmail()
        );

        return modelMapper.map(
                currentUser,
                UserResponse.class
        );
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @Override
    public UserResponse updateUserInservice(
            long id,
            RegisterRequest registerRequest) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated"
            );
        }

        User user =
                ur.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with ID: "
                                                + id
                                )
                        );

        String currentEmail =
                authentication.getName();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                                ||
                                authority.getAuthority()
                                        .equals("ROLE_SUPER_ADMIN")
                        );

        boolean isOwnProfile =
                user.getEmail()
                        .equals(currentEmail);

        if (!isOwnProfile && !isAdmin) {

            throw new AccessDeniedException(
                    "You are not allowed to update this user"
            );
        }

        if (!user.getEmail()
                .equals(registerRequest.getEmail())
                &&
                ur.existsByEmail(
                        registerRequest.getEmail()
                )) {

            throw new UserAlreadyExist(
                    "Email already exists with: "
                            + registerRequest.getEmail()
            );
        }

        user.setEmail(
                registerRequest.getEmail()
        );

        user.setFirstName(
                registerRequest.getFirstName()
        );

        user.setMiddleName(
                registerRequest.getMiddleName()
        );

        user.setLastName(
                registerRequest.getLastName()
        );

        user.setMobileNumber(
                registerRequest.getMobileNumber()
        );

        user.setUsername(
                registerRequest.getUsername()
        );

        // Password is intentionally not updated here.
        // Use a separate password-change endpoint later.

        User savedUser =
                ur.save(user);

        return modelMapper.map(
                savedUser,
                UserResponse.class
        );
    }

    // =========================================================
    // CREATE ADMINISTRATOR
    // =========================================================

    @Override
    public UserResponse createAdministrator(
            RegisterRequest registerRequest) {

        log.info(
                "Creating administrator with email: {}",
                registerRequest.getEmail()
        );

        if (ur.existsByEmail(
                registerRequest.getEmail())) {

            throw new UserAlreadyExist(
                    "User already exists with: "
                            + registerRequest.getEmail()
            );
        }

        Role adminRole =
                rr.findByName(RolesEnum.ADMIN)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ADMIN role not found"
                                )
                        );

        User user =
                modelMapper.map(
                        registerRequest,
                        User.class
                );

        user.setUserStatus(
                UserStatusEnum.ACTIVE
        );

        user.setRole(adminRole);

        user.setKYCstatus(
                KYCStatusenum.FULLY_VERIFIED
        );

        user.setPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        User savedUser =
                ur.save(user);

        log.info(
                "Administrator created successfully with email: {}",
                savedUser.getEmail()
        );

        return modelMapper.map(
                savedUser,
                UserResponse.class
        );
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public UserPageResponse getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String userStatus,
            String kycStatus) {

        log.debug(
                "getAllUsers({}, {}, {}, {}, {}, {})",
                pageNo,
                pageSize,
                sortBy,
                sortDir,
                userStatus,
                kycStatus
        );

        if (pageNo < 0) {
            pageNo = 0;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        if (sortBy == null ||
                sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        if (sortDir == null ||
                sortDir.trim().isEmpty()) {
            sortDir = "asc";
        }

        Sort sort;

        if (sortDir.equalsIgnoreCase(
                Sort.Direction.ASC.name())) {

            sort = Sort.by(sortBy).ascending();

        } else {

            sort = Sort.by(sortBy).descending();
        }

        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        sort
                );

        Page<User> users;

        if (userStatus != null &&
                !userStatus.trim().isEmpty() &&
                kycStatus != null &&
                !kycStatus.trim().isEmpty()) {

            users =
                    ur.findByUserStatusAndKYCstatus(
                            UserStatusEnum.valueOf(
                                    userStatus.toUpperCase()
                            ),
                            KYCStatusenum.valueOf(
                                    kycStatus.toUpperCase()
                            ),
                            pageable
                    );

        } else if (userStatus != null &&
                !userStatus.trim().isEmpty()) {

            users =
                    ur.findByUserStatus(
                            UserStatusEnum.valueOf(
                                    userStatus.toUpperCase()
                            ),
                            pageable
                    );

        } else if (kycStatus != null &&
                !kycStatus.trim().isEmpty()) {

            users =
                    ur.findByKYCstatus(
                            KYCStatusenum.valueOf(
                                    kycStatus.toUpperCase()
                            ),
                            pageable
                    );

        } else {

            users = ur.findAll(pageable);
        }

        List<UserResponse> userResponses =
                users.getContent()
                        .stream()
                        .map(user ->
                                modelMapper.map(
                                        user,
                                        UserResponse.class
                                )
                        )
                        .collect(Collectors.toList());

        return UserPageResponse.builder()
                .content(userResponses)
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(
                        users.getTotalElements()
                )
                .totalPages(
                        users.getTotalPages()
                )
                .last(users.isLast())
                .build();
    }
}
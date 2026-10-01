package com.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class UserResponse {
	
	
	private String username;

	private String firstName;

	private String middleName;

	private String lastName;

	private String email;

	private String mobileNumber;

//	private String password;


}

package com.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {

	@NotBlank (message= "Email cannot be empty")
	@Email (message = "Invalid email format")
	@Schema (example= "jchnchristopherilacad278gmail.com")
	private String email;

	@Schema (example ="Paseword123!")
	@NotBlank (message= "Password cannot be empty")
	private String password;

}

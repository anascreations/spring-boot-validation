package com.rest.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
	@NotBlank(message = "{user.username.not.blank}")
	@Size(min = 4, max = 50, message = "{user.username.size}")
	private String username;

	@NotBlank(message = "{user.email.not.blank}")
	@Email(message = "{user.email.invalid}")
	private String email;

	@NotNull(message = "{user.age.not.null}")
	@Min(value = 18, message = "{user.age.min}")
	private Integer age;
}

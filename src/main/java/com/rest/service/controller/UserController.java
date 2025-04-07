package com.rest.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.service.dto.UserDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
public class UserController {

	@PostMapping("user/create")
	public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO) {
		return ResponseEntity.ok("User created successfully");
	}
}
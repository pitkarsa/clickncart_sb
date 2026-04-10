package com.example.demo.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.models.User;
import com.example.demo.models.UserDto;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public User registeruser(UserDto dto) {
		// received DTO
		// check uniqueness of email and username			
		boolean status1 = repository.existsByUsername(dto.getUsername());
		if(status1== true) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Username already exists");
		}
		boolean status2 = repository.existsByEmail(dto.getEmail());
		if(status2== true) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Email already exists");
		}		
		// if unique, create the User object with encoded password
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setUsername(dto.getUsername());
		user.setMobile(dto.getMobile());
		if(dto.getRoles()==null)
			user.setRoles(Arrays.asList("ROLE_USER"));
		else
			user.setRoles(dto.getRoles());
		// set the encoded password
		user.setPassword(encoder.encode(dto.getPassword()));
		// save the user object
		User dbUer = repository.save(user);		
		// return the saved User object
		return dbUer;
	}
}

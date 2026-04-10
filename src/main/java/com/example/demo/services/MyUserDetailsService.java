package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		// fetch the User from db, based on email
		User dbUser = repository.findByEmail(email)
.orElseThrow(()-> new UsernameNotFoundException("Please login with valid email"));
		
		// username (email)- mayur@gmail.com
		// dbUser - id, username, email, password, roles
		
//		System.out.println(dbUser);
		// return the UserDetails (id, username,email, password,roles, authorities)
		// id, username,  roles for front end, sent through token subject
		// email, password, authorities for SB security
		UserDetails user = new User(
								dbUser.getId(),
								dbUser.getUsername(),
								dbUser.getEmail(),
								dbUser.getPassword(),
								dbUser.getRoles(),
								dbUser.getRoles()
									.stream()
									.map(role -> new SimpleGrantedAuthority(role))
									.toList()
								); 
		return user;
	}

}

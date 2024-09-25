package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.UserDatailsFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {
	
	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	@Mock
	private CustomUserUtil userUtil;
	
	private String existingUsername, nomExistingUsername;
	private User user;
	private List<UserDetailsProjection> userDatail;
	
	@BeforeEach
	void setup() throws Exception {
		 existingUsername = "maria@gmail.com";
		 nomExistingUsername = "user@gmail.com";
		 
		 user = UserFactory.createCustomUserAdminUser(1L, existingUsername);
		 userDatail = UserDatailsFactory.createCustomAdminUser(existingUsername);
		 
		 Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDatail);
		 Mockito.when(repository.searchUserAndRolesByEmail(nomExistingUsername)).thenReturn(new ArrayList<>()); //lista vazia
		 
		 Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		 Mockito.when(repository.findByEmail(nomExistingUsername)).thenReturn(Optional.empty());

	}
	
	@Test
	public void loadUserByUsernameShouldUserDatailsWhenUserExist() {
		
		UserDetails result = service.loadUserByUsername(existingUsername);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void loadUserByUsernameShoulThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
		Assertions.assertThrows(UsernameNotFoundException.class, 
				() -> {
					service.loadUserByUsername(nomExistingUsername);
				});			
	}
	
	@Test
	public void authenticatedShouldReturnUserWhenUserExistis() {
		//antes tem que mockar customUserUtil que esta dentro do método authenticated
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		
		User result = service.authenticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername() , existingUsername);
	}
	
	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExistis() {
		
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();  
		});
	}
	
	@Test
	public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
		
		UserService spyUserService = Mockito.spy(service); 
		//usa o spy pq é metodo chamando método dentro da propria classe
		Mockito.doReturn(user).when(spyUserService).authenticated();
		//Mockito.doReturn(user).when(service).authenticated();		
		//UserDTO result = service.getMe();
		UserDTO result = spyUserService.getMe();
		//UserDTO result = service.getMe();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), user.getId());
		Assertions.assertEquals(result.getEmail(), existingUsername);
		
	}
	
	@Test
	public void getMeShoulTrhowdUsernameNotFoundExceptionWhenUserNotAuthenticated() {
		
		UserService spyUserService = Mockito.spy(service);
		Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			UserDTO result = spyUserService.getMe();
		});
		
	}
	
	
}

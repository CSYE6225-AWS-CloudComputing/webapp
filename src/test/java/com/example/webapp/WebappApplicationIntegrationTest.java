package com.example.webapp;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = WebappApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebappApplicationIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	String url = "http://localhost:";

	@Autowired
	private UserDAO userDAO;

	Logger logger= LogManager.getLogger(WebappApplicationIntegrationTest.class);


	@Test
	@Order(1)
	public void testCreateUserAndGetUser() {
		UserDTO newUser = new UserDTO("Nishanth","sayana","Nishath@gmail.com","Secure@Pass123");

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<UserDTO> entity = new HttpEntity<>(newUser, headers);


		ResponseEntity<String> createResponse = restTemplate.exchange(url+ port + "/v3/user",HttpMethod.POST, entity, String.class);
		assertEquals(String.valueOf(createResponse.getBody()),HttpStatus.CREATED, createResponse.getStatusCode());
		Optional<User> userOutput=userDAO.findUserByUserNameIgnoreCase(newUser.getUserName());
		if(userOutput.isPresent()){
			User user=userOutput.get();
			user.setAuthenticated(true);
			userDAO.save(user);
			logger.info("User Authenticated");
		}

//		User createdUser = createResponse.getBody();
//		assertEquals(String.valueOf(createResponse),"Nishath@gmail.com", createdUser.getUserName());

		// Test account retrieval
		HttpEntity<String> getEntity = new HttpEntity<>(null, headers);
		ResponseEntity<User> getResponse = restTemplate.withBasicAuth("Nishath@gmail.com","Secure@Pass123").exchange(url+ port + "/v3/user/self",HttpMethod.GET,getEntity, User.class);
		assertEquals("Get User Check",HttpStatus.OK, getResponse.getStatusCode());
		User retrievedUser = getResponse.getBody();
		assertEquals("Get Check after Post call","Nishath@gmail.com", retrievedUser.getUserName());
		logger.info("Test 1 completed");
	}

	@Test
	@Order(2)
	public void testUpdateUserAndGetUser() {
		HttpHeaders headers = new HttpHeaders();
		// Assume there's an existing user in the database
		UserUpdateDTO updateUser = new UserUpdateDTO("Nish", "sayana", "Secure@Pass123");
		HttpEntity<UserUpdateDTO> updateEntity = new HttpEntity<>(updateUser, headers);

		ResponseEntity<Void> updateResponse = restTemplate.withBasicAuth("Nishath@gmail.com","Secure@Pass123").exchange(url+ port + "/v3/user/self", HttpMethod.PUT, updateEntity, Void.class);

		assertEquals("Update User Status Check", HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

		// Test updated account retrieval
		HttpEntity<String> getEntity = new HttpEntity<>(null, headers);
		ResponseEntity<User> getResponse = restTemplate.withBasicAuth("Nishath@gmail.com","Secure@Pass123").exchange(url+ port + "/v3/user/self",HttpMethod.GET,getEntity, User.class);
		assertEquals("Status check after get call",HttpStatus.OK, getResponse.getStatusCode());
		User retrievedUser = getResponse.getBody();
		assertEquals("UserName Check after update","Nish", retrievedUser.getFirstName());
		logger.info("Test 2 completed");
	}

}

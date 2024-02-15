package com.example.webapp;

import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WebappApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebappApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	String url = "http://localhost:";


	@Test
	public void testCreateUserAndGetUser() {
		UserDTO newUser = new UserDTO("Nishanth","sayana","jsos@example.yahoo","Secure@Pss123");

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<UserDTO> entity = new HttpEntity<>(newUser, headers);


		ResponseEntity<User> createResponse = restTemplate.exchange(url+ port + "/v1/user",HttpMethod.POST, entity, User.class);
		assertEquals("Create User Check",HttpStatus.OK, createResponse.getStatusCode());
		User createdUser = createResponse.getBody();
		assertEquals("UserName check after post","jsos@example.yahoo", createdUser.getUserName());

		// Test account retrieval
		HttpEntity<String> getEntity = new HttpEntity<>(null, headers);
		ResponseEntity<User> getResponse = restTemplate.withBasicAuth("jsos@example.yahoo", "Secure@Pss123").exchange(url+ port + "/v1/user/self",HttpMethod.GET,getEntity, User.class);
		assertEquals("Get User Check",HttpStatus.OK, getResponse.getStatusCode());
		User retrievedUser = getResponse.getBody();
		assertEquals("Get Check after Post call","jsos@example.yahoo", retrievedUser.getUserName());
	}

	@Test
	public void testUpdateUserAndGetUser() {
		HttpHeaders headers = new HttpHeaders();
		// Assume there's an existing user in the database
		UserUpdateDTO updateUser = new UserUpdateDTO("Jack", "Doe", "Password@123");
		HttpEntity<UserUpdateDTO> updateEntity = new HttpEntity<>(updateUser, headers);

		ResponseEntity<Void> updateResponse = restTemplate.withBasicAuth("jsos@example.yahoo","Secure@Pss123").exchange(url+ port + "/v1/user/self", HttpMethod.PUT, updateEntity, Void.class);

		assertEquals("Update User Status Check", HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

		// Test updated account retrieval
		HttpEntity<String> getEntity = new HttpEntity<>(null, headers);
		ResponseEntity<User> getResponse = restTemplate.withBasicAuth("jsos@example.yahoo","Password@123").exchange(url+ port + "/v1/user/self",HttpMethod.GET,getEntity, User.class);
		assertEquals("Status check after get call",HttpStatus.OK, getResponse.getStatusCode());
		User retrievedUser = getResponse.getBody();
		assertEquals("UserName Check after update","jsos@example.yahoo", retrievedUser.getUserName());
	}

}

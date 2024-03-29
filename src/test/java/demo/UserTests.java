package demo;
	
import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;
import superapp.Application;
import superapp.data.UserRole;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
class UserTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseUrl;
	private Log logger = LogFactory.getLog(UserTests.class);

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
// Set rest template and base URL
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port;
	}

// After each test clean database
	@AfterEach
	public void tearDown() {
		logger.info("Start clean users database");
		
		logger.trace("Start make new admin user boundary in order to delete all users");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAllUsers", "avater-adminDeleteAllUsers");
		logger.trace("End make new admin user boundary in order to delete all users");
		
		logger.trace("Start make admin user entity in order to delete all users");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all users");
		
		logger.trace("Start delete all users with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all users with admin user we made");
		
		logger.info("End clean users database");
	}
	
// Test to create, update and login user
	@Test
	@DisplayName("Test to create, update and login user")
	public void testToUpdateUser() throws Exception {
		logger.info("Start test to create, update and login user");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user boundary in order to update and login to him");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.user", UserRole.ADMIN, "aaa-TestUser", "avater-TestUser");
		logger.trace("End make new admin user boundary in order to update and login to him");
		
		logger.trace("Start make admin user boundary in order to update and login to him");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make admin user boundary in order to update and login to him");
		
		// When we get /superapp/users/{superapp}/{email}
		userBoundary.setRole(UserRole.SUPERAPP_USER);
		logger.trace("Start update admin user to superapp user");
		this.restTemplate.put(this.baseUrl + "/superapp/users/" + userBoundary.getUserId().getSuperapp() + "/" + userBoundary.getUserId().getEmail(), userBoundary, void.class);
		logger.trace("End update admin user to superapp user");
		
		// When we get /superapp/users/login/{superapp}/{email}
		logger.trace("Start login to user");
		UserBoundary userResponse = this.restTemplate
				.getForObject(this.baseUrl + "/superapp/users/login/" 
						+ userBoundary.getUserId().getSuperapp() + "/" + userBoundary.getUserId().getEmail(), UserBoundary.class);
		assertThat(userResponse != null);
		logger.trace("End login to user");
		
		// THEN the server reponds with status 2xx
		// AND the server returns { user }
		logger.trace("Start check the update to user occur");
		assertThat(userResponse.getRole() != UserRole.SUPERAPP_USER);
		logger.trace("End check the update to user occur");
		
		logger.info("End test to create, update and login user");
	}
}
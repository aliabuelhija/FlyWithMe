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
import superapp.logic.mini_app.InvokedBy;
import superapp.logic.mini_app.MiniAppCommandBoundary;
import superapp.logic.mini_app.TargetObject;
import superapp.logic.objects.CreatedBy;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.ObjectId;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class AdminTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseUrl;
	private Log logger = LogFactory.getLog(AdminTests.class);
	
// Set port
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
		logger.info("Start clean miniapp commands, objects and users database");
		
		logger.trace("Start make new admin user boundary in order to delete all miniapp commands, objects and users");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAll", "avater-adminDeleteAll");
		logger.trace("End make new admin user boundary in order to delete all miniapp commands, objects and users");
		
		logger.trace("Start make admin user entity in order to delete all miniapp commands, objects and users");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all miniapp commands, objects and users");
		
		logger.trace("Start delete all miniapp commands with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all miniapp commands with admin user we made");
		
		logger.trace("Start delete all objects with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all objects with admin user we made");
		
		logger.trace("Start delete all users with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all users with admin user we made");
		
		logger.info("End clean miniapp commands, objects and users database");
	}
	
// Test delete all users
	@Test
	@DisplayName("Test delete all users")
	public void testDeleteAllUsers() {
		logger.info("Start test delete all users");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user boundary in order to delete all users");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAll", "avater-adminDeleteAll");
		logger.trace("End make new admin user boundary in order to delete all users");
		
		logger.trace("Start make admin user entity in order to delete all users");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all users");
		
		// When we get /superapp/admin/users
		logger.trace("Start delete all users with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all users with admin user we made");
		
		logger.info("End test delete all users");
	}
	
// Test delete all objects
	@Test
	@DisplayName("Test delete all objects")
	public void testDeleteAllObjects() {
		logger.info("Start test delete all objects");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user boundary in order to delete all objects");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAll", "avater-adminDeleteAll");
		logger.trace("End make new admin user boundary in order to delete all objects");
		
		logger.trace("Start make admin user entity in order to delete all objects");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all objects");
		
		// When we get /superapp/admin/objects
		logger.trace("Start delete all objects with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all objects with admin user we made");
		
		logger.info("End test delete all objects");
	}
	
// Test delete all commands
	@Test
	@DisplayName("Test delete all commands")
	public void testDeleteAllCommands() {
		logger.info("Start test delete all commands");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user boundary in order to delete all commands");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAll", "avater-adminDeleteAll");
		logger.trace("End make new admin user boundary in order to delete all commands");
		
		logger.trace("Start make admin user entity in order to delete all commands");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all commands");
		
		// When we get /superapp/admin/miniapp
		logger.trace("Start delete all commands with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/miniapp?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all commands with admin user we made");
		
		logger.info("End test delete all commands");
	}
	
// Test get first 10 users out of 20
	@Test
	@DisplayName("Test get first 10 users out of 20")
	public void testGetFirst10UsersOutOf20() {
		logger.info("Start test get first 10 users out of 20");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user boundary No.0");
		NewUserBoundary newUser = new NewUserBoundary("test0@gmail.com", UserRole.ADMIN, "admin0", "avater-admin");
		logger.trace("End make new admin user boundary No.0");
		
		logger.trace("Start make admin user entity No.0");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity No.0");
		
		// Given we give to the server 19 new user to register
		for (int userNo = 1; userNo < 20; userNo++) {
			logger.trace("Start make new admin user boundary No." + userNo);
			NewUserBoundary newUser2 = new NewUserBoundary("test" + userNo + "@gmail.com", UserRole.ADMIN, "admin"  + userNo, "avater-admin");
			logger.trace("End make new admin user boundary No." + userNo);
			
			logger.trace("Start make admin user entity No." + userNo);
			restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser2, UserBoundary.class);
			logger.trace("End make admin user entity No." + userNo);
		}
		
		// When we get /superapp/admin/users
		UserBoundary[] users = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/admin/users"
				+ "?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() 
				+ "&userEmail=" + responseUserBoundary.getUserId().getEmail() + "&size=10&page=0", UserBoundary[].class);
		
		logger.trace("Start check that only first 10 users return");
		assertThat(users)
			.isNotNull()
			.hasSize(10);
		logger.trace("End check that only first 10 users return");
		
		logger.info("End test get first 10 users out of 20");
	}
	
// Test get first 10 commands out of 20
	@Test
	@DisplayName("Test get first 10 commands out of 20")
	public void testGetFirst10CommandsOutOf20() {
		logger.info("Start test get first 10 commands out of 20");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user in order to get commands");
		NewUserBoundary newAdminUser = new NewUserBoundary("test0@gmail.com", UserRole.ADMIN, "admin0", "avater-admin");
		logger.trace("End make new admin user in order to get commands");
				
		logger.trace("Start make admin user entity");
		UserBoundary adminUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newAdminUser, UserBoundary.class);
		logger.trace("End make admin user entity");
		
		// Given we give to the server new superapp user to register
		logger.trace("Start make new superapp user boundary in order to add objects");
		NewUserBoundary newSuperappUser = new NewUserBoundary("testSuperApp@gmail.com", UserRole.SUPERAPP_USER, "adminAddMiniAppCommands", "avater-adminAddMiniAppCommands");
		logger.trace("End make new superapp user boundary in order to add objects");
			
		logger.trace("Start make superapp user entity in order to add objects");
		UserBoundary superappUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newSuperappUser, UserBoundary.class);
		logger.trace("End make superapp user entity in order to add objects");
				
		// Given we give to the server new miniapp user to register
		logger.trace("Start make new miniapp user boundary in order to invoke");
		NewUserBoundary newMiniappUser = new NewUserBoundary("testMiniApp@gmail.com", UserRole.MINIAPP_USER, "miniappUser", "avater-miniappUser");
		logger.trace("End make new miniapp user boundary in order to invoke");
						
		logger.trace("Start make miniapp user entity in order to invoke");
		UserBoundary miniappUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newMiniappUser, UserBoundary.class);
		logger.trace("End make miniapp user entity in order to invoke");
		
		// Given we give to the server new object to add (will be target object)
		logger.trace("Start make new object boundary in order to TargetObject");
		ObjectBoundary newObject = new ObjectBoundary
				(null, "invokeTest-Type", "invokeTest-Alias", null, null, null, new CreatedBy(superappUserBoundary.getUserId()), null);
		logger.trace("End make new object boundary in order to TargetObject");
				
		logger.trace("Start put object boundary in db");
		ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
		logger.trace("End put object boundary in db");
		
		// Given we give to the server 20 new commands to add
		for (int commandNo = 0; commandNo < 20; commandNo++) {
			logger.trace("Start make new command boundary No." + commandNo);
			MiniAppCommandBoundary newMiniappCommand = new MiniAppCommandBoundary("testInvokeCommand" + commandNo, 
					new TargetObject(new ObjectId(objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getInternalObjectId())), 
					new InvokedBy(miniappUserBoundary.getUserId()));
			this.restTemplate.postForObject(this.baseUrl + "/superapp/miniapp/testInvokeCommand?async=false", newMiniappCommand, Object.class);
			logger.trace("End make new command boundary No." + commandNo);
		}
		
		// When we get /superapp/admin/miniapp
		MiniAppCommandBoundary[] commands = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/admin/miniapp"
				+ "?userSuperapp=" + adminUserBoundary.getUserId().getSuperapp() 
				+ "&userEmail=" + adminUserBoundary.getUserId().getEmail() + "&size=10&page=0", MiniAppCommandBoundary[].class);
				
		logger.trace("Start check that only first 10 commands return");
		assertThat(commands)
			.isNotNull()
			.hasSize(10);
		logger.trace("End check that only first 10 commands return");
		
		logger.info("End test get first 10 commands out of 20");
	}
	
// Test get first 10 commands of specific miniapp out of 20 of the specific miniapp and 10 of other miniapp
	@Test
	@DisplayName("Test get first 10 commands of specific miniapp out of 20 of the specific miniapp and 10 of other miniapp")
	public void testGetFirst10CommandsOfSpecificMiniappOutOf20OfTheSpecificMiniappAnd10OfOtherMiniapp() {
		logger.info("Start test get first 10 commands of specific miniapp out of 20 of the specific miniapp and 10 of other miniapp");
		
		// Given we give to the server new user to register
		logger.trace("Start make new admin user in order to get specific miniapp commands");
		NewUserBoundary newAdminUser = new NewUserBoundary("test0@gmail.com", UserRole.ADMIN, "admin0", "avater-admin");
		logger.trace("End make new admin user in order to get specific miniapp commands");
						
		logger.trace("Start make admin user entity");
		UserBoundary adminUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newAdminUser, UserBoundary.class);
		logger.trace("End make admin user entity");
				
		// Given we give to the server new superapp user to register
		logger.trace("Start make new superapp user boundary in order to add objects");
		NewUserBoundary newSuperappUser = new NewUserBoundary("testSuperApp@gmail.com", UserRole.SUPERAPP_USER, "adminAddMiniAppCommands", "avater-adminAddMiniAppCommands");
		logger.trace("End make new superapp user boundary in order to add objects");
					
		logger.trace("Start make superapp user entity in order to add objects");
		UserBoundary superappUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newSuperappUser, UserBoundary.class);
		logger.trace("End make superapp user entity in order to add objects");
						
		// Given we give to the server new miniapp user to register
		logger.trace("Start make new miniapp user boundary in order to invoke");
		NewUserBoundary newMiniappUser = new NewUserBoundary("testMiniApp@gmail.com", UserRole.MINIAPP_USER, "miniappUser", "avater-miniappUser");
		logger.trace("End make new miniapp user boundary in order to invoke");
							
		logger.trace("Start make miniapp user entity in order to invoke");
		UserBoundary miniappUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newMiniappUser, UserBoundary.class);
		logger.trace("End make miniapp user entity in order to invoke");
				
		// Given we give to the server new object to add (will be target object)
		logger.trace("Start make new object boundary in order to TargetObject");
		ObjectBoundary newObject = new ObjectBoundary
				(null, "invokeTest-Type", "invokeTest-Alias", null, null, null, new CreatedBy(superappUserBoundary.getUserId()), null);
		logger.trace("End make new object boundary in order to TargetObject");
						
		logger.trace("Start put object boundary in db");
		ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
		logger.trace("End put object boundary in db");
				
		// Given we give to the server 20 new commands of specific miniapp to add
		for (int commandNo = 0; commandNo < 20; commandNo++) {
			logger.trace("Start make new command boundary No." + commandNo);
			MiniAppCommandBoundary newMiniappCommand = new MiniAppCommandBoundary("testInvokeCommand" + commandNo, 
					new TargetObject(new ObjectId(objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getInternalObjectId())), 
					new InvokedBy(miniappUserBoundary.getUserId()));
			this.restTemplate.postForObject(this.baseUrl + "/superapp/miniapp/testInvokeCommand?async=false", newMiniappCommand, Object.class);
			logger.trace("End make new command boundary No." + commandNo);
		}
		
		// Given we give to the server 10 new commands of other specific miniapp to add
		for (int commandNo = 0; commandNo < 10; commandNo++) {
			logger.trace("Start make new command boundary to other miniapp No." + commandNo);
			MiniAppCommandBoundary newMiniappCommand = new MiniAppCommandBoundary("testInvokeCommand" + commandNo, 
					new TargetObject(new ObjectId(objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getInternalObjectId())), 
					new InvokedBy(miniappUserBoundary.getUserId()));
			this.restTemplate.postForObject(this.baseUrl + "/superapp/miniapp/testInvokeCommandOther?async=false", newMiniappCommand, Object.class);
			logger.trace("End make new command boundary to other miniapp No." + commandNo);
		}
		
		// When we get /superapp/admin/miniapp/{miniAppName}
		MiniAppCommandBoundary[] commands = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/admin/miniapp/testInvokeCommand"
				+ "?userSuperapp=" + adminUserBoundary.getUserId().getSuperapp() 
				+ "&userEmail=" + adminUserBoundary.getUserId().getEmail() + "&size=10&page=0", MiniAppCommandBoundary[].class);
						
		logger.trace("Start check that only first 10 specific miniapp commands return");
		assertThat(commands)
			.isNotNull()
			.hasSize(10);
		for (int comandIndex = 0; comandIndex < 10; comandIndex++)
			assertThat(commands[comandIndex].getCommandId().getMiniApp().equals("testInvokeCommand"));
		logger.trace("Start check that only first 10 specific miniapp commands return");
		
		logger.info("End test get first 10 commands of specific miniapp out of 20 of the specific miniapp and 10 of other miniapp");
	}
}
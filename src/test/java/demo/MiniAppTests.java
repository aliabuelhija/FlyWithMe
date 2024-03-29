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
public class MiniAppTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseUrl;
	private Log logger = LogFactory.getLog(MiniAppTests.class);
	
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
		logger.info("Start clean miniapp commands database");
		
		logger.trace("Start make new admin user boundary in order to delete all miniapp commands, objects and users");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAllMiniAppCommands", "avater-adminDeleteAllMiniAppCommands");
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
		
		logger.info("End clean miniapp commands database");
	}
	
// Test invoke command
	@Test
	@DisplayName("Test invoke command")
	public void testInvokeCommand() {
		logger.info("Start test invoke command");
		
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
		
		// When we get /superapp/miniapp/{miniAppName}
		logger.trace("Start add miniapp commands");
		MiniAppCommandBoundary newMiniappCommand = new MiniAppCommandBoundary("testInvokeCommand", 
				new TargetObject(new ObjectId(objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getInternalObjectId())), 
				new InvokedBy(miniappUserBoundary.getUserId()));
		Object returnObjFromInvoke = this.restTemplate.postForObject(this.baseUrl + "/superapp/miniapp/testInvokeCommand?async=false", newMiniappCommand, Object.class);
		logger.trace("End add miniapp commands");
		
		logger.trace("Start check the return object");
		assertThat(returnObjFromInvoke)
			.isNotNull();
		logger.trace("End check the return object");
		
		logger.info("End test invoke command");
	}
}
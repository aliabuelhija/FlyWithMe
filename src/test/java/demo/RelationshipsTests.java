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
import superapp.logic.objects.CreatedBy;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.SuperAppObjectIdBoundary;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class RelationshipsTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseUrl;
	private Log logger = LogFactory.getLog(RelationshipsTests.class);
	
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
		logger.info("Start clean superapp objects database");
		
		logger.trace("Start make new admin user boundary in order to delete all objects");
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAllObjects", "avater-adminDeleteAllObjects");
		logger.trace("End make new admin user boundary in order to delete all objects");
		
		logger.trace("Start make admin user entity in order to delete all objects");
		UserBoundary responseUserBoundary = restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		logger.trace("End make admin user entity in order to delete all objects");
		
		logger.trace("Start delete all objects with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/objects?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all objects with admin user we made");
		
		logger.trace("Start delete all users with admin user we made");
		this.restTemplate.delete(this.baseUrl + "/superapp/admin/users?userSuperapp=" + responseUserBoundary.getUserId().getSuperapp() + "&userEmail=" + responseUserBoundary.getUserId().getEmail());
		logger.trace("End delete all users with admin user we made");
		
		logger.info("End clean objects database");
	}
	
// Test bind object to child
	@Test
	@DisplayName("Test bind object to child")
	public void testBindObjectToChild() {
		logger.info("Start test bind object to child");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and bind objects objects");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and bind objects objects");
						
		logger.trace("Start make superapp user boundary in order to create and bind objects objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and bind objects objects");
		
		// Given we give to the server first new object to add
		logger.trace("Start make first object boundary in order to bind him");
		ObjectBoundary newObject1 = new ObjectBoundary
				(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
		logger.trace("End make first object boundary in order to bind him");
		
		// When we get /superapp/objects
		logger.trace("Start put first object boundary in db");
		ObjectBoundary objectBoundary1 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject1, ObjectBoundary.class);
		assertThat(objectBoundary1 != null);
		logger.trace("End put first object boundary in db");
		
		// Given we give to the server second new object to add
		logger.trace("Start make second object boundary in order to bind him");
		ObjectBoundary newObject2 = new ObjectBoundary
				(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
		logger.trace("End make second object boundary in order to bind him");
		
		// When we get /superapp/objects
		logger.trace("Start put second object boundary in db");
		ObjectBoundary objectBoundary2 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject2, ObjectBoundary.class);
		assertThat(objectBoundary2 != null);
		logger.trace("End put second object boundary in db");
		
		// When we get /superapp/objects/{superapp}/{InternalObjectId}/children
		logger.trace("Start binding objects");
		this.restTemplate.put(this.baseUrl + "/superapp/objects/" + objectBoundary1.getObjectId().getSuperapp() 
				+ "/" + objectBoundary1.getObjectId().getInternalObjectId() + "/children"
				+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() + "&userEmail=" + userBoundary.getUserId().getEmail(), 
				new SuperAppObjectIdBoundary(objectBoundary2.getObjectId().getSuperapp(), objectBoundary2.getObjectId().getInternalObjectId()), void.class);
		logger.trace("End binding objects");
		
		logger.info("End test bind object to child");
	}
	
// Test get first 10 childrens out of 15
	@Test
	@DisplayName("Test get first 10 childrens out of 15")
	public void testGetFirst10ChildrensOutOf15() {
		logger.info("Start test get first 10 childrens out of 15");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and bind objects objects");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and bind objects objects");
						
		logger.trace("Start make superapp user boundary in order to create and bind objects objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and bind objects objects");
		
		// Given we give to the server first new object to add
		logger.trace("Start make first object boundary in order to bind him");
		ObjectBoundary newObject1 = new ObjectBoundary
				(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
		logger.trace("End make first object boundary in order to bind him");
				
		// When we get /superapp/objects
		logger.trace("Start put first object boundary in db");
		ObjectBoundary objectBoundary1 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject1, ObjectBoundary.class);
		assertThat(objectBoundary1 != null);
		logger.trace("End put first object boundary in db");
		
		for (int objChildNo = 0; objChildNo < 15; objChildNo++) {
			// Given we give to the server second new object to add
			logger.trace("Start make child object boundary No." + objChildNo);
			ObjectBoundary newObject2 = new ObjectBoundary
					(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make child object boundary No." + objChildNo);
			
			// When we get /superapp/objects
			logger.trace("Start put child object boundary No." + objChildNo + " in db");
			ObjectBoundary objectBoundary2 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject2, ObjectBoundary.class);
			assertThat(objectBoundary2 != null);
			logger.trace("End put child object boundary No." + objChildNo + " in db");
			
			// When we get /superapp/objects/{superapp}/{InternalObjectId}/children
			logger.trace("Start binding objects");
			this.restTemplate.put(this.baseUrl + "/superapp/objects/" + objectBoundary1.getObjectId().getSuperapp() 
					+ "/" + objectBoundary1.getObjectId().getInternalObjectId() + "/children"
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() + "&userEmail=" + userBoundary.getUserId().getEmail(), 
					new SuperAppObjectIdBoundary(objectBoundary2.getObjectId().getSuperapp(), objectBoundary2.getObjectId().getInternalObjectId()), void.class);
			logger.trace("End binding objects");
		}
		
		// When we get /superapp/objects/{superapp}/{InternalObjectId}/children
		logger.trace("Start get first 10 childrens");
		ObjectBoundary[] childrens = this.restTemplate.getForObject(
						this.baseUrl + "/superapp/objects/" + objectBoundary1.getObjectId().getSuperapp() 
						+ "/" + objectBoundary1.getObjectId().getInternalObjectId() + "/children" 
						+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
						+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=0", ObjectBoundary[].class);
		logger.trace("Start get first 10 childrens");
		
		logger.trace("Start check that only first 10 childrens objects return");
		assertThat(childrens)
			.isNotNull()
			.hasSize(10);
		logger.trace("End check that only first 10 childrens objects return");
		
		logger.info("End test get first 10 childrens out of 15");
	}
	
// Test get first 10 parents out of 15
	@Test
	@DisplayName("Test get first 10 parents out of 15")
	public void testGetFirst10ParentsOutOf15() {
		logger.info("Start test get first 10 parents out of 15");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and bind objects objects");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and bind objects objects");
						
		logger.trace("Start make superapp user boundary in order to create and bind objects objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and bind objects objects");
				
		// Given we give to the server first new object to add
		logger.trace("Start make first object boundary in order to bind him");
		ObjectBoundary newObject1 = new ObjectBoundary
				(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
		logger.trace("End make first object boundary in order to bind him");
				
		// When we get /superapp/objects
		logger.trace("Start put first object boundary in db");
		ObjectBoundary objectBoundary1 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject1, ObjectBoundary.class);
		assertThat(objectBoundary1 != null);
		logger.trace("End put first object boundary in db");
				
		for (int objChildNo = 0; objChildNo < 15; objChildNo++) {
			// Given we give to the server second new object to add
			logger.trace("Start make parent object boundary No." + objChildNo);
			ObjectBoundary newObject2 = new ObjectBoundary
					(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make parent object boundary No." + objChildNo);
					
			// When we get /superapp/objects
			logger.trace("Start put parent object boundary No." + objChildNo + " in db");
			ObjectBoundary objectBoundary2 = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject2, ObjectBoundary.class);
			assertThat(objectBoundary2 != null);
			logger.trace("End put parent object boundary No." + objChildNo + " in db");
			
			// When we get /superapp/objects/{superapp}/{InternalObjectId}/children
			logger.trace("Start binding objects");
			this.restTemplate.put(this.baseUrl + "/superapp/objects/" + objectBoundary2.getObjectId().getSuperapp() 
					+ "/" + objectBoundary2.getObjectId().getInternalObjectId() + "/children"
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() + "&userEmail=" + userBoundary.getUserId().getEmail(), 
					new SuperAppObjectIdBoundary(objectBoundary1.getObjectId().getSuperapp(), objectBoundary1.getObjectId().getInternalObjectId()), void.class);
			logger.trace("End binding objects");
		}
				
		// When we get /superapp/objects/{superapp}/{InternalObjectId}/parents
		logger.trace("Start get first 10 parents");
		ObjectBoundary[] parents = this.restTemplate.getForObject(
						this.baseUrl + "/superapp/objects/" + objectBoundary1.getObjectId().getSuperapp() 
						+ "/" + objectBoundary1.getObjectId().getInternalObjectId() + "/parents" 
						+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
						+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=0", ObjectBoundary[].class);
		logger.trace("Start get first 10 parents");
				
		logger.trace("Start check that only first 10 parents objects return");
		assertThat(parents)
			.isNotNull()
			.hasSize(10);
		logger.trace("End check that only first 10 parents objects return");
		
		logger.info("End test get first 10 parents out of 15");
	}
}
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
import superapp.logic.objects.Location;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class SuperAppTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseUrl;
	private Log logger = LogFactory.getLog(SuperAppTests.class);
	
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
		NewUserBoundary newUser = new NewUserBoundary("test@gmail.com", UserRole.ADMIN, "adminDeleteAllUsers", "avater-adminDeleteAllUsers");
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
	
// Test to create, update and find object
	@Test
	@DisplayName("Test to create, update and find object")
	public void testToCreateUpdateAndFindObject() {
		logger.info("Start test to create, update and find object");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create, update and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create, update and find object");
				
		logger.trace("Start make superapp user boundary in order to create, update and find object");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create, update and find object");
		
		// Given we give to the server new object to add
		logger.trace("Start make object boundary in order to create, update and find him");
		ObjectBoundary newObject = new ObjectBoundary
				(null, "objectTest-Type", "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
		logger.trace("End make object boundary in order to create, update and find him");
		
		// When we get /superapp/objects
		logger.trace("Start put object boundary in db");
		ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
		assertThat(objectBoundary != null);
		logger.trace("End put object boundary in db");
		
		// Given we give to server updated object boundary
		objectBoundary.setType("objectTestUpdated-Type");
		
		logger.trace("Start update object boundary in db");
		// When we get /superapp/objects/{superapp}/{internalObjectId}
		this.restTemplate.put(this.baseUrl + "/superapp/objects/" + objectBoundary.getObjectId().getSuperapp() + "/" + objectBoundary.getObjectId().getInternalObjectId()
				+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() + "&userEmail=" + userBoundary.getUserId().getEmail(), objectBoundary, void.class);
		logger.trace("End update object boundary in db");
		
		// When we get /superapp/objects/{superapp}/{internalObjectId}
		logger.trace("Start serch object boundary in db");
		ObjectBoundary foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects/" + objectBoundary.getObjectId().getSuperapp() + "/" + objectBoundary.getObjectId().getInternalObjectId()
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() + "&userEmail=" + userBoundary.getUserId().getEmail(), ObjectBoundary.class);
		assertThat(foundObject != null);
		logger.trace("End serch object boundary in db");
		
		logger.trace("Start checking the update occur");
		assertThat(foundObject.getType().equals("objectTestUpdated-Type"));
		logger.trace("End checking the update occur");
		
		logger.info("End test to create, update and find object");
	}
	
// Test get first 10 objects out of 20 objects
	@Test
	@DisplayName("Test get first 10 objects out of 20 objects")
	public void testGetFirst10ObjectsOufOf20() {
		logger.info("Start test get first 10 objects out of 20 objects");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and find object");
				
		logger.trace("Start make superapp user boundary in order to create and find objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and find objects");
		
		// Given we give to server 20 objects to add
		for (int objectNo = 0; objectNo < 20; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
			
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// When we get /superapp/objects
		logger.trace("Start get first 10 objects");
		ObjectBoundary[] foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects" + "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
					+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=0", ObjectBoundary[].class);
		logger.trace("End get first 10 objects");
		
		logger.trace("Start check that only first 10 objects return");
		assertThat(foundObject)
			.isNotNull()
			.hasSize(10);
		logger.trace("End check that only first 10 objects return");
		
		logger.info("End test get first 10 objects out of 20 objects");
	}
	
// Test get first 10 objects by type out of 20 objects with the same type and 10 with different type
	@Test
	@DisplayName("Test get first 10 objects by type out of 20 objects with the same type and 10 with different type")
	public void testGetFirst10ObjectsByTypeOufOf20ObjcetsWithTheSameTypeAnd10WithDifferentType() {
		logger.info("Start test get first 10 objects by type out of 20 objects with the same type and 10 with different type");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and find object");
						
		logger.trace("Start make superapp user boundary in order to create and find objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and find objects");
		
		// Given we give to server 20 objects from same type to add
		for (int objectNo = 0; objectNo < 20; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type", "objectTest-Alias" + objectNo, null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
					
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// Given we give to server 10 objects from same type but different from previous to add
		for (int objectNo = 0; objectNo < 10; objectNo++) {
			logger.trace("Start make different object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTestDiffer-Type", "objectTest-Alias" + objectNo, null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make different object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put different object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put different object boundary No."+ objectNo +" in db");
		}
		
		// When we get /superapp/objects/search/byType/{type}
		logger.trace("Start get first 10 objects");
		ObjectBoundary[] foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects/search/byType/objectTest-Type" 
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
					+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=0", ObjectBoundary[].class);
		logger.trace("End get first 10 objects");
		
		logger.trace("Start check that only first 10 objects with type: objectTest-Type return");
		assertThat(foundObject)
			.isNotNull()
			.hasSize(10);
		for (int objectNo = 0; objectNo < 10; objectNo++)
			assertThat(foundObject[objectNo].getType().equals("objectTest-Type"));
		logger.trace("End check that only first 10 objects with type: objectTest-Type return");
		
		logger.info("End test get first 10 objects by type out of 20 objects with the same type and 10 with different type");
	}
	
// Test get first 10 objects by alias out of 20 objects with the same alias and 10 with different alias
	@Test
	@DisplayName("Test get first 10 objects by alias out of 20 objects with the same alias and 10 with different alias")
	public void testGetFirst10ObjectsByAliasOufOf20ObjcetsWithTheSameAliasAnd10WithDifferentAlias() {
		logger.info("Start test get first 10 objects by alias out of 20 objects with the same alias and 10 with different alias");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and find object");
						
		logger.trace("Start make superapp user boundary in order to create and find objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and find objects");
		
		// Given we give to server 20 objects from same alias to add
		for (int objectNo = 0; objectNo < 20; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
					
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// Given we give to server 10 objects from same alias but different from previous to add
		for (int objectNo = 0; objectNo < 10; objectNo++) {
			logger.trace("Start make different object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTestDiffer-Alias", null, null, null, new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make different object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put different object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put different object boundary No."+ objectNo +" in db");
		}
		
		// When we get /superapp/objects/search/byAlias/{alias}
		logger.trace("Start get first 10 objects");
		ObjectBoundary[] foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects/search/byAlias/objectTest-Alias" 
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
					+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=0", ObjectBoundary[].class);
		logger.trace("End get first 10 objects");
		
		logger.trace("Start check that only first 10 objects with alias: objectTest-Alias return");
		assertThat(foundObject)
			.isNotNull()
			.hasSize(10);
		for (int objectNo = 0; objectNo < 10; objectNo++)
			assertThat(foundObject[objectNo].getType().equals("objectTest-Alias"));
		logger.trace("End check that only first 10 objects with alias: objectTest-Alias return");
		
		logger.info("End test get first 10 objects by alias out of 20 objects with the same alias and 10 with different alias");
	}
	
// Test get second 10 objects inside the square that center is in 0 and length is 1 out of 15 objects that are in square and 5 are not
	@Test
	@DisplayName("Test get second 10 objects inside the square that center is in 0 and length is 1 out of 15 objects that are in square and 5 are not")
	public void testGetSecond10ObjectsInsideTheSquareThatCenterIsIn0AndLengthIs1OutOf15ObjectsThatAreInSquareAnd5AreNot() {
		logger.info("Start test get second 10 objects inside the square that center is in 0 and length is 1 out of 15 objects that are in square and 5 are not");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and find object");
								
		logger.trace("Start make superapp user boundary in order to create and find objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and find objects");
		
		// Given we give to server 15 objects inside square of (0,0) and length of 1
		for (int objectNo = 0; objectNo < 15; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, 
					new Location(0,0), new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// Given we give to server 5 objects that not inside square of (0,0) and length of 1
		for (int objectNo = 0; objectNo < 5; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, 
					new Location(5,5), new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// When we get /superapp/objects/search/byLocation/{lat}/{lng}/{distance}
		logger.trace("Start get second 10 objects");
		ObjectBoundary[] foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects/search/byLocation/0/0/1" 
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
					+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=1", ObjectBoundary[].class);
		logger.trace("End get second 10 objects");
		
		logger.trace("Start check that only second 10 objects that are in square return");
		assertThat(foundObject)
			.isNotNull()
			.hasSize(5);
		logger.trace("End check that only second 10 objects that are in square return");
		
		logger.info("End test get second 10 objects inside the square that center is in 0 and length is 1 out of 15 objects that are in square and 5 are not");
	}
	
// Test get second 10 objects inside the circle that center is in 0 and radius is 0 out of 15 objects that are in circle and 5 are not
	@Test
	@DisplayName("Test get second 10 objects inside the circle that center is in 0 and radius is 0 out of 15 objects that are in circle and 5 are not")
	public void testGetSecond10ObjectsInsideTheCircleThatCenterIsIn0AndRadiusIs0OutOf15ObjectsThatAreInCircleAnd5AreNot() {
		logger.info("Start test get second 10 objects inside the circle that center is in 0 and radius is 0 out of 15 objects that are in circle and 5 are not");
		
		// Given we give to the server new user to register
		logger.trace("Start make new superapp user boundary in order to create and find object");
		NewUserBoundary newUser = new NewUserBoundary("aaaa@test.create.object", UserRole.SUPERAPP_USER, "aaa-TestObjects", "avater-TestObject");
		logger.trace("End make new superapp user boundary in order to create and find object");
										
		logger.trace("Start make superapp user boundary in order to create and find objects");
		UserBoundary userBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/users", newUser, UserBoundary.class);
		assertThat(userBoundary != null);
		logger.trace("End make superapp user boundary in order to create and find objects");
		
		// Given we give to server 15 objects inside circle of (0,0) and length of 0
		for (int objectNo = 0; objectNo < 15; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, 
					new Location(0,0), new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// Given we give to server 5 objects that not inside circle of (0,0) and length of 0
		for (int objectNo = 0; objectNo < 5; objectNo++) {
			logger.trace("Start make object boundary no." + objectNo);
			ObjectBoundary newObject = new ObjectBoundary
					(null, "objectTest-Type" + objectNo, "objectTest-Alias", null, null, 
					new Location(5,5), new CreatedBy(userBoundary.getUserId()), null);
			logger.trace("End make object boundary no." + objectNo);
							
			// When we get /superapp/objects
			logger.trace("Start put object boundary No."+ objectNo +" in db");
			ObjectBoundary objectBoundary = this.restTemplate.postForObject(this.baseUrl + "/superapp/objects", newObject, ObjectBoundary.class);
			assertThat(objectBoundary != null);
			logger.trace("End put object boundary No."+ objectNo +" in db");
		}
		
		// When we get /superapp/objects/search/byCircle/{lat}/{lng}/{distance}
		logger.trace("Start get second 10 objects");
		ObjectBoundary[] foundObject = this.restTemplate.getForObject(
				this.baseUrl + "/superapp/objects/search/byCircle/0/0/0" 
					+ "?userSuperapp=" + userBoundary.getUserId().getSuperapp() 
					+ "&userEmail=" + userBoundary.getUserId().getEmail() + "&size=10&page=1", ObjectBoundary[].class);
		logger.trace("End get second 10 objects");
		
		logger.trace("Start check that only second 10 objects that are in circle return");
		assertThat(foundObject)
			.isNotNull()
			.hasSize(5);
		logger.trace("End check that only second 10 objects that are in circle return");
		
		logger.info("End test get second 10 objects inside the circle that center is in 0 and radius is 0 out of 15 objects that are in circle and 5 are not");
	}
}
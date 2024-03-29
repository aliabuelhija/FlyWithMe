package superapp.rest_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.UsersServiceExtend;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

@RestController
public class UserAPIController {
	private UsersServiceExtend userService;

	@Autowired
	public UserAPIController(UsersServiceExtend userService) {
		this.userService = userService;
	}

// Create user
	@RequestMapping(
			path = { "/superapp/users" }, 
			method = { RequestMethod.POST }, 
			produces = { MediaType.APPLICATION_JSON_VALUE }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary createUser(@RequestBody NewUserBoundary newUserBoundry) {
		
		return userService.createUser(newUserBoundry);
	}

// Update user
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/superapp/users/{superapp}/{email}", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(
			@PathVariable("superapp") String superapp, 
			@PathVariable("email") String email,
			@RequestBody UserBoundary userBoundary) {
		
		userService.updateUser(superapp, email, userBoundary);
	}
	
// Login
	@RequestMapping(
			path = { "/superapp/users/login/{superapp}/{email}" }, 
			method = { RequestMethod.GET }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary login(
			@PathVariable("superapp") String superapp, 
			@PathVariable("email") String email) {
		
		return userService.login(superapp, email);
	}
}
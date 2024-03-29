package superapp.rest_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.data.DefaultValues;
import superapp.logic.MiniAppCommandsServiceExtend;
import superapp.logic.ObjectsServiceExtend;
import superapp.logic.UsersServiceExtend;
import superapp.logic.mini_app.MiniAppCommandBoundary;
import superapp.logic.users.UserBoundary;

@RestController
public class AdminAPIController {
	private UsersServiceExtend userService;
	private MiniAppCommandsServiceExtend miniAppCommandsService;
	private ObjectsServiceExtend ObjectsService;

	@Autowired
	public AdminAPIController(UsersServiceExtend userService, MiniAppCommandsServiceExtend miniAppCommandsService,
			ObjectsServiceExtend ObjectsService) {
		
		this.userService = userService;
		this.miniAppCommandsService = miniAppCommandsService;
		this.ObjectsService = ObjectsService;
	}

// Delete all users
	@RequestMapping(
			path = { "/superapp/admin/users" }, 
			method = { RequestMethod.DELETE })
	public void deleteAllUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		
		userService.deleteAllUsers(userSuperapp, userEmail);
	}

// Delete all objects
	@RequestMapping(path = { "/superapp/admin/objects" }, method = { RequestMethod.DELETE })
	public void deleteAllObjects(@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		
		ObjectsService.deleteAllObjects(userSuperapp, userEmail);
	}

// Delete all commands
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.DELETE })
	public void deleteAllCommands(@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		
		miniAppCommandsService.deleteAllCommands(userSuperapp, userEmail);
	}

// Get all users
	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] getAllUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
		
		return userService.getAllUsers(userSuperapp, userEmail, size, page).toArray(new UserBoundary[0]);
	}

// Get all commands
	@RequestMapping(
			path = { "/superapp/admin/miniapp" }, 
			method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public MiniAppCommandBoundary[] getAllCommands(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
		
		return miniAppCommandsService.getAllCommands(userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
	}

// Get all miniapp commands
	@RequestMapping(
			path = { "/superapp/admin/miniapp/{miniAppName}" }, 
			method = { RequestMethod.GET }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public MiniAppCommandBoundary[] getAllMiniAppCommands(
			@PathVariable("miniAppName") String miniapp,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
			
		return miniAppCommandsService.getAllMiniAppCommands(miniapp, userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
	}
}
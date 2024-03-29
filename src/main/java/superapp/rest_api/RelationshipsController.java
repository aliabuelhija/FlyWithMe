package superapp.rest_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.ObjectsServiceExtend;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.SuperAppObjectIdBoundary;

@RestController
public class RelationshipsController {
	private ObjectsServiceExtend objectsService;
	private final String SIZE_DEFULT = "10";
	private final String PAGE_DEFULT = "0";
	
	@Autowired
	public RelationshipsController(ObjectsServiceExtend objectsService) {
		this.objectsService = objectsService;
	}

// Bind object to child
	@RequestMapping(
			method = RequestMethod.PUT, 
			path = "/superapp/objects/{superapp}/{InternalObjectId}/children", 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void bindObjectToChild(
			@RequestBody SuperAppObjectIdBoundary superAppObjectIdBoundary,
			@PathVariable("superapp") String superapp, 
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {

		objectsService.bindObjectToChild(superapp, internalObjectId, superAppObjectIdBoundary, userSuperapp, userEmail);
	}

// Get all childrens
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects/{superapp}/{InternalObjectId}/children", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary[] getAllChildrens(
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = SIZE_DEFULT) int size,
			@RequestParam(name = "page", required = false, defaultValue = PAGE_DEFULT) int page) {
		
		return objectsService.getChildrens(superapp + "#" + internalObjectId, userSuperapp, userEmail, size, page).toArray(new ObjectBoundary[0]);
	}

// Get all parents
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects/{superapp}/{InternalObjectId}/parents", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary[] getAllParents(
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = SIZE_DEFULT) int size,
			@RequestParam(name = "page", required = false, defaultValue = PAGE_DEFULT) int page) {
			
		return objectsService.getParents(superapp + "#" + internalObjectId, userSuperapp, userEmail, size, page).toArray(new ObjectBoundary[0]);
	}
}
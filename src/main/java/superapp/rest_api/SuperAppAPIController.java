package superapp.rest_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.data.DefaultValues;
import superapp.logic.ObjectsServiceExtend;
import superapp.logic.exceptions‬.BadRequestException;
import superapp.logic.objects.ObjectBoundary;

@RestController
public class SuperAppAPIController {
	private ObjectsServiceExtend objectsService;

	@Autowired
	public SuperAppAPIController(ObjectsServiceExtend objectsService) {
		this.objectsService = objectsService;
	}

// Create object
	@RequestMapping(
			path = { "/superapp/objects" }, 
			method = { RequestMethod.POST }, 
			produces = { MediaType.APPLICATION_JSON_VALUE }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary createObject(@RequestBody ObjectBoundary objectBoundary) {

		return objectsService.createObject(objectBoundary);
	}

// Update object
	@RequestMapping(
			path = { "/superapp/objects/{superapp}/{internalObjectId}" }, 
			method = { RequestMethod.PUT }, 
			produces = { MediaType.APPLICATION_JSON_VALUE }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateObject(
			@PathVariable("superapp") String superApp,
			@PathVariable("internalObjectId") String objectId, 
			@RequestBody ObjectBoundary object,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		
		objectsService.updateObject(superApp, objectId, object, userSuperapp, userEmail);
	}

// Get specific object
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects/{superapp}/{internalObjectId}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary getSpecificObject(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String objectId,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {

		return objectsService.getSpecificObject(superapp, objectId, userSuperapp, userEmail);
	}

// Get all objects
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary[] getAllObjects(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
		
		return objectsService.getAllObjects(userSuperapp, userEmail, size, page).toArray(new ObjectBoundary[0]);
	}

// Get objects by type
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects/search/byType/{type}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary[] getObjectsByType(@PathVariable("type") String type,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {

		return objectsService.getObjectsByType(userSuperapp, userEmail, type, size, page)
				.toArray(new ObjectBoundary[0]);
	}

// Get objects by alias
	@RequestMapping(
			method = RequestMethod.GET, 
			path = "/superapp/objects/search/byAlias/{alias}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ObjectBoundary[] getObjectsByAlias(
			@PathVariable("alias") String alias,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {

		return objectsService.getObjectsByAlias(userSuperapp, userEmail, alias, size, page)
				.toArray(new ObjectBoundary[0]);
	}

// Get objects by location square search
	@RequestMapping(
			path = { "/superapp/objects/search/byLocation/{lat}/{lng}/{distance}" }, 
			method = { RequestMethod.GET }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getObjectsByLocationSquareSearch(
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng, 
			@PathVariable("distance") double distance,
			@RequestParam(name = "units", defaultValue = "NEUTRAL") String distanceUnits,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
		
		double distanceInMeters;
		if (distanceUnits.equalsIgnoreCase(DefaultValues.KM))
			distanceInMeters = distance * 1000; // Convert kilometers to meters
		else if (distanceUnits.equalsIgnoreCase(DefaultValues.MIL))
			distanceInMeters = distance * 1609.34; // Convert miles to meters
		else if (distanceUnits.equalsIgnoreCase("NEUTRAL"))
			distanceInMeters = distance; // Regular
		else
			throw new BadRequestException("Not valid units"); // Not valid unit

		return objectsService.getObjectsByLocationSquareSearch(userSuperapp, userEmail, lat, lng,
				distanceInMeters, size, page).toArray(new ObjectBoundary[0]);
	}

// Get objects by location circle search
	@RequestMapping(
			path = { "/superapp/objects/search/byCircle/{lat}/{lng}/{distance}" }, 
			method = { RequestMethod.GET }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getObjectsByLocationCircleSearch(
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng, 
			@PathVariable("distance") double distance,
			@RequestParam(name = "units", defaultValue = "NEUTRAL") String distanceUnits,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(name = "page", required = false, defaultValue = DefaultValues.DEFAULT_PAGE_NUMBER) int page) {
		
		double distanceInMeters;
		if (distanceUnits.equalsIgnoreCase(DefaultValues.KM))
			distanceInMeters = distance * 1000; // Convert kilometers to meters
		else if (distanceUnits.equalsIgnoreCase(DefaultValues.MIL))
			distanceInMeters = distance * 1609.34; // Convert miles to meters
		else if (distanceUnits.equalsIgnoreCase("NEUTRAL"))
			distanceInMeters = distance; // Regular
		else
			throw new BadRequestException("Not valid units"); // Not valid unit
		
		return objectsService.getObjectsByLocationCircleSearch(userSuperapp, userEmail, lng, lat,
				distanceInMeters, size, page).toArray(new ObjectBoundary[0]);
	}
}
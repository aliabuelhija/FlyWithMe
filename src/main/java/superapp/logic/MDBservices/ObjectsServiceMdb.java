package superapp.logic.MDBservices;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import superapp.dal.ObjectsCrud;
import superapp.data.DefaultValues;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserRole;
import superapp.logic.ObjectsServiceExtend;
import superapp.logic.converters.ObjectsConvertor;
import superapp.logic.exceptions‬.ObjectNotFoundException;
import superapp.logic.exceptions‬.BadRequestException;
import superapp.logic.exceptions‬.DeprecatedOperationException;
import superapp.logic.objects.CreatedBy;
import superapp.logic.objects.Location;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.ObjectId;
import superapp.logic.objects.SuperAppObjectIdBoundary;

@Service
public class ObjectsServiceMdb implements ObjectsServiceExtend {
	private ObjectsCrud objectsCrud;
	private ObjectsConvertor objectsConvertor;
	private UserServiceMdb userServiceMdb;
	private String superAppName;
	private JmsTemplate jmsTemplate;
	private Log logger = LogFactory.getLog(ObjectsServiceMdb.class);

	@Autowired
	public ObjectsServiceMdb(ObjectsCrud objectsCrud, UserServiceMdb userServiceMdb, ObjectsConvertor objectsConvertor, JmsTemplate jmsTemplate) {
		this.objectsCrud = objectsCrud;
		this.userServiceMdb = userServiceMdb;
		this.objectsConvertor = objectsConvertor;
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(3000L);
	}

	@Value("${spring.application.name}")
	public void setSuperAppName(String superAppName) {
		this.superAppName = superAppName;
	}
	
// Create object
	@Override
	public ObjectBoundary createObject(ObjectBoundary object) {
		logger.info("Start create object");
		logger.trace("Start checking the fields for object are valid");
		if (!CheckFields.checkFieldsToCreateObject(object)) {
			logger.error("The fields for object are not valid");
			throw new BadRequestException("The fields for object are not valid");
		}
		logger.trace("End checking the fields for object are valid");
		
		if(!CheckFields.checkIfUserRoleIsValid(object.getCreatedBy().getUserId().getSuperapp(), object.getCreatedBy().getUserId().getEmail(), UserRole.SUPERAPP_USER)) {
			logger.error("User is not superapp user");
			throw new BadRequestException("Only superapp user can create object");
		}
		
		logger.trace("Fill objectid, active, location, creationTimestamp and createdby");
		object.setObjectId(new ObjectId(superAppName, UUID.randomUUID().toString())); // Set object id
		if (object.getActive() == null) // Default: active = true
			object.setActive(true);
		if (object.getLocation() == null) // Default: 0, 0
			object.setLocation(new Location(0, 0));
		object.setCreationTimestamp(new Date()); // Set creation time is now
		object.getCreatedBy().getUserId().setSuperapp(superAppName); // Set superapp
		
		logger.trace("Convert boundary to entity");
		SuperAppObjectEntity entity = objectsConvertor.toEntity(object);
		logger.trace("Saving the entity to db");
		entity = objectsCrud.save(entity);
		logger.info("End create object");
		return objectsConvertor.toBoundary(entity);
	}

// Update object - old
	@Override
	@Deprecated
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update) {
		logger.error("Start update object - Deprecated");
		throw new DeprecatedOperationException();
	}

// Update object - new
	@Override
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update, String userSuperapp, String userEmail) {
		logger.info("Start update object");
		if(!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) {
			logger.error("User is not superapp user");
			throw new BadRequestException("Only superapp user can update object");
		}
		
		SuperAppObjectEntity entity = objectsConvertor
				.toEntity(getObjectBoundaryById(objectSuperApp + '#' + internalObjectId));

		// Update type
		if (update.getType() != null && !update.getType().isBlank())
			entity.setType(update.getType());

		// Update alias
		if (update.getAlias() != null && !update.getAlias().isBlank())
			entity.setAlias(update.getAlias());

		// Update active
		if (update.getActive() != null)
			entity.setActive(update.getActive());

		// Update location
		if (update.getLocation() != null) {
			if (update.getLocation().getLat() != 0.0)
				entity.setLat(update.getLocation().getLat());
			if (update.getLocation().getLng() != 0.0)
				entity.setLng(update.getLocation().getLng());
		}

		// Update object details
		if (update.getObjectDetails() != null)
			entity.setObjectDetails(update.getObjectDetails());
		
		logger.trace("Save the update object");
		entity = objectsCrud.save(entity);
		logger.info("End update object");
		return objectsConvertor.toBoundary(entity);
	}

// Get specific object - old
	@Override
	@Deprecated
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {
		logger.error("Start get specific object - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get specific object - new
	@Override
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp, String userEmail) {
		logger.info("Start get specific object");
		if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
			logger.info("End get specific object");
			return getObjectBoundaryById(objectSuperApp + '#' + internalObjectId);
		}
		else if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
			if(getObjectBoundaryById(objectSuperApp + '#' + internalObjectId).getActive()) { // Active
				logger.info("End get specific object");
				return getObjectBoundaryById(objectSuperApp + '#' + internalObjectId);
			}
			else {
				logger.error("Miniapp user tried to get un active object");
				throw new BadRequestException("Miniapp user can get only active object"); // Not active
			}
		}
		logger.error("User is not superapp user or miniapp user");
		throw new BadRequestException("Only superapp user and miniapp user can get specific object");
	}

// Get all objects - old
	@Override
	@Deprecated
	public List<ObjectBoundary> getAllObjects() {
		logger.error("Start get all objects - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all objects - new
	@Override
	public List<ObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		logger.info("Start get all objects");
		if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
			logger.info("End get all objects");
			return this.objectsCrud.findAll(PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		else if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
			logger.info("End get all objects");
			return this.objectsCrud.findAllByActiveTrue(PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		logger.error("User is not superapp user or miniapp user");
		throw new BadRequestException("Only superapp user and miniapp user can get all objects");
	}

// Get objects by type
	@Override
	public List<ObjectBoundary> getObjectsByType(String userSuperapp, String userEmail, String type, int size, int page) {
		logger.info("Start get all objects by type");
	    if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user 
	    	logger.info("End get all objects by type");
	    	return objectsCrud.findAllByType(type, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
	                .stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
	    	logger.info("End get all objects by type");
	    	return objectsCrud.findAllByTypeAndActiveTrue(type, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
	                .stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    logger.error("User is not superapp user or miniapp user");
	    throw new BadRequestException("Only superapp user and miniapp user can get objects by type"); 
	}
		
// Get objects by alias
	@Override
	public List<ObjectBoundary> getObjectsByAlias(String userSuperapp, String userEmail, String alias, int size, int page) {
		logger.info("Start get all objects by alias");
	    if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
	    	logger.info("End get all objects by alias");
	    	return objectsCrud.findAllByAlias(alias, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
	                .stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
	    	logger.info("End get all objects by alias");
	    	return objectsCrud.findAllByAliasAndActiveTrue(alias, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
	                .stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    logger.error("User is not superapp user or miniapp user");
	    throw new BadRequestException("Only superapp user and miniapp user can get objects by alias");
	}
	
// Get objects by location square search
	@Override
	public List<ObjectBoundary> getObjectsByLocationSquareSearch(String userSuperapp, String userEmail, double lat,
			double lng, double distance, int size, int page) {
		logger.info("Start get all objects by location square");
	    if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
	    	logger.info("End get all objects by location squere");
	    	return objectsCrud.findAllByLatBetweenAndLngBetween(lat - distance, lat + distance, lng - distance, lng + distance,
					PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
	    	logger.info("End get all objects by location squere");
	    	return objectsCrud.findAllByLatBetweenAndLngBetweenAndActive(lat - distance, lat + distance, lng - distance, lng + distance,
	        		true,PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
	    }
	    logger.error("User is not superapp user or miniapp user");
	    throw new BadRequestException("Only superapp user and miniapp user can get objects by location square");		
	}

// Get objects by location circle search
	@Override
	public List<ObjectBoundary> getObjectsByLocationCircleSearch(String userSuperapp, String userEmail, double lng, double lat,
			double distance, int size, int page) {
		logger.info("Start get all objects by location circle");
		if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
			logger.info("End get all objects by location circle");
			return objectsCrud.findObjectsByRadius(lat, lng, distance, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (only active object)
			logger.info("End get all objects by location circle");
			return objectsCrud.findObjectsByRadiusAndActive(lat, lng, distance, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		logger.error("User is not superapp user or miniapp user");
	    throw new BadRequestException("Only superapp user and miniapp user can get objects by location circle");
	}
	
// Bind object to child - old
	@Override
	@Deprecated
	public void bindObjectToChild(String superApp, String InternalObjectId, SuperAppObjectIdBoundary superAppObjectIdBoundary) {
		logger.error("Start bind object to child - Deprecated");
		throw new DeprecatedOperationException();
	}
	
// Bind object to child - new
	@Override
	public void bindObjectToChild(String superApp, String InternalObjectId, SuperAppObjectIdBoundary superAppObjectIdBoundary, String userSuperapp, String userEmail) {
		logger.info("Start bind object to child");
		if (!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Only superapp user can bind objects
			logger.error("User is not superapp user");
			throw new BadRequestException("Only superapp user can bind objects");
		}
		logger.trace("Find parent and child");
		String parentId = superApp + "#" + InternalObjectId;
		String childId = superAppObjectIdBoundary.getSuperapp() + "#" + superAppObjectIdBoundary.getInternalObjectId();
		SuperAppObjectEntity parent = this.objectsCrud.findById(parentId) // Find parent object
				.orElseThrow(() -> new ObjectNotFoundException("could not find object parent by id")); // Not find parent object
		SuperAppObjectEntity child = this.objectsCrud.findById(childId) // Find child object
				.orElseThrow(() -> new ObjectNotFoundException("could not find object child by id")); // Not find child object
		logger.trace("Add parent to parents list and child childrens list");
		child.getObjectParents().add(parent); // Add the parent to child parents list
		parent.getObjectChildrens().add(child); // Add the child to parent childs list
		logger.trace("Save child and parent after update");
		objectsCrud.save(child); // Save the data of child
		objectsCrud.save(parent); // Save the data of parent
		logger.info("End bind object to child");
	}
	
// Get all childrens - old
	@Override
	@Deprecated
	public List<ObjectBoundary> getChildrens(String objParent) {
		logger.error("Start get childrens - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all childrens - new
	@Override
	public List<ObjectBoundary> getChildrens(String objParent, String userSuperapp, String userEmail, int size,	int page) {
		logger.info("Start get childrens");
		if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
			logger.info("End get childrens");
			return objectsCrud.findAllByObjectParents_ObjectId(objParent,PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (object must be active)
			logger.info("End get childrens");
			return objectsCrud.findAllByObjectParents_ObjectIdAndActive(objParent,true,PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		logger.error("User is not superapp user or miniapp user");
		throw new BadRequestException("Only superapp and miniapp users can get childrens");
	}

// Get all parents - old
	@Override
	@Deprecated
	public List<ObjectBoundary> getParents(String objChild) {
		logger.error("Start get parents - Deprecated");
		throw new DeprecatedOperationException();
	}

// Get all parents - new
	@Override
	public List<ObjectBoundary> getParents(String objChild, String userSuperapp, String userEmail, int size, int page) {
		logger.info("Start get parents");
		if(CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.SUPERAPP_USER)) { // Superapp user
			logger.info("End get parents");
			return objectsCrud.findAllByObjectChildrens_ObjectId(objChild, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		else if (CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.MINIAPP_USER)) { // Miniapp user (object must be active)
			logger.info("End get parents");
			return objectsCrud.findAllByObjectChildrens_ObjectIdAndActive(objChild, true, PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConvertor::toBoundary).collect(Collectors.toList());
		}
		logger.error("User is not superapp user or miniapp user");
		throw new BadRequestException("Only superapp and miniapp users can get parents");
	}	
	
// Delete all objects - old
	@Override
	@Deprecated
	public void deleteAllObjects() {
		logger.error("Start delete all objects - Deprecated");
		throw new DeprecatedOperationException();
	}

// Delete all objects - new
	@Override
	public void deleteAllObjects(String userSuperapp, String userEmail) {
		logger.info("Start delete all objects");
		if(!CheckFields.checkIfUserRoleIsValid(userSuperapp, userEmail, UserRole.ADMIN)) {
			logger.error("User is not admin");
			throw new BadRequestException("Only admin can delete all objects");
		}
		objectsCrud.deleteAll();
		logger.info("End delete all objects");
	}
	
// Get object boundary by id
	public ObjectBoundary getObjectBoundaryById(String id) {
		logger.info("Start get object by id");
		Optional<SuperAppObjectEntity> op = this.objectsCrud.findById(id);
		if (op.isPresent()) {
			SuperAppObjectEntity entity = op.get();
			logger.info("End get object by id");
			return objectsConvertor.toBoundary(entity);
		} 
		else {
			logger.error("Object not found");
			throw new ObjectNotFoundException("Object not found");
		}
	}
	
// Insert each Json in JsonArray as object
	public String insertEachJsonInJsonArrayOfHotelsAsObject(String returnStringJsonFromHotelsRequest) {
		if(returnStringJsonFromHotelsRequest == null || returnStringJsonFromHotelsRequest.isEmpty())
			return returnStringJsonFromHotelsRequest;
		logger.info("Start analyzing returned string");
		
		logger.trace("Start convert string to json");
		JSONObject returnJsonFromHotelsRequest = new JSONObject(returnStringJsonFromHotelsRequest);
		logger.trace("End convert string to json");
		
		logger.trace("Start checking if json has general data field");
		if(returnJsonFromHotelsRequest == null || returnJsonFromHotelsRequest.get("data") == null)
			return returnStringJsonFromHotelsRequest;
		logger.trace("End checking if json has general data field");
		
		JSONObject jsonObjectOfGeneralData = new JSONObject(returnJsonFromHotelsRequest.get("data").toString());
		
		logger.trace("Start checking if general data field has hotels data field");
		if(jsonObjectOfGeneralData == null || jsonObjectOfGeneralData.getJSONArray("data") == null)
			return returnStringJsonFromHotelsRequest;
		logger.trace("End checking if general data field has hotels data field");
		
		JSONArray jsonArrayOfHotels = jsonObjectOfGeneralData.getJSONArray("data");
		
		logger.trace("Start moving on every hotel and create him");
		for (int jsonObjNo = 0; jsonObjNo < jsonArrayOfHotels.length(); jsonObjNo++) {
		    JSONObject jsonobject = jsonArrayOfHotels.getJSONObject(jsonObjNo);
		    String objectType = "Hotel";
		    String objectAlias = jsonobject.get("title").toString();
		    CreatedBy objectCreatedBy = new CreatedBy(userServiceMdb.login(superAppName, DefaultValues.DEFAULT_SUPERAPP_USER_EMAIL).getUserId());
		    Map<String, Object> objectDetails = new HashMap<String, Object>();
		    objectDetails.put("data", jsonobject);
		    createObject(new ObjectBoundary(null, objectType, objectAlias, true, null, null, objectCreatedBy, objectDetails));
		}
		logger.trace("End moving on every hotel and create him");
		
		logger.info("End analyzing returned string");
		return returnStringJsonFromHotelsRequest;
	}
}
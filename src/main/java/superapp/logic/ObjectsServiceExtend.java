package superapp.logic;

import java.util.List;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.SuperAppObjectIdBoundary;

public interface ObjectsServiceExtend extends ObjectsService {
	
// Update object - new
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update, String userSuperapp, String userEmail);
	
// Get specific object - new
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp, String userEmail);
	
// Get all objects - new
	public List<ObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page);
	
// Get objects by type
	public List<ObjectBoundary> getObjectsByType(String userSuperapp, String userEmail, String type, int size, int page);
	
// Get objects by alias
	public List<ObjectBoundary> getObjectsByAlias(String userSuperapp, String userEmail, String alias, int size, int page);
	
// Get objects by location square search
	public List<ObjectBoundary> getObjectsByLocationSquareSearch(String userSuperapp, String userEmail, double lat, double lng, double distance, int size, int page);
	
// Get objects by location circle search
	public List<ObjectBoundary> getObjectsByLocationCircleSearch(String userSuperapp, String userEmail, double lng, double lat, double distance, int size, int page);
	
// Bind object to child - old
	@Deprecated
	public void bindObjectToChild(String superApp, String InternalObjectId,SuperAppObjectIdBoundary superAppObjectIdBoundary);
	
// Bind object to child - new
	public void bindObjectToChild(String superApp, String InternalObjectId, SuperAppObjectIdBoundary superAppObjectIdBoundary, String userSuperapp, String userEmail);
	
// Get all childrens - old
	@Deprecated
	public List<ObjectBoundary> getChildrens(String objParent);
	
// Get all childrens - new
	public List<ObjectBoundary> getChildrens(String objParent, String userSuperapp, String userEmail, int size, int page);
	
// Get all parents - old
	@Deprecated
	public List<ObjectBoundary> getParents(String objChild);
	
// Get all parents - new
	public List<ObjectBoundary> getParents(String objChild, String userSuperapp, String userEmail, int size, int page);
	
// Delete all objects - new
	public void deleteAllObjects(String userSuperapp, String userEmail);
}
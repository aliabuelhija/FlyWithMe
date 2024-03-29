package superapp.logic;

import java.util.List;
import superapp.logic.objects.ObjectBoundary;

public interface ObjectsService {

// Create object
	public ObjectBoundary createObject(ObjectBoundary object);
	
// Update object - old
	@Deprecated
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update);
	
// Get specific object - old
	@Deprecated
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId);
	
// Get all objects - old
	@Deprecated
	public List<ObjectBoundary> getAllObjects();
	
// Delete all objects - old 
	@Deprecated
	public void deleteAllObjects();
}
package superapp.logic.mini_app;

import superapp.logic.objects.ObjectId;

public class TargetObject {
	protected ObjectId objectId;

// Constructors
	public TargetObject() {
		this.objectId=new ObjectId();
	}

	public TargetObject(ObjectId objectId) {
		this.objectId = objectId;
	}
	
// Gets
	public ObjectId getObjectId() {
		return objectId;
	}

// Sets
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}
}
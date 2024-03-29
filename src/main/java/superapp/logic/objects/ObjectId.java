package superapp.logic.objects;

public class ObjectId {

	private String superapp;
	private String internalObjectId;

// Constructors
	public ObjectId() {

	}

	public ObjectId(String superapp, String internalObjectId) {
		this.superapp = superapp;
		this.internalObjectId = internalObjectId;
	}

// Gets
	public String getSuperapp() {
		return superapp;
	}

	public String getInternalObjectId() {
		return internalObjectId;
	}

// Sets
	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
// To string
	@Override
	public String toString() {
		return "[superapp=" + superapp + ", internalObjectId=" + internalObjectId + "]";
	}
}
package superapp.logic.objects;

public class SuperAppObjectIdBoundary {
	protected String superapp;
	protected String internalObjectId;
	
// Constructors
	public SuperAppObjectIdBoundary() {
		super();
	}
	
	public SuperAppObjectIdBoundary(String superapp, String internalObjectId) {
		super();
		this.superapp = superapp;
		this.internalObjectId = internalObjectId;
	}

//Gets
	public String getSuperapp() {
		return superapp;
	}
	
	public String getInternalObjectId() {
		return internalObjectId;
	}
	
// Sets
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}
}
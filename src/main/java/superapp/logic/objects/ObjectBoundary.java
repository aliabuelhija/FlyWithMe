package superapp.logic.objects;

import java.util.Date;
import java.util.Map;

public class ObjectBoundary {

	private ObjectId objectId;
	private String type;
	private String alias;
	private Boolean active;
	private Date creationTimestamp;
	private Location location;
	private CreatedBy createdBy;
	private Map<String, Object> objectDetails;

// Constructors
	public ObjectBoundary() {
		this.objectId=new ObjectId();
		this.location=new Location();
		this.createdBy=new CreatedBy();

	}

	public ObjectBoundary(ObjectId objectId, String type, String alias, Boolean active, Date creationTimesTamp,
			Location location, CreatedBy createdBy, Map<String, Object> objectDetails) {
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.active = active;
		this.creationTimestamp = creationTimesTamp;
		this.location = location;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
	}

// Gets
	public ObjectId getObjectId() {
		return objectId;
	}

	public String getType() {
		return type;
	}

	public String getAlias() {
		return alias;
	}

	public Boolean getActive() {
		return active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public Location getLocation() {
		return location;
	}

	public CreatedBy getCreatedBy() {
		return createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

// Sets
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setCreationTimestamp(Date creationTimesTamp) {
		this.creationTimestamp = creationTimesTamp;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	
// To string
	@Override
	public String toString() {
		return "[objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
				+ ", creationTimestamp=" + creationTimestamp + ", location=" + location + ", createdBy=" + createdBy
				+ ", objectDetails=" + objectDetails + "]";
	}
}
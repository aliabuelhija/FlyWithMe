package superapp.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Document(collection = DefaultValues.OBJECTS_DATABA_NAME)

public class SuperAppObjectEntity {

	@Id
	private String objectId;
	private String type;
	private String alias;
	private boolean active;
	private Date creationTimestamp;
	private double lat;
	private double lng;
	@Field("location")
	private GeoJsonPoint location;
	private String createdBySuperapp;
	private String createdByEmail;
	@DBRef
	private Set<SuperAppObjectEntity> objectParents;
	@DBRef
	private Set<SuperAppObjectEntity> objectChildrens;
	private Map<String, Object> objectDetails;

// Constructors
	public SuperAppObjectEntity() {
        objectParents = new HashSet<>();
        objectChildrens = new HashSet<>();
        location = new GeoJsonPoint(0,0);
	}
	
// Gets
	public String getObjectId() {
		return objectId;
	}

	public String getType() {
		return type;
	}

	public String getAlias() {
		return alias;
	}

	public boolean getActive() {
		return active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
	
	public GeoJsonPoint getLocation() {
		return location;
	}

	public String getCreatedBySuperapp() {
		return createdBySuperapp;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}
	
	public Set<SuperAppObjectEntity> getObjectParents() {
		return objectParents;
	}
	
	public Set<SuperAppObjectEntity> getObjectChildrens() {
		return objectChildrens;
	}
	
	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

// Sets
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public void setLocation(GeoJsonPoint location) {
		this.location = location;
	}

	public void setCreatedBySuperapp(String createdBySuperapp) {
		this.createdBySuperapp = createdBySuperapp;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}
	
	public void setObjectParents(HashSet<SuperAppObjectEntity> objectParents) {
		this.objectParents = objectParents;
	}
	
	public void setoOjectChildrens(HashSet<SuperAppObjectEntity> objectChildrens) {
		this.objectChildrens = objectChildrens;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

// Override the equals method
	@Override
	public boolean equals(Object otherObj) {
		if (this == otherObj) // Same address
            return true;
		if (otherObj == null || getClass() != otherObj.getClass()) // Not same class
            return false;
		SuperAppObjectEntity otherEntity = (SuperAppObjectEntity)otherObj;
        return objectId.equals(otherEntity.objectId); // Compare the id to check if same (id is unique)
	}
	
// To String
	@Override
	public String toString() {
		return "SuperAppObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active="
				+ active + ", creationTimestamp=" + creationTimestamp + ", lat=" + lat + ", lng=" + lng
				+ "createdBySuperapp" + createdBySuperapp + "createdByEmail" + createdByEmail + "objectDetails"
				+ objectDetails + "]";
	}
}
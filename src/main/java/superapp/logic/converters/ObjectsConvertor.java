package superapp.logic.converters;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.objects.CreatedBy;
import superapp.logic.objects.Location;
import superapp.logic.objects.ObjectBoundary;
import superapp.logic.objects.ObjectId;
import superapp.logic.users.UserId;

@Component
public class ObjectsConvertor {

// Change object boundary to entity
	public SuperAppObjectEntity toEntity(ObjectBoundary objecBoundary) {
		SuperAppObjectEntity entity = new SuperAppObjectEntity();

		// ObjectId
		if (objecBoundary.getObjectId() != null && objecBoundary.getObjectId().getSuperapp() != null && objecBoundary.getObjectId().getInternalObjectId() != null)
			entity.setObjectId(objectIdToString(objecBoundary.getObjectId()));

		// Type
		if (objecBoundary.getType() != null)
			entity.setType(objecBoundary.getType());

		// Alias
		if (objecBoundary.getAlias() != null)
			entity.setAlias(objecBoundary.getAlias());

		// Active
		if (objecBoundary.getActive() != null)
			entity.setActive(objecBoundary.getActive());

		// CreationTimestamp
		if (objecBoundary.getCreationTimestamp() != null)
			entity.setCreationTimestamp(objecBoundary.getCreationTimestamp());

		// Location
		if (objecBoundary.getLocation() != null) {
			entity.setLat(objecBoundary.getLocation().getLat());
			entity.setLng(objecBoundary.getLocation().getLng());
			entity.setLocation(new GeoJsonPoint(entity.getLng(), entity.getLat()));
		}

		// CreatedBy
		if (objecBoundary.getCreatedBy() != null && objecBoundary.getCreatedBy().getUserId() != null) {
			if (objecBoundary.getCreatedBy().getUserId().getSuperapp() != null)
				entity.setCreatedBySuperapp(objecBoundary.getCreatedBy().getUserId().getSuperapp());
			
			if (objecBoundary.getCreatedBy().getUserId().getEmail() != null) {
				entity.setCreatedByEmail(objecBoundary.getCreatedBy().getUserId().getEmail());
			}	
		}

		// ObjectDetails
		if (objecBoundary.getObjectDetails() != null)
			entity.setObjectDetails(objecBoundary.getObjectDetails());

		return entity;
	}

// Change object entity to boundary
	public ObjectBoundary toBoundary(SuperAppObjectEntity entity) {
		ObjectBoundary objectBoundary = new ObjectBoundary();
		objectBoundary.setType(entity.getType());
		objectBoundary.setAlias(entity.getAlias());
		objectBoundary.setActive(entity.getActive());
		objectBoundary.setCreationTimestamp(entity.getCreationTimestamp());
		objectBoundary.setLocation(new Location(entity.getLng(), entity.getLat()));
		objectBoundary.setCreatedBy(new CreatedBy(new UserId(entity.getCreatedBySuperapp(), entity.getCreatedByEmail())));
		objectBoundary.setObjectId(objectIdtoBoundary(entity.getObjectId()));
		objectBoundary.setObjectDetails(entity.getObjectDetails());

		return objectBoundary;
	}

// ObjectId to string
	private String objectIdToString(ObjectId objectId) {
		return objectId.getSuperapp() + "#" + objectId.getInternalObjectId();
	}

// objectId to boundary
	public ObjectId objectIdtoBoundary(String entityId) {
		String[] id = entityId.split("#");
		return new ObjectId(id[0], id[1]);
	}	
}
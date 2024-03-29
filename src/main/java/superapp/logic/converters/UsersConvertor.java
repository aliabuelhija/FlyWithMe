package superapp.logic.converters;

import org.springframework.stereotype.Component;
import superapp.data.UserEntity;
import superapp.logic.users.UserBoundary;
import superapp.logic.users.UserId;

@Component
public class UsersConvertor {

// Change user boundary to entity
	public UserEntity toEntity(UserBoundary userBoundary) {
		UserEntity entity = new UserEntity();
		if (userBoundary.getUserId() != null && userBoundary.getUserId().getSuperapp() != null && userBoundary.getUserId().getEmail() != null) 
			entity.setUserId(userIdToString(userBoundary.getUserId()));
		
		if (userBoundary.getAvatar() != null) 
			entity.setAvatar(userBoundary.getAvatar());
		
		if (userBoundary.getUsername() != null) 
			entity.setUserName(userBoundary.getUsername());
		
		if (userBoundary.getRole() != null) 
			entity.setRole(userBoundary.getRole());
		
		return entity;
	}

// Change user entity to boundary
	public UserBoundary toBoundary(UserEntity entity) {
		UserBoundary userBoundary = new UserBoundary();
		userBoundary.setAvatar(entity.getAvatar());
		userBoundary.setUserId(userIdtoBoundary(entity.getUserId()));
		userBoundary.setUsername(entity.getUserName());
		userBoundary.setRole(entity.getRole());
		return userBoundary;
	}

// User id to string
	private String userIdToString(UserId userId) {
		return userId.getSuperapp() + "#" + userId.getEmail();
	}

// User id to boundary
	public UserId userIdtoBoundary(String entityId) {
		String[] id = entityId.split("#");
		return new UserId(id[0], id[1]);
	}	
}
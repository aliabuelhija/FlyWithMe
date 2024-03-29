package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = DefaultValues.USERS_DATABA_NAME)
public class UserEntity {
	@Id
	private String userId;
	private String username;
	private String avatar;
	private UserRole role;

// Constructors
	public UserEntity() {
	}

// Gets
	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return username;
	}

	public String getAvatar() {
		return avatar;
	}

	public UserRole getRole() {
		return role;
	}

// Sets
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

// To String
	@Override
	public String toString() {
		return "UserEntity [userId=" + userId + ", username=" + username + ", avatar=" + avatar + ", role=" + role
				+ "]";
	}
}
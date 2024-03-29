package superapp.logic.users;

import superapp.data.UserRole;

public class UserBoundary {
	private UserId userId;
	private UserRole role;
	private String username;
	private String avatar;

// Constructors
	public UserBoundary() {
		userId = new UserId();

	}

	public UserBoundary(String superApp, String email, UserRole role, String userName, String avatar) {
		userId = new UserId(superApp, email);
		this.avatar = avatar;
		this.username = userName;
		this.role = role;
	}

// Gets
	public UserId getUserId() {
		return userId;
	}

	public UserRole getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public String getAvatar() {
		return avatar;
	}

// Sets
	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
// To String
	@Override
	public String toString() {
		return "[userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}
}
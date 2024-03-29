package superapp.logic.users;

import superapp.data.UserRole;

public class NewUserBoundary {

	private String email;
	private UserRole role;
	private String username;
	private String avatar;

// Constructors
	public NewUserBoundary() {
	}
	
	public NewUserBoundary(String email, UserRole role, String username, String avatar) {
		this.email = email;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

// Gets
	public String getEmail() {
		return email;
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
	public void setEmail(String email) {
		this.email = email;
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
		return "[email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}
}
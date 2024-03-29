package superapp.logic.users;

public class UserId {

	private String superapp;
	private String email;

// Constructors
	public UserId() {
	}

	public UserId(String email) {
		this.email = email;
	}
	
	public UserId(String superapp, String email) {
		this.superapp = superapp;
		this.email = email;
	}

// Gets
	public String getSuperapp() {
		return superapp;
	}

	public String getEmail() {
		return email;
	}

// Sets
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}

	public void setEmail(String email) {
		this.email = email;
	}

// To String
	@Override
	public String toString() {
		return "[superapp=" + superapp + ", email=" + email + "]";
	}
}
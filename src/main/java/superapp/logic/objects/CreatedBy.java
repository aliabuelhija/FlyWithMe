package superapp.logic.objects;

import superapp.logic.users.UserId;

public class CreatedBy {

	private UserId userId;

// Constructors
	public CreatedBy() {
		this.userId = new UserId();
	}

	public CreatedBy(UserId userId) {
		this.userId = userId;
	}

// Gets
	public UserId getUserId() {
		return userId;
	}

// Sets
	public void setUserId(UserId userId) {
		this.userId = userId;
	}

// To String
	@Override
	public String toString() {
		return "[userID=" + userId + "]";
	}
}
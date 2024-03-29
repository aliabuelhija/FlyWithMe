package superapp.logic.mini_app;

import superapp.logic.users.UserId;

public class InvokedBy {
	protected UserId userId;

// Constructors
	public InvokedBy() {
		this.userId=new UserId();
	}
	
	public InvokedBy(UserId userId) {
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
}
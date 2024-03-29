package superapp.logic;

import java.util.List;
import superapp.logic.users.NewUserBoundary;
import superapp.logic.users.UserBoundary;

public interface UsersService {
// Create user
	public UserBoundary createUser(NewUserBoundary newUser);

// Update user
	public UserBoundary updateUser(String userApp, String userEmail, UserBoundary update);
	
// Login
	public UserBoundary login(String userSuperApp, String userEmail);

// Delete all users - old
	@Deprecated
	public void deleteAllUsers();

// Get all users - old
	@Deprecated
	public List<UserBoundary> getAllUsers();	
}
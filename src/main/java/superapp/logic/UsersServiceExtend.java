package superapp.logic;

import java.util.List;
import superapp.logic.users.UserBoundary;

public interface UsersServiceExtend extends UsersService {
	
// Delete all users - new
	public void deleteAllUsers(String userSuperapp, String userEmail);
	
// Get all users - new
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page);	
}
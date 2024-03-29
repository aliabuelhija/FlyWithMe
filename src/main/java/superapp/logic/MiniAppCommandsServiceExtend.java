package superapp.logic;

import java.util.List;
import superapp.logic.mini_app.MiniAppCommandBoundary;

public interface MiniAppCommandsServiceExtend extends MiniAppCommandsService {

// Invoke command - Async
	public Object asyncInvokeCommand(MiniAppCommandBoundary MiniAppCommandBoundary);
	
// Async operation
	public Object asyncOperation(MiniAppCommandBoundary miniAppCommandBoundary);
	
// Delete all commands - new
	public void deleteAllCommands(String userSuperapp, String userEmail);
	
// Get all commands - new
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page);
	
// Get all miniapp commands - new
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail, int size, int page);
}
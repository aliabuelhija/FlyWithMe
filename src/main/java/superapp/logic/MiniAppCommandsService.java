package superapp.logic;

import java.util.List;
import superapp.logic.mini_app.MiniAppCommandBoundary;

public interface MiniAppCommandsService {

// Invoke command - Not async
	public Object invokeCommand(MiniAppCommandBoundary MiniAppCommandBoundary);
	
// Delete all commands - old
	@Deprecated
	public void deleteAllCommands();
	
// Get all commands - old
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands();

// Get all miniapp commands - old
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
}
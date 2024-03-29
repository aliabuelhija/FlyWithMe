package superapp.rest_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import superapp.logic.MiniAppCommandsServiceExtend;
import superapp.logic.mini_app.CommandId;
import superapp.logic.mini_app.MiniAppCommandBoundary;

@RestController
public class MiniAppAPIController {
	private MiniAppCommandsServiceExtend miniAppCommandsService;

	@Autowired
	public MiniAppAPIController(MiniAppCommandsServiceExtend miniAppCommandsService) {
		this.miniAppCommandsService = miniAppCommandsService;
	}

// Invoke command
	@RequestMapping(
			path = { "/superapp/miniapp/{miniAppName}" }, 
			method = { RequestMethod.POST }, 
			produces = { MediaType.APPLICATION_JSON_VALUE }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	public Object invokeCommand(
			@PathVariable("miniAppName") String miniAppName,
			@RequestBody MiniAppCommandBoundary miniAppBoundary, 
			@RequestParam(name="async", defaultValue = "false") boolean asyncFlag) {
		CommandId commandId = new CommandId();
		miniAppBoundary.setCommandId(commandId);
		miniAppBoundary.getCommandId().setMiniApp(miniAppName);
		if(asyncFlag)
			return miniAppCommandsService.asyncInvokeCommand(miniAppBoundary);
		else
			return miniAppCommandsService.invokeCommand(miniAppBoundary);
	}
}
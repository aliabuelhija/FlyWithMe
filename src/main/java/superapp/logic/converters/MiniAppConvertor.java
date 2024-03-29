package superapp.logic.converters;

import org.springframework.stereotype.Component;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.mini_app.CommandId;
import superapp.logic.mini_app.MiniAppCommandBoundary;
import superapp.logic.mini_app.TargetObject;
import superapp.logic.objects.ObjectId;
import superapp.logic.users.UserId;

@Component
public class MiniAppConvertor {
	
// Change MiniApp command boundary to entity
	public MiniAppCommandEntity toEntity(MiniAppCommandBoundary boundary) {
		MiniAppCommandEntity entity = new MiniAppCommandEntity();
		entity.setCommandId(boundary.getCommandId().toString());
		entity.setCommand(boundary.getCommand().toString());

		// CommandId
		if (boundary.getCommandId() == null) {
			entity.setCommandId("");
		} else {
			String convertedCommandId = "";
			// Check every string in CommandId
			if (boundary.getCommandId().getInternalCommandId() != null) // InternalCommandID
				convertedCommandId += boundary.getCommandId().getInternalCommandId();
			convertedCommandId += "#";
			
			if (boundary.getCommandId().getMiniApp() != null) // MiniApp
					convertedCommandId += boundary.getCommandId().getMiniApp();
			convertedCommandId += "#";
			
			if (boundary.getCommandId().getSuperapp() != null) // SuperApp
					convertedCommandId += boundary.getCommandId().getSuperapp();
			entity.setCommandId(convertedCommandId);
		}

		// Command
		if (boundary.getCommand() == null)
			entity.setCommand("");
		else
			entity.setCommand(boundary.getCommand());

		// TargetObject
		if (boundary.getTargetObject() == null)
			entity.setTargetObject("");
		else {
			String convertedTargetObject = "";
			// Check every string in TargetObject
			if (boundary.getTargetObject().getObjectId().getInternalObjectId() != null) // InternalObjectId
				convertedTargetObject += boundary.getTargetObject().getObjectId().getInternalObjectId();
			convertedTargetObject += "#";
			
			if (boundary.getTargetObject().getObjectId().getSuperapp() != null) // superApp
				convertedTargetObject += boundary.getTargetObject().getObjectId().getSuperapp();
			
			entity.setTargetObject(convertedTargetObject);
		}

		// InvokedBy
		if (boundary.getInvokedBy() == null) {
			entity.setInvokedByEmail(" ");
			entity.setInvokedBySuperApp(" ");
		} else {
			// Check every string in InvokedBy
			if (boundary.getInvokedBy().getUserId().getEmail() != null) // Email
				entity.setInvokedByEmail(boundary.getInvokedBy().getUserId().getEmail());
			
			if (boundary.getInvokedBy().getUserId().getSuperapp() != null) // superApp
				entity.setInvokedBySuperApp(boundary.getInvokedBy().getUserId().getSuperapp());
		}

		// Date
		entity.setInvokcationTimestamp(boundary.getInvocationTimestamp());

		// Data
		entity.setCommandAttributed(boundary.getCommandAttributes());
		
		// MiniApp
		entity.setMiniApp(boundary.getCommandId().getMiniApp());

		return entity;
	}

// Change MiniApp command entity to boundary
	public MiniAppCommandBoundary toBoundary(MiniAppCommandEntity entity) {
		MiniAppCommandBoundary boundary = new MiniAppCommandBoundary();
		boundary.setCommandId(commandIdToBoundary(entity.getCommandId()));
		boundary.setCommand(entity.getCommand());
		boundary.setTargetObject(objectIdToBoundary(entity.getTargetObject()));
		boundary.setInvocationTimestamp(entity.getInvokcationTimestamp());
		boundary.getInvokedBy().getUserId().setEmail(entity.getInvokedByEmail());
		boundary.getInvokedBy().getUserId().setSuperapp(entity.getInvokedBySuperApp());
		boundary.setCommandAttributes(entity.getCommandAttributed());
		return boundary;
	}

// Command id to boundary
	public CommandId commandIdToBoundary(String entityCommand) {
		String[] id = entityCommand.split("#");
		return new CommandId(id[2], id[1], id[0]);
	}

// Object id to boundary
	public TargetObject objectIdToBoundary(String entityObjId) {
		String[] id = entityObjId.split("#");
		return new TargetObject(new ObjectId(id[1], id[0]));
	}

// User id to boundary
	public UserId userIdtoBoundary(String entityId) {
		String[] id = entityId.split("#");
		return new UserId(id[1], id[0]);
	}	
}
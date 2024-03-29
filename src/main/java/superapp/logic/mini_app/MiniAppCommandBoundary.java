package superapp.logic.mini_app;

import java.util.Date;
import java.util.Map;

public class MiniAppCommandBoundary {

	private CommandId commandId;
	private String command;
	private TargetObject targetObject;
	private Date invocationTimestamp;
	private InvokedBy invokedBy;
	private Map<String, Object> commandAttributes; // key-value

// Constructors
	public MiniAppCommandBoundary() {
		this.commandId = new CommandId();
		this.invokedBy = new InvokedBy();
	}
	
	public MiniAppCommandBoundary(String command, TargetObject targetObject, InvokedBy invokedBy)
	{
		this.command = command;
		this.targetObject = targetObject;
		this.invokedBy = invokedBy;
	} 
	
	public MiniAppCommandBoundary(CommandId commandId, String command, TargetObject targetObject,
			Date invocationTimestamp, InvokedBy invokedBy, Map<String, Object> commandAttributed) {
		super();
		this.commandId = commandId;
		this.command = command;
		this.targetObject = targetObject;
		this.invocationTimestamp = invocationTimestamp;
		this.invokedBy = invokedBy;
		this.commandAttributes = commandAttributed;
	}

// Gets
	public CommandId getCommandId() {
		return commandId;
	}

	public String getCommand() {
		return command;
	}

	public TargetObject getTargetObject() {
		return targetObject;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public InvokedBy getInvokedBy() {
		return invokedBy;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

// Sets
	public void setCommandId(CommandId commandId) {
		this.commandId = commandId;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setTargetObject(TargetObject targetObject) {
		this.targetObject = targetObject;
	}

	public void setInvocationTimestamp(Date invokcationTimeStap) {
		this.invocationTimestamp = invokcationTimeStap;
	}

	public void setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
	}

	public void setCommandAttributes(Map<String, Object> commandAttributed) {
		this.commandAttributes = commandAttributed;
	}
	
// To String
	@Override
	public String toString() {
		return "MiniAppCommandBoundary [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + "invocationTimestamp" + invocationTimestamp + "invokedBy" + invokedBy
				+ "commandAttributes" + commandAttributes + "]";
	}
}
package superapp.data;

import java.util.Date;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = DefaultValues.MINIAPP_COMMANDS_DATABA_NAME)
public class MiniAppCommandEntity {

	@Id
	private String commandId;
	private String miniApp;
	private String command;
	private String targetObject;
	private String invokedByEmail;
	private String invokedBySuperApp;
	private Date invokcationTimestamp;
	private Map<String, Object> commandAttributed;

// Constructors
	public MiniAppCommandEntity() {
	}

// Gets
	public String getCommandId() {
		return commandId;
	}
	
	public String getMiniApp() {
		return miniApp;
	}

	public String getCommand() {
		return command;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public String getInvokedByEmail() {
		return invokedByEmail;
	}

	public String getInvokedBySuperApp() {
		return invokedBySuperApp;
	}

	public Date getInvokcationTimestamp() {
		return invokcationTimestamp;
	}

// Sets
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	
	public void setMiniApp(String miniApp) {
		this.miniApp = miniApp;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public void setInvokedByEmail(String invokedByEmail) {
		this.invokedByEmail = invokedByEmail;
	}

	public void setInvokedBySuperApp(String invokedBySuperApp) {
		this.invokedBySuperApp = invokedBySuperApp;
	}

	public void setInvokcationTimestamp(Date invokcationTimestamp) {
		this.invokcationTimestamp = invokcationTimestamp;
	}

	public Map<String, Object> getCommandAttributed() {
		return commandAttributed;
	}

	public void setCommandAttributed(Map<String, Object> commandAttributed) {
		this.commandAttributed = commandAttributed;
	}

// To String
	@Override
	public String toString() {
		return "MiniAppCommandEntity [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invokedByEmail=" + invokedByEmail + ", invokedBySuperApp=" + invokedBySuperApp
				+ ", invokcationTimestamp=" + invokcationTimestamp + ", commandAttributed=" + commandAttributed + "]";
	}
}
package superapp.logic.mini_app;

public class CommandId {

	private String superapp;
	private String miniApp;
	private String internalCommandId;

// Constructors
	public CommandId() {
	}
	
	public CommandId(String miniapp) {
		this.miniApp = miniapp;
	}
	
	public CommandId(String superapp, String internalCommandId) {
		this.superapp = superapp;
		this.internalCommandId = internalCommandId;
	}
	
	public CommandId(String superapp, String miniApp, String internalCommandId) {
		this.superapp = superapp;
		this.miniApp = miniApp;
		this.internalCommandId = internalCommandId;
	}

// Gets
	public String getSuperapp() {
		return superapp;
	}

	public String getMiniApp() {
		return miniApp;
	}

	public String getInternalCommandId() {
		return internalCommandId;
	}

// Sets
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}

	public void setMiniApp(String miniApp) {
		this.miniApp = miniApp;
	}

	public void setInternalCommandId(String internalCommandId) {
		this.internalCommandId = internalCommandId;
	}

// To String
	@Override
	public String toString() {
		return "CommandId [superapp=" + superapp + ", miniApp=" + miniApp + ", internalCommandId=" + internalCommandId
				+ "]";
	}
}
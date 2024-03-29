package superapp.logic.exceptions‬;

public class ServerErrorException extends RuntimeException {

	private static final long serialVersionUID = 7432520543381603313L;

	public ServerErrorException() {
		super();
	}

	public ServerErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerErrorException(String message) {
		super(message);
	}

	public ServerErrorException(Throwable cause) {
		super(cause);
	}
}

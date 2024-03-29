package superapp.logic.exceptions‬;

public class ConflictException extends RuntimeException {
	
	private static final long serialVersionUID = 2253587256538674636L;

	public ConflictException() {
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}


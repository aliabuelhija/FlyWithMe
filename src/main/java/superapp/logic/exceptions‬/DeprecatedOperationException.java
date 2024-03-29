package superapp.logic.exceptions‬;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DeprecatedOperationException extends RuntimeException {
	
	private static final long serialVersionUID = -4846654301927485915L;

	public DeprecatedOperationException() {
	}

	public DeprecatedOperationException(String message) {
		super(message);
	}

	public DeprecatedOperationException(Throwable cause) {
		super(cause);
	}

	public DeprecatedOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
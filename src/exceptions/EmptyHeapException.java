package exceptions;

public class EmptyHeapException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public EmptyHeapException(String message) {
		super(message);
	}
}

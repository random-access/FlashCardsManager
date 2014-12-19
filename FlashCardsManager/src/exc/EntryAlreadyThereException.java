package exc;

@SuppressWarnings("serial")
public class EntryAlreadyThereException extends Exception {

	public EntryAlreadyThereException() {
	}

	public EntryAlreadyThereException(String message) {
		super(message);
	}

	public EntryAlreadyThereException(Throwable cause) {
		super(cause);
	}

	public EntryAlreadyThereException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntryAlreadyThereException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

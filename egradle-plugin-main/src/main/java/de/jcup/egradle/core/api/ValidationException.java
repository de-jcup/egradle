package de.jcup.egradle.core.api;

/**
 * Something did go wrong on validation...
 * 
 * @author Albert Tregnaghi
 *
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = -1838348397373426505L;
	private String _message;
	private String _details;

	public ValidationException(String message) {
		this(message,null);
	}
	
	public ValidationException(String message, String details) {
		super(message);
		this._message = message;
		this._details = details;
	}
	
	/**
	 * Returns details or <code>null</code>
	 * @return details
	 */
	public String getDetails() {
		return _details;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_message == null) ? 0 : _message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationException other = (ValidationException) obj;
		if (_details == null) {
			if (other._details != null)
				return false;
		} else if (!_details.equals(other._details))
			return false;
		if (_message == null) {
			if (other._message != null)
				return false;
		} else if (!_message.equals(other._message))
			return false;
		return true;
	}
	


}

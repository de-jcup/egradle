/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.core.util;

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
		this(message, null);
	}

	public ValidationException(String message, String details) {
		super(message);
		this._message = message;
		this._details = details;
	}

	/**
	 * Returns details or <code>null</code>
	 * 
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

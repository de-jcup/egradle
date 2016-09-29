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
 package de.jcup.egradle.core.api;

/**
 * Special exception. Can be used to throw so caller is informed about problem but the caller. But the problem is already logged or shown to user in UI. So caller has nothing more to do -just to forget
 * @author Albert Tregnaghi
 *
 */
public class ForgetMeRuntimeException extends RuntimeException implements ForgetMe{

	private static final long serialVersionUID = 1L;

	public ForgetMeRuntimeException(String message, Throwable alreadyHandledException) {
		super(message, alreadyHandledException);
	}
	public ForgetMeRuntimeException(String message) {
		super(message);
	}
	
	

	
}

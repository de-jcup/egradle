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
 package de.jcup.egradle.codeassist;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.jcup.egradle.core.util.ErrorHandler;

public class CodeCompletionRegistry {

	private Set<RegistryListener> listeners = new LinkedHashSet<>();
	private ErrorHandler errorHandler;

	private Map<Class<?>, Object> services = new HashMap<>();

	public CodeCompletionRegistry() {
	}

	private ErrorHandler safeGetErrorHandler() {
		if (errorHandler == null) {
			return ErrorHandler.IGNORE_ERRORS;
		}
		return errorHandler;
	}

	void addListener(RegistryListener listener) {
		if (listener == null) {
			return;
		}
		listeners.add(listener);
	}

	void removeListener(RegistryListener listener) {
		if (listener == null) {
			return;
		}
		listeners.remove(listener);
	}

	/**
	 * (Re-)Initialize registry and the code completion parts inside. Will inform all registry listeners about rebuild in correct ordered types
	 */
	public void init() {
		/* do not change ordering!*/
		fireRegistryEvent(RegistryEventType.DESTROY);
		fireRegistryEvent(RegistryEventType.LOAD_PLUGINS);
	}

	private void fireRegistryEvent(RegistryEventType type) {
		for (RegistryListener listener : listeners) {
			listener.onCodeCompletionEvent(new RegistryEventImpl(type));
		}
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public interface RegistryListener {
		public void onCodeCompletionEvent(RegistryEvent event);
	}

	public interface RegistryEvent {
		public CodeCompletionRegistry getRegistry();
		
		public ErrorHandler getErrorHandler();
		
		public RegistryEventType getType();
	}
	
	public enum RegistryEventType{
		/**
		 * Destroys existing data
		 */
		DESTROY,
		
		/**
		 * Load plugins and apply
		 */
		LOAD_PLUGINS
	}

	private class RegistryEventImpl implements RegistryEvent {

		private RegistryEventType type;
		
		private RegistryEventImpl(RegistryEventType type){
			this.type=type;
		}
		
		@Override
		public ErrorHandler getErrorHandler() {
			return safeGetErrorHandler();
		}

		@Override
		public RegistryEventType getType() {
			return type;
		}

		@Override
		public CodeCompletionRegistry getRegistry() {
			return CodeCompletionRegistry.this;
		}

	}

	/**
	 * Register a code completion service. If the service itself is a
	 * {@link RegistryListener} if will listen now. If there was a former
	 * service installed it will be deinstalled (if being a listener it will
	 * also be removed as listener as well). To simply deinstall a listener use
	 * new service with <code>null</code>
	 * 
	 * @param serviceClazz
	 * @param service
	 */
	public <T extends CodeCompletionService> void registerService(Class<T> serviceClazz, T service) {
		Object previous = services.put(serviceClazz, service);
		if (service instanceof RegistryListener) {
			RegistryListener l = (RegistryListener) service;
			addListener(l);
		}
		if (previous instanceof RegistryListener) {
			RegistryListener l = (RegistryListener) previous;
			removeListener(l);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends CodeCompletionService> T getService(Class<T> serviceClazz) {
		return (T) services.get(serviceClazz);
	}

}

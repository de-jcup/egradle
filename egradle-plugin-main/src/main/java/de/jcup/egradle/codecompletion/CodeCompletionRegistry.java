package de.jcup.egradle.codecompletion;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.jcup.egradle.core.api.ErrorHandler;

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

	public void addListener(RegistryListener listener) {
		if (listener == null) {
			return;
		}
		listeners.add(listener);
	}

	public void removeListener(RegistryListener listener) {
		if (listener == null) {
			return;
		}
		listeners.remove(listener);
	}

	/**
	 * Rebuilds repository. Will inform all registry listeners about rebuild
	 */
	public void rebuild() {
		for (RegistryListener listener : listeners) {
			listener.onRebuild(new RegistryEventImpl());
		}
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public interface RegistryListener {
		public void onRebuild(RegistryEvent event);
	}

	public interface RegistryEvent {
		public ErrorHandler getErrorHandler();
	}

	private class RegistryEventImpl implements RegistryEvent {

		@Override
		public ErrorHandler getErrorHandler() {
			return safeGetErrorHandler();
		}

	}

	/**
	 * Unregisters a service if existing
	 * 
	 * @param serviceClazz
	 */
	public <T extends CodeCompletionService> void unregisterService(Class<T> serviceClazz) {
		registerService(serviceClazz, null);
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
	public <T extends CodeCompletionService> void registerService(Class<T > serviceClazz, T service) {
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
	public <T> T getService(Class<T> serviceClazz) {
		return (T) services.get(serviceClazz);
	}
}

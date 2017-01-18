package de.jcup.egradle.core.codecompletion;

import java.util.LinkedHashSet;
import java.util.Set;

import de.jcup.egradle.core.api.ErrorHandler;

public class CodeCompletionRegistry {

	private Set<RegistryListener> listeners = new LinkedHashSet<>();
	private ErrorHandler errorHandler;
	
	public CodeCompletionRegistry(){
	}
	
	private ErrorHandler safeGetErrorHandler(){
		if (errorHandler==null){
			return ErrorHandler.IGNORE_ERRORS;
		}
		return errorHandler;
	}
	
	public void add(RegistryListener listener){
		if (listener==null){
			return;
		}
		listeners.add(listener);
	}
	
	public void remove(RegistryListener listener){
		if (listener==null){
			return;
		}
		listeners.remove(listener);
	}
	
	
	/**
	 * Rebuilds repository. Will inform all registry listeners about rebuild
	 */
	public void rebuild(){
		for (RegistryListener listener:listeners){
			listener.onRebuild(new RegistryEventImpl());
		}
	}


	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler=errorHandler;
	}

	public interface RegistryListener{
		public void onRebuild(RegistryEvent event);
	}

	public interface RegistryEvent{
		public ErrorHandler getErrorHandler();
	}

	private class RegistryEventImpl implements RegistryEvent{
	
		@Override
		public ErrorHandler getErrorHandler() {
			return safeGetErrorHandler();
		}
		
	}
}

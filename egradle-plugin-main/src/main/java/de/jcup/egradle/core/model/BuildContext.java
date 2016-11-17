package de.jcup.egradle.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildContext {
	
	private List<Error> errors = new ArrayList<>();
	
	public void add(Error error){
		if (error==null){
			return;
		}
		errors.add(error);
	}
	
	/**
	 * @return an unmodifiable list of outline errors, never <code>null</code>
	 */
	public List<Error> getErrors(){
		return Collections.unmodifiableList(errors);
	}

	public boolean hasErrors() {
		return errors.size()>0;
	}
	
}

package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.notNull;

public class GradleSubproject extends AbstractGradleProject{

	private String name;

	public GradleSubproject(String name){
		notNull(name);
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
}

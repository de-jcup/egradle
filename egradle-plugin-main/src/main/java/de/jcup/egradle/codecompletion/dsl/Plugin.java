package de.jcup.egradle.codecompletion.dsl;

import java.util.Set;

public interface Plugin {

	public String getId();

	public String getDescription();

	public Set<TypeExtension> getExtensions();
}

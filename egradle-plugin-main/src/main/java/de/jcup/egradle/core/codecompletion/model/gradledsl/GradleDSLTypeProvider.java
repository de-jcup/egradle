package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.jcup.egradle.core.codecompletion.model.Type;
import de.jcup.egradle.core.codecompletion.model.TypeProvider;

public class GradleDSLTypeProvider implements TypeProvider{
	/* FIXME ATR, 13.01.2017: make parts here abstract and rename test - the resolving is not gradle dsl specific*/
	private GradleDSLFileLoader fileLoader;
	private Map<String, Type> nameToTypeMapping;
	private Set<String> unresolveableNames;
	
	public GradleDSLTypeProvider(GradleDSLFileLoader loader){
		if (loader==null){
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader=loader;
		 nameToTypeMapping = new TreeMap<>();
		 unresolveableNames= new TreeSet<>();
	}
	
	@Override
	public Type getType(String name) {
		Type type = nameToTypeMapping.get(name);
		if (type==null){
			if (unresolveableNames.contains(name)){
				/* already tried to load*/
				return null;
			}
			/* try to load */
			type = fileLoader.load(name);
			if (type==null){
				unresolveableNames.add(name);
			}else{
				nameToTypeMapping.put(name, type);
			}
		}
		return type;
	}
	
}

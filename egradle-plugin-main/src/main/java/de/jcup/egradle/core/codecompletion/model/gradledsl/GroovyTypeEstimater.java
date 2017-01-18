package de.jcup.egradle.core.codecompletion.model.gradledsl;

import de.jcup.egradle.core.codecompletion.model.Type;
import de.jcup.egradle.core.codecompletion.model.TypeProvider;
import de.jcup.egradle.core.model.Item;

public class GroovyTypeEstimater implements TypeEstimater {
	/* FIXME ATR, 18.01.2017: implement */
	private TypeProvider typeProvider;
	
	public Type estimateFromGradleProjectAsRoot(Item item){
		return estimate(item, "org.gradle.api.Project");
	}
	
	public Type estimate(Item item, String rootItemType){
		Type type = typeProvider.getType(rootItemType);
		if (type==null){
			return null;
		}
		return null;
	}

	@Override
	public Type estimate(int offset) {
		// TODO Auto-generated method stub
		return null;
	}
}

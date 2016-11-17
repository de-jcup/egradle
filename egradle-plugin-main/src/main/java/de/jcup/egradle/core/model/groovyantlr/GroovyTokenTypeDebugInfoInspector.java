package de.jcup.egradle.core.model.groovyantlr;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;


/**
 * A simple reflection util which does create a map of names for all fields defined in {@link GroovyTokenTypes}. 
 * So it's easier to show debug information - e.g. instead of type 7, a "SLIST" can be printed
 * @author Albert Tregnaghi
 *
 */
class GroovyTokenTypeDebugInfoInspector implements GroovyTokenTypes{
	
	private Map<Integer,String> map = new TreeMap<>();
	public GroovyTokenTypeDebugInfoInspector(){
		Field[] fields = GroovyTokenTypes.class.getDeclaredFields();
		for (Field field: fields){
			try {
				Object r = field.get(this);
				if (r instanceof Integer){
					Integer i = (Integer) r;
					map.put(i, field.getName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				break;
			}
		}
	}
	
	public String getGroovyTokenTypeName(int type){
		String data = map.get(type);
		if (data==null){
			return "???";
		}
		return data;
	}
}
package de.jcup.egradle.codeassist.dsl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XMLDSLTypeInfo implements Comparable<XMLDSLTypeInfo> {
	
	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "property", type=XMLTask.class)
	private Set<XMLDSLPropertyInfo> properties = new LinkedHashSet<>();
	
	@XmlElement(name = "method", type=XMLTask.class)
	private Set<XMLDSLMethodInfo> methods = new LinkedHashSet<>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<XMLDSLMethodInfo> getMethods() {
		return methods;
	}
	
	public Set<XMLDSLPropertyInfo> getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return "XMLDSLTypeInfo [ properties=" + properties+", methods=" + methods + "]";
	}

	@Override
	public int compareTo(XMLDSLTypeInfo o) {
		if (o==null){
			return 1;
		}
		if (o.name==null){
			if (name==null){
				return 0;
			}
			return 1;
		}
		return name.compareTo(o.name);
	}
}

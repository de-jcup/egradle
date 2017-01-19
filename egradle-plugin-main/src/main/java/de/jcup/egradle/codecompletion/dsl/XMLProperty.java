package de.jcup.egradle.codecompletion.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "property")
public class XMLProperty implements Property{
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "type")
	private String typeAsString;
	
	@XmlElement(name = "description")
	private String description;

	private Type type;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getTypeAsString(){
		return typeAsString;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public void setType(Type returnType) {
		this.type = returnType;
	}
	
	@Override
	public String toString() {
		return "XMLProperty [name=" + name + ", type=" + typeAsString + "]";
	}
	
}

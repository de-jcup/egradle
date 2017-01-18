package de.jcup.egradle.core.codecompletion.model.gradledsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.jcup.egradle.core.codecompletion.model.Parameter;
import de.jcup.egradle.core.codecompletion.model.Type;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "parameter")
public class XMLParameter implements Parameter {


	@XmlAttribute(name = "type")
	private String typeAsString;
	
	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "description")
	private String description;

	private Type type;

	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public String getTypeAsString() {
		return typeAsString;
	}
}

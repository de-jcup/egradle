package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.jcup.egradle.core.codecompletion.model.Method;
import de.jcup.egradle.core.codecompletion.model.Type;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "method")
public class XMLMethod implements Method{
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "returnType")
	private String returnTypeAsString;
	
	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "parameter")
	private List<XMLParameter> parameters = new ArrayList<>();

	private Type returnType;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getReturnTypeAsString(){
		return returnTypeAsString;
	}
	
	@Override
	public Type getReturnType() {
		return returnType;
	}
	
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	@Override
	public List<XMLParameter> getParameters() {
		return parameters;
	}

	
}

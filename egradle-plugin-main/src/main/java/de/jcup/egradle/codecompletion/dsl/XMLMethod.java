package de.jcup.egradle.codecompletion.dsl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "method")
public class XMLMethod implements Method{
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "returnType")
	private String returnTypeAsString;
	
	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "parameter", type=XMLParameter.class)
	private List<Parameter> parameters = new ArrayList<>();

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
	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "XMLMethod [name=" + name + ", returnType=" + returnTypeAsString + "]";
	}
	
}

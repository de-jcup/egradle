package de.jcup.egradle.codeassist.dsl;

import java.util.ArrayList;
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
	
	@XmlAttribute(name = "delegationTarget")
	private String delegationTargetAsString;
	
	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "parameter", type=XMLParameter.class)
	private List<Parameter> parameters = new ArrayList<>();

	private Type returnType;

	private Type parent;

	private Type delegationTarget;

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
	
	@Override
	public Type getParent() {
		return parent;
	}
	
	public void setParent(Type parent) {
		this.parent = parent;
	}

	@Override
	public String getDelegationTargetAsString() {
		return delegationTargetAsString;
	}

	public void setDelegationTargetAsString(String delegationTargetAsString) {
		this.delegationTargetAsString = delegationTargetAsString;
	}
	
	public void setDelegationTarget(Type target) {
		this.delegationTarget = target;
	}
	
	@Override
	public Type getDelegationTarget() {
		return delegationTarget;
	}

}

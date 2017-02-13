package de.jcup.egradle.codeassist.dsl;

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
public class XMLMethod implements ModifiableMethod{
	
	@XmlAttribute(name = "name")
	String name;
	
	@XmlAttribute(name = "returnType")
	private String returnTypeAsString;
	
	@XmlAttribute(name = "delegationTarget")
	private String delegationTargetAsString;
	
	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "parameter", type=XMLParameter.class)
	List<Parameter> parameters = new ArrayList<>();

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
	
	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableMethod#setReturnType(de.jcup.egradle.codeassist.dsl.Type)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableMethod#setParent(de.jcup.egradle.codeassist.dsl.Type)
	 */
	@Override
	public void setParent(Type parent) {
		this.parent = parent;
	}

	@Override
	public String getDelegationTargetAsString() {
		return delegationTargetAsString;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableMethod#setDelegationTargetAsString(java.lang.String)
	 */
	@Override
	public void setDelegationTargetAsString(String delegationTargetAsString) {
		this.delegationTargetAsString = delegationTargetAsString;
	}
	
	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableMethod#setDelegationTarget(de.jcup.egradle.codeassist.dsl.Type)
	 */
	@Override
	public void setDelegationTarget(Type target) {
		this.delegationTarget = target;
	}
	
	@Override
	public Type getDelegationTarget() {
		return delegationTarget;
	}

	@Override
	public int compareTo(Method o) {
		if (o==null){
			return 1;
		}
		if (o==this){
			return 0;
		}
		if (name==null){
			return -1;
		}
		String otherName = o.getName();
		if (otherName==null){
			return 1;
		}
		int compared = name.compareTo(otherName);
		if (compared!=0){
			return compared;
		}
		/* okay - check parameters */
		List<Parameter> otherParameters = o.getParameters();
		if (otherParameters==null){
			return 1;
		}
		int diff = parameters.size()-otherParameters.size();
		if (diff !=0){
			return diff;
		}
		/* same parameter length*/
		Iterator<Parameter> paramIt=parameters.iterator();
		Iterator<Parameter> otherParamIt=otherParameters.iterator();
		while(paramIt.hasNext()){
			Parameter p = paramIt.next();
			Parameter op = otherParamIt.next();
			int paramCompared = p.compareTo(op);
			if (paramCompared!=0){
				return paramCompared;
			}
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delegationTargetAsString == null) ? 0 : delegationTargetAsString.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((returnTypeAsString == null) ? 0 : returnTypeAsString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLMethod other = (XMLMethod) obj;
		if (delegationTargetAsString == null) {
			if (other.delegationTargetAsString != null)
				return false;
		} else if (!delegationTargetAsString.equals(other.delegationTargetAsString))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (returnTypeAsString == null) {
			if (other.returnTypeAsString != null)
				return false;
		} else if (!returnTypeAsString.equals(other.returnTypeAsString))
			return false;
		return true;
	}

}

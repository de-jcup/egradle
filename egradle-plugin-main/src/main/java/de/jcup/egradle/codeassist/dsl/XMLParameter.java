package de.jcup.egradle.codeassist.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "parameter")
public class XMLParameter implements Parameter, ModifiableParameter {


	@XmlAttribute(name = "type")
	String typeAsString;
	
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

	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableParameter#setType(de.jcup.egradle.codeassist.dsl.Type)
	 */
	@Override
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
	
	@Override
	public String toString() {
		return "XMLType [name=" + name + ", type="+typeAsString+" ]";
	}

	@Override
	public int compareTo(Parameter o) {
		if (o==null){
			return 1;
		}
		String otherTypeAsString = o.getTypeAsString();
		if (otherTypeAsString==null){
			return 1;
		}
		if (typeAsString==null){
			return -1;
		}
		int comparedType= typeAsString.compareTo(otherTypeAsString);
		return comparedType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typeAsString == null) ? 0 : typeAsString.hashCode());
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
		XMLParameter other = (XMLParameter) obj;
		if (typeAsString == null) {
			if (other.typeAsString != null)
				return false;
		} else if (!typeAsString.equals(other.typeAsString))
			return false;
		return true;
	}
	
	
}

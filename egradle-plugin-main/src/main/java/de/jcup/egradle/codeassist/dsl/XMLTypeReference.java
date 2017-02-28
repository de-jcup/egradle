package de.jcup.egradle.codeassist.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class XMLTypeReference implements ModifiableTypeReference {

	@XmlAttribute(name = "name")
	private String typeAsString;

	private Type type;

	public String getTypeAsString() {
		return typeAsString;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type returnType) {
		this.type = returnType;
		if (type!=null){
			this.typeAsString=type.getName();
		}
	}

	@Override
	public int compareTo(TypeReference o) {
		if (o == null) {
			return 1;
		}
		String otherTypeAsString = o.getTypeAsString();
		if (otherTypeAsString == null) {
			return 1;
		}
		if (typeAsString == null) {
			return -1;
		}
		int comparedName = typeAsString.compareTo(otherTypeAsString);
		return comparedName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		XMLTypeReference other = (XMLTypeReference) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (typeAsString == null) {
			if (other.typeAsString != null)
				return false;
		} else if (!typeAsString.equals(other.typeAsString))
			return false;
		return true;
	}

	
}

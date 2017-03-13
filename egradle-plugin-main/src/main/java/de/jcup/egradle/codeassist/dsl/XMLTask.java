package de.jcup.egradle.codeassist.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "method")
public class XMLTask implements Task{
	
	@XmlAttribute(name = "name")
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}

	private Type type;

	
	@XmlAttribute(name = "type")
	private String typeAsString;
	
	public void setTypeAsString(String typeAsString) {
		this.typeAsString = typeAsString;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getTypeAsString() {
		return typeAsString;
	}

	@Override
	public String toString() {
		return "XMLTask [name="+name+", typeAsString=" + typeAsString+", type="+type+"]";
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int compareTo(Task other) {
		if (other==null){
			return 1;
		}
		String otherTypeAsString = other.getTypeAsString();
		if (otherTypeAsString==null){
			return 1;
		}
		if (typeAsString==null){
			return -1;
		}
		return typeAsString.compareTo(otherTypeAsString);
	}


	

	
	
}

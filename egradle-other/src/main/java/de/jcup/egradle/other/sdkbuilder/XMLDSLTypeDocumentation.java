package de.jcup.egradle.other.sdkbuilder;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "dslDoc")
public class XMLDSLTypeDocumentation {

	@XmlElement(name = "property", type = XMLDSLPropertyInfo.class)
	private Set<XMLDSLPropertyInfo> properties = new LinkedHashSet<>();

	@XmlElement(name = "method", type = XMLDSLMethodInfo.class)
	private Set<XMLDSLMethodInfo> methods = new LinkedHashSet<>();

	public Set<XMLDSLMethodInfo> getMethods() {
		return methods;
	}

	public Set<XMLDSLPropertyInfo> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "XMLDSLTypeInfo [ properties=" + properties + ", methods=" + methods + "]";
	}

}

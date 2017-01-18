package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.jcup.egradle.core.codecompletion.model.Type;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "type")
public class XMLType implements Type{

	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "language")
	private String language;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlAttribute(name = "version")
	private String version;
	
	@XmlAttribute(name = "interface")
	private boolean _interface;
	
	@XmlElement(name = "method")
	private Set<XMLMethod> methods = new LinkedHashSet<>();
	
	@XmlElement(name = "property")
	private Set<XMLProperty> properties = new LinkedHashSet<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<XMLProperty> getProperties() {
		return properties;
	}

	@Override
	public Set<XMLMethod> getMethods() {
		return methods;
	}

	public String getLanguage() {
		return language;
	}
	
	public String getVersion() {
		return version;
	}
	
	@Override
	public String getShortName() {
		/* FIXME ATR, 18.01.2017: implement */
		return null;
	}

}

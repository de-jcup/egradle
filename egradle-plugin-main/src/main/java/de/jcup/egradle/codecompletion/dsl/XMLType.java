package de.jcup.egradle.codecompletion.dsl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	
	@XmlElement(name = "method", type=XMLMethod.class)
	private Set<Method> methods = new LinkedHashSet<>();
	
	@XmlElement(name = "property", type = XMLProperty.class)
	private Set<Property> properties = new LinkedHashSet<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<Property> getProperties() {
		return properties;
	}

	@Override
	public Set<Method> getMethods() {
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
	
	@Override
	public String toString() {
		return "XMLType [name=" + name + "]";
	}

	@Override
	public void mixin(Type mixinType) {
		if (mixinType==null){
			return;
		}
		for(Method method: mixinType.getMethods()){
			methods.add(method);
		}
	}

	@Override
	public void addExtension(String extensionId, Type extensionType) {
		// TODO Auto-generated method stub
		
	}
}

package de.jcup.egradle.codecompletion.dsl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "type")
public class XMLType implements Type {

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

	@XmlElement(name = "method", type = XMLMethod.class)
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

	private Map<Method, Reason> methodReasons = new HashMap<>();
	
	public Reason getReasonForMethod(Method method){
		if (method==null){
			return null;
		}
		return methodReasons.get(method);
	}
	
	@Override
	public void mixin(Type mixinType, Reason reason) {
		if (mixinType == null) {
			return;
		}
		for (Method method : mixinType.getMethods()) {
			methods.add(method);
		}
		/*
		 * TODO ATR, 28.01.2017: use a map and handle mixin type + a reason
		 * object So a cleanup can be done and also methods like isMixedIn() and
		 * getMixinReason() can be easily implemented. This becomes important
		 * when plugins are not all applied but only dedicated, and also to show
		 * information about reasoning why this method comes up (should appear
		 * in code propsals and descriptions later)
		 */
	}
	private Map<String, Type> extensionMap= new TreeMap<>();
	private Map<String, Reason> extensionReasonMap= new TreeMap<>();

	@Override
	public void addExtension(String extensionId, Type extensionType, Reason reason) {
		if (extensionType==null){
			return;
		}
		if (extensionId==null){
			extensionId="null";
		}
		extensionMap.put(extensionId, extensionType);
	}
	
	public Map<String, Type> getExtensions() {
		return extensionMap;
	}
	
	public Reason getReasonForExtension(String extensionId){
		if (extensionId==null){
			return null;
		}
		return extensionReasonMap.get(extensionId);
	}
	
}

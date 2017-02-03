package de.jcup.egradle.codeassist.dsl;

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

import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "type")
public class XMLType implements Type {

	@XmlAttribute(name = "interface")
	private boolean _interface;

	@XmlElement(name = "description")
	private String description;

	private Map<String, Type> extensionMap = new TreeMap<>();

	private Map<String, Reason> extensionReasonMap = new TreeMap<>();

	@XmlAttribute(name = "language")
	private String language;

	private Map<Method, Reason> methodReasons = new HashMap<>();

	@XmlElement(name = "method", type = XMLMethod.class)
	private Set<Method> methods = new LinkedHashSet<>();

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "property", type = XMLProperty.class)
	private Set<Property> properties = new LinkedHashSet<>();

	@XmlAttribute(name = "version")
	private String version;

	@Override
	public void addExtension(String extensionId, Type extensionType, Reason reason) {
		if (extensionType == null) {
			return;
		}
		if (extensionId == null) {
			extensionId = "null";
		}
		extensionMap.put(extensionId, extensionType);
		if (reason != null) {
			extensionReasonMap.put(extensionId, reason);
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	public Map<String, Type> getExtensions() {
		return extensionMap;
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public Set<Method> getMethods() {
		return methods;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Property> getProperties() {
		return properties;
	}

	public Reason getReasonForExtension(String extensionId) {
		if (extensionId == null) {
			return null;
		}
		return extensionReasonMap.get(extensionId);
	}

	public Reason getReasonForMethod(Method method) {
		if (method == null) {
			return null;
		}
		return methodReasons.get(method);
	}

	@Override
	public String getShortName() {
		return StringUtils.substringAfterLast(name, ".");
	}

	public String getVersion() {
		if (version==null || version.length()==0){
			return "current";
		}
		return version;
	}

	@Override
	public void mixin(Type mixinType, Reason reason) {
		if (mixinType == null) {
			return;
		}
		for (Method method : mixinType.getMethods()) {
			methods.add(method);
			if (reason != null) {
				methodReasons.put(method, reason);
			}
		}
	}

	@Override
	public String toString() {
		return "XMLType [name=" + name + "]";
	}

}

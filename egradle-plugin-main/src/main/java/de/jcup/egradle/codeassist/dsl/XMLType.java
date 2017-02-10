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
public class XMLType implements ModifiableType {

	@XmlAttribute(name = "interface")
	private boolean _interface;
	
	@XmlElement(name = "description")
	private String description;

	private Map<String, Type> extensionMap = new TreeMap<>();

	private Map<String, Reason> extensionReasonMap = new TreeMap<>();

	@XmlAttribute(name = "language")
	private String language;

	private Map<LanguageElement, Reason> elementReasons = new HashMap<>();

	@XmlElement(name = "method", type = XMLMethod.class)
	Set<Method> methods = new LinkedHashSet<>();

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "property", type = XMLProperty.class)
	Set<Property> properties = new LinkedHashSet<>();

	@XmlAttribute(name = "version")
	private String version;

	@XmlAttribute(name = "superType")
	private String superTypeAsString;

	private Type superType;

	private Set<Method> mergedMethods;
	private Set<Property> mergedProperties;

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
		if (superType==null){
			return methods;
		}
		return mergedMethods;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Property> getProperties() {
		if (superType==null){
			return properties;
		}
		return mergedProperties;
	}

	public Reason getReasonForExtension(String extensionId) {
		if (extensionId == null) {
			return null;
		}
		return extensionReasonMap.get(extensionId);
	}

	public Reason getReasonFor(LanguageElement element) {
		if (element == null) {
			return null;
		}
		return elementReasons.get(element);
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
				elementReasons.put(method, reason);
			}
		}
	}

	@Override
	public String toString() {
		return "XMLType [name=" + name + "]";
	}

	@Override
	public String getSuperTypeAsString() {
		return superTypeAsString;
	}

	@Override
	public void extendBySuperType(Type superType) {
		if (this.superType!=null){
			/* unregister former reasons*/
			Set<Method> formerSuperMethods = this.superType.getMethods();
			for (Method sm : formerSuperMethods){
				elementReasons.remove(sm);
			}
			
			Set<Property> formerSuperProperties = this.superType.getProperties();
			for (Property sm : formerSuperProperties){
				elementReasons.remove(sm);
			}
		}
		this.superType=superType;
		if (superType==null){
			mergedMethods=null;
			mergedProperties=null;
			return;
		}
		this.mergedMethods=new LinkedHashSet<>(methods);
		Set<Method> superMethods = superType.getMethods();
		this.mergedMethods.addAll(superMethods);
		/* register super methods and reason*/
		ReasonImpl superTypeReason = new ReasonImpl();
		superTypeReason.setSuperType(superType);
		for (Method sm : superMethods){
			elementReasons.put(sm,superTypeReason);
		}
		
		Set<Property> superProperties = this.superType.getProperties();
		this.mergedProperties=new LinkedHashSet<>(properties);
		this.mergedProperties.addAll(superProperties);
		/* register super properties and reason*/
		for (Property sm : superProperties){
			elementReasons.put(sm,superTypeReason);
		}
	}

}

package de.jcup.egradle.codeassist.dsl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "method")
public class XMLPlugin implements Plugin{
	
	@XmlAttribute(name = "id")
	private String id;
	
	@XmlAttribute(name = "description")
	private String description;

	@XmlElement(name = "extends", type=XMLTypeExtension.class)
	private Set<TypeExtension> typeExtensions = new LinkedHashSet<>();

	@Override
	public Set<TypeExtension> getExtensions() {
		return typeExtensions;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "XMLPlugin [id=" + id + ", description=" + description + ", typeExtensions=" + typeExtensions + "]";
	}

	

	
	
}

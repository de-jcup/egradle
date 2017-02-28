package de.jcup.egradle.codeassist.dsl;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "plugins")
public class XMLPlugins{
	
	@XmlElement(name = "plugin", type=XMLPlugin.class)
	private Set<Plugin> plugins = new TreeSet<>();

	public Set<Plugin> getPlugins() {
		return plugins;
	}


	@Override
	public String toString() {
		return "XMLPlugins [ plugins=" + plugins + "]";
	}

	

	
	
}

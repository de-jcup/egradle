package de.jcup.egradle.codecompletion.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "extends")
public class XMLTypeExtension implements TypeExtension {

	@XmlAttribute(name = "id")
	private String id;

	@XmlAttribute(name = "targetClass")
	private String targetTypeAsString;

	@XmlAttribute(name = "extensionClass")
	private String extensionTypeAsString;

	@XmlAttribute(name = "mixinClass")
	private String mixinTypeAsString;

	public String getExtensionTypeAsString() {
		return extensionTypeAsString;
	}

	public String getTargetTypeAsString() {
		return targetTypeAsString;
	}

	public String getMixinTypeAsString() {
		return mixinTypeAsString;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "XMLTypeExtension [targetTypeAsString=" + targetTypeAsString + ", extensionTypeAsString="
				+ extensionTypeAsString + ", mixinTypeAsString=" + mixinTypeAsString + "]";
	}

}

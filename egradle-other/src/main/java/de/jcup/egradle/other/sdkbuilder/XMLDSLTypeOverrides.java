package de.jcup.egradle.other.sdkbuilder;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.jcup.egradle.codeassist.dsl.XMLType;
/**
 * Files containing "virtual" types which do some override mechanism. Currently only used for manual delegationTarget overload
 * @author Albert Tregnaghi
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "overrides")
public class XMLDSLTypeOverrides {

	@XmlElement(name = "type", type = XMLType.class)
	private Set<XMLType> overrideTypes = new LinkedHashSet<>();

	public Set<XMLType> getOverrideTypes() {
		return overrideTypes;
	}

	@Override
	public String toString() {
		return "XMLDSLTypeOverrides [...]";
	}

}

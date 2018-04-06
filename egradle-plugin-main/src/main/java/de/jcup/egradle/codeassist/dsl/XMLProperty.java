/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.codeassist.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "property")
public class XMLProperty implements ModifiableProperty {

	@XmlAttribute(name = "name")
	String name;

	@XmlAttribute(name = "type")
	private String typeAsString;

	@XmlElement(name = "description")
	private String description;

	private Type type;

	@XmlAttribute(name = "documented", required = false)
	private Boolean partOfGradleDSLDocumentation = null;

	private Type parent;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeAsString() {
		return typeAsString;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type returnType) {
		this.type = returnType;
	}

	@Override
	public String toString() {
		return "XMLProperty [name=" + name + ", type=" + typeAsString + "]";
	}

	@Override
	public Type getParent() {
		return parent;
	}

	@Override
	public void setParent(Type parent) {
		this.parent = parent;
	}

	@Override
	public boolean isDocumented() {
		/* workaround for JAXM preventing to: atribute="false" */
		return partOfGradleDSLDocumentation != null && partOfGradleDSLDocumentation.booleanValue();
	}

	public void setDocumented(boolean partOfGradleDSLDocumentation) {
		if (!partOfGradleDSLDocumentation) {
			this.partOfGradleDSLDocumentation = null;
		} else {
			this.partOfGradleDSLDocumentation = Boolean.TRUE;
		}
	}

	@Override
	public int compareTo(Property o) {
		if (o == null) {
			return 1;
		}
		String otherName = o.getName();
		if (otherName == null) {
			return 1;
		}
		if (name == null) {
			return -1;
		}
		int comparedName = name.compareTo(otherName);
		return comparedName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeAsString == null) ? 0 : typeAsString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLProperty other = (XMLProperty) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeAsString == null) {
			if (other.typeAsString != null)
				return false;
		} else if (!typeAsString.equals(other.typeAsString))
			return false;
		return true;
	}

}

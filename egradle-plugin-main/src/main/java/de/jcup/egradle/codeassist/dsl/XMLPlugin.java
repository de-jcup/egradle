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

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "plugin")
public class XMLPlugin implements Plugin {

	@XmlAttribute(name = "id")
	private String id;

	@XmlAttribute(name = "description")
	private String description;

	@XmlElement(name = "extends", type = XMLTypeExtension.class)
	private Set<TypeExtension> typeExtensions = new TreeSet<>();

	@Override
	public Set<TypeExtension> getExtensions() {
		return typeExtensions;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public int compareTo(Plugin o) {
		if (o == null) {
			return 1;
		}
		String otherId = o.getId();
		if (otherId == null) {
			return 1;
		}
		if (id == null) {
			return -1;
		}
		int comparedId = id.compareTo(otherId);
		return comparedId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((typeExtensions == null) ? 0 : typeExtensions.hashCode());
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
		XMLPlugin other = (XMLPlugin) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (typeExtensions == null) {
			if (other.typeExtensions != null)
				return false;
		} else if (!typeExtensions.equals(other.typeExtensions))
			return false;
		return true;
	}

}

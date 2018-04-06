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
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "method")
public class XMLTask implements Task {

	@XmlAttribute(name = "name")
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	private Type type;

	@XmlAttribute(name = "type")
	private String typeAsString;

	public void setTypeAsString(String typeAsString) {
		this.typeAsString = typeAsString;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTypeAsString() {
		return typeAsString;
	}

	@Override
	public String toString() {
		return "XMLTask [name=" + name + ", typeAsString=" + typeAsString + ", type=" + type + "]";
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int compareTo(Task other) {
		if (other == null) {
			return 1;
		}
		String otherTypeAsString = other.getTypeAsString();
		if (otherTypeAsString == null) {
			return 1;
		}
		if (typeAsString == null) {
			return -1;
		}
		return typeAsString.compareTo(otherTypeAsString);
	}

}

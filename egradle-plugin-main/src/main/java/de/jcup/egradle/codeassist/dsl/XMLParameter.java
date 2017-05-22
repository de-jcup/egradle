/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
@XmlRootElement(name = "parameter")
public class XMLParameter implements Parameter, ModifiableParameter {


	@XmlAttribute(name = "type")
	String typeAsString;
	
	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "description")
	private String description;

	private Type type;

	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.codeassist.dsl.ModifiableParameter#setType(de.jcup.egradle.codeassist.dsl.Type)
	 */
	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public String getTypeAsString() {
		return typeAsString;
	}
	
	@Override
	public String toString() {
		return "XMLType [name=" + name + ", type="+typeAsString+" ]";
	}

	@Override
	public int compareTo(Parameter o) {
		if (o==null){
			return 1;
		}
		String otherTypeAsString = o.getTypeAsString();
		if (otherTypeAsString==null){
			return 1;
		}
		if (typeAsString==null){
			return -1;
		}
		int comparedType= typeAsString.compareTo(otherTypeAsString);
		return comparedType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		XMLParameter other = (XMLParameter) obj;
		if (typeAsString == null) {
			if (other.typeAsString != null)
				return false;
		} else if (!typeAsString.equals(other.typeAsString))
			return false;
		return true;
	}
	
	
}

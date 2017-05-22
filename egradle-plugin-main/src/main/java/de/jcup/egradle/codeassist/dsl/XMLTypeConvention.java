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
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "convention")
public class XMLTypeConvention implements TypeConvention {

	@XmlAttribute(name = "id")
	private String id;
	
	@XmlAttribute(name = "conventionClass")
	private String conventionTypeAsString;

	public void setConventionTypeAsString(String extensionTypeAsString) {
		this.conventionTypeAsString = extensionTypeAsString;
	}
	
	public String getConventionTypeAsString() {
		return conventionTypeAsString;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "XMLTypeConvention [conventionTypeAsString="
				+ conventionTypeAsString + ", id=" + id+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conventionTypeAsString == null) ? 0 : conventionTypeAsString.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		XMLTypeConvention other = (XMLTypeConvention) obj;
		if (conventionTypeAsString == null) {
			if (other.conventionTypeAsString != null)
				return false;
		} else if (!conventionTypeAsString.equals(other.conventionTypeAsString))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String compareString;
	
	private String getCompareString() {
		if (compareString==null){
			compareString=createCompareString(this);
		}
		return compareString;
	}
	
	@Override
	public int compareTo(TypeConvention o) {
		if (o==null){
			return 1;
		}
		if (o==this){
			return 0;
		}
		String otherCompareString = null;
		if (o instanceof XMLTypeConvention){
			XMLTypeConvention ote = (XMLTypeConvention) o;
			otherCompareString=ote.getCompareString();
		}else{
			otherCompareString = createCompareString(o);
		}
		int compared = getCompareString().compareTo(otherCompareString);
		return compared;
	}
	
	private String createCompareString(TypeConvention extension){
		StringBuilder sb = new StringBuilder();
		sb.append(extension.getId());
		sb.append('|');
		sb.append(extension.getConventionTypeAsString());
		return sb.toString();
	}

}

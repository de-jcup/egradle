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

	public void setMixinTypeAsString(String mixinTypeAsString) {
		this.mixinTypeAsString = mixinTypeAsString;
	}
	
	public void setExtensionTypeAsString(String extensionTypeAsString) {
		this.extensionTypeAsString = extensionTypeAsString;
	}
	
	public void setTargetTypeAsString(String targetTypeAsString) {
		this.targetTypeAsString = targetTypeAsString;
	}
	
	public String getExtensionTypeAsString() {
		return extensionTypeAsString;
	}

	public String getTargetTypeAsString() {
		return targetTypeAsString;
	}

	public String getMixinTypeAsString() {
		return mixinTypeAsString;
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
		return "XMLTypeExtension [targetTypeAsString=" + targetTypeAsString + ", extensionTypeAsString="
				+ extensionTypeAsString + ", mixinTypeAsString=" + mixinTypeAsString + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extensionTypeAsString == null) ? 0 : extensionTypeAsString.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mixinTypeAsString == null) ? 0 : mixinTypeAsString.hashCode());
		result = prime * result + ((targetTypeAsString == null) ? 0 : targetTypeAsString.hashCode());
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
		XMLTypeExtension other = (XMLTypeExtension) obj;
		if (extensionTypeAsString == null) {
			if (other.extensionTypeAsString != null)
				return false;
		} else if (!extensionTypeAsString.equals(other.extensionTypeAsString))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mixinTypeAsString == null) {
			if (other.mixinTypeAsString != null)
				return false;
		} else if (!mixinTypeAsString.equals(other.mixinTypeAsString))
			return false;
		if (targetTypeAsString == null) {
			if (other.targetTypeAsString != null)
				return false;
		} else if (!targetTypeAsString.equals(other.targetTypeAsString))
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
	public int compareTo(TypeExtension o) {
		if (o==null){
			return 1;
		}
		if (o==this){
			return 0;
		}
		String otherCompareString = null;
		if (o instanceof XMLTypeExtension){
			XMLTypeExtension ote = (XMLTypeExtension) o;
			otherCompareString=ote.getCompareString();
		}else{
			otherCompareString = createCompareString(o);
		}
		int compared = getCompareString().compareTo(otherCompareString);
		return compared;
	}
	
	private String createCompareString(TypeExtension extension){
		StringBuilder sb = new StringBuilder();
		sb.append(extension.getId());
		sb.append('#');
		sb.append(extension.getTargetTypeAsString());
		sb.append('=');
		sb.append(extension.getMixinTypeAsString());
		sb.append('|');
		sb.append(extension.getExtensionTypeAsString());
		return sb.toString();
	}

}

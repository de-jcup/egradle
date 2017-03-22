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
 package de.jcup.egradle.sdk.builder.model;

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

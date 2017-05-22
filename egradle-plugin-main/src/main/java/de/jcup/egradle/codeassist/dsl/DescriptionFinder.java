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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class DescriptionFinder {

	/**
	 * Find description for given element. If element has no description itself the contained
	 * interfaces will be scanned for method/property descriptions
	 * 
	 * @param element
	 * @return description or <code>null</code>
	 */
	public String findDescription(LanguageElement element) {
		if (element == null) {
			return null;
		}
		String description = element.getDescription();
		if (description != null && description.indexOf("@inheritDoc") == -1) {
			/*
			 * not necessary to scan - description is available in normal way...
			 */
			return description;
		}
		if (element instanceof TypeChild) {
			TypeChild child = (TypeChild) element;
			Type parentType = child.getParent();
			if (parentType == null) {
				return null;
			}
			if (element instanceof Method) {
				MethodRefTypeVisitor visitor = new MethodRefTypeVisitor();
				visitor.method = (Method) element;
				return invite(visitor, parentType);
			} else if (element instanceof Property) {
				PropertyRefVisitor visitor = new PropertyRefVisitor();
				visitor.property = (Property) element;
				return invite(visitor, parentType);
			}
		}
		return null;
	}
	
	private String invite(RefTypeVisitor visitor, Type parentType) {
		Set<TypeReference> interfaceReferences = parentType.getInterfaces();
		for (TypeReference ref : interfaceReferences) {
			Type refType = ref.getType();
			if (refType == null) {
				continue;
			}
			String result = visitor.visit(refType);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	private String createInheritedDescriptionWithLink(Type refType, String descriptionFromInterface) {
		StringBuilder sb = new StringBuilder();
		sb.append("<i>Inherited description from:");
		HTMLLinkUtil.appendLinkToType(sb, false, refType, null);
		sb.append(" :</i><br>");
		sb.append(descriptionFromInterface);
		return sb.toString();
	}

	private abstract class RefTypeVisitor {
		/**
		 * Visits given ref type
		 * 
		 * @param refType
		 * @return <code>null</code> when nothing found, otherwise string with
		 *         description
		 */
		abstract String visit(Type refType);
	}

	private class MethodRefTypeVisitor extends RefTypeVisitor {

		private Method method;

		@Override
		String visit(Type refType) {
			Set<Method> interfaceMethods = refType.getDefinedMethods();
			for (Method interfaceMethod : interfaceMethods) {
				if (interfaceMethod == null) {
					continue;
				}
				if (MethodUtils.haveSameSignatures(method, interfaceMethod)) {
					String descriptionFromInterface = interfaceMethod.getDescription();
					if (StringUtils.isNotBlank(descriptionFromInterface)) {
						if (descriptionFromInterface.indexOf("@inheritDoc") == -1) {
							/* enrich with interface link */
							return createInheritedDescriptionWithLink(refType, descriptionFromInterface);
						}
					}
					break;// break this type - means description was blank for
							// same method. so try another one
				}
			}
			/* not found here - so try other interfaces if available */
			String result = invite(this, refType);
			if (result != null) {
				return result;
			}
			return null;
		}

		

	}

	private class PropertyRefVisitor extends RefTypeVisitor {
		private Property property;

		@Override
		String visit(Type refType) {
			Set<Property> interfaceProperties = refType.getDefinedProperties();
			for (Property interfaceProperty : interfaceProperties) {
				if (interfaceProperty == null) {
					continue;
				}
				if (interfaceProperty.getName().equals(property.getName())) {
					String descriptionFromInterface = interfaceProperty.getDescription();
					if (StringUtils.isNotBlank(descriptionFromInterface)) {
						if (descriptionFromInterface.indexOf("@inheritDoc") == -1) {
							return createInheritedDescriptionWithLink(refType, descriptionFromInterface);
						}
					}
					break;// break this type - means description was blank for
							// same properties. so try another one
				}
			}
			/* not found here - so try other interfaces if available */
			String result = invite(this, refType);
			if (result != null) {
				return result;
			}
			return null;
		}

	}

	

}

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
package de.jcup.egradle.sdk.builder.action.delegationtarget;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

/* TODO ATR, 16.02.2017: override (manage inside alternative-delegatesTo.xml) all which is annotated by @DelegatesTo(value=...trategy=1) 
 */
public class CalculateDelegationTargetsAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			XMLType originType = (XMLType) context.originGradleFilesProvider.getType(typeName);
			calculateStillMissingDelegateTargets(originType, context);
		}

	}

	/**
	 * Very interesting how gradle internal works:
	 * 
	 * <pre>
	 * 
	 * private EclipseJdt jdt;
	 * 
	 * private EclipseJdt getJdt();
	 * 
	 * private void setJdt(EclipseJdt jdt);
	 * 
	 * private void jdt(Closure closure);
	 * </pre>
	 * 
	 * So the closure type is simply always the property type!
	 * 
	 * <pre>
	 * eclipse{
	 * 	jdt{
	 * 		// do something with jdt
	 *  }
	 * }
	 * </pre>
	 * 
	 * Is pretty much like:
	 * 
	 * <pre>
	 * 	project.callClosureWithDelegateTarget(getEclipse()).callClosureWithDelegateTarget(getJdt())...
	 * </pre>
	 * 
	 * Normally typical delegatino targets are done by EGradleAssembleDslTask in
	 * gradle fork. But there are some special parts which where now handled
	 * here.
	 * 
	 * @param type
	 * @param context
	 */
	void calculateStillMissingDelegateTargets(Type type, SDKBuilderContext context) {
		for (Method m : type.getMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			context.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = method.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set
			}
			String targetType = null;
			List<Parameter> parameters = method.getParameters();
			if (parameters.size() != 1) {
				continue;
			}
			Parameter firstParam = parameters.iterator().next();
			if (!firstParam.getTypeAsString().equals("groovy.lang.Closure")) {
				continue;
			}
			String methodName = m.getName();
			targetType = scanProperties(type, methodName);

			if (targetType != null) {
				method.setDelegationTargetAsString(targetType);
			}
		}

	}

	private String scanProperties(Type type, String methodName) {
		/*
		 * try to find a property - in this class or subclass or interfaces
		 * etc..
		 */
		Set<Property> allProperties = type.getProperties();
		for (Property p : allProperties) {
			String propName = p.getName();
			if (propName.equals(methodName)) {
				String typeAsString = p.getTypeAsString();
				if (typeAsString != null && !typeAsString.isEmpty()) {
					return typeAsString;
				}
			}
		}
		return null;
	}
}

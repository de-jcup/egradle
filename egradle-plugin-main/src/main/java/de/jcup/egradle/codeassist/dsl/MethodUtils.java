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

import static de.jcup.egradle.codeassist.dsl.TypeConstants.*;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MethodUtils {
	/**
	 * Create a method signature string
	 * 
	 * @param method
	 *            - if <code>null</code> - result will be "null"
	 * @return string, never <code>null</code>
	 */
	public static String createSignature(Method method) {
		return createSignature(method, false);
	}

	/**
	 * Create a method signature string (with parameter names)
	 * 
	 * @param method
	 *            - if <code>null</code> - result will be "null"
	 * @param longTypeNames
	 * @return string, never <code>null</code>
	 */
	public static String createSignature(Method method, boolean longTypeNames) {
		return createSignature(method, longTypeNames, true);
	}

	/**
	 * Create a method signature string
	 * 
	 * @param method
	 *            - if <code>null</code> - result will be "null"
	 * @param longTypeNames
	 * @param withParameterNames
	 *            - when true signature contains parameter names
	 * @return string, never <code>null</code>
	 */
	public static String createSignature(Method method, boolean longTypeNames, boolean withParameterNames) {
		if (method == null) {
			return "null";
		}
		StringBuilder signatureSb = new StringBuilder();
		signatureSb.append(method.getName());
		signatureSb.append('(');

		List<Parameter> params = method.getParameters();
		int pos = 0;
		for (Parameter param : params) {
			String pname = param.getName();
			if (StringUtils.isBlank(pname)) {
				pname = "arg" + pos;
			}
			if (pos > 0) {
				signatureSb.append(", ");
			}
			String typeAsString = null;
			Type pType = param.getType();
			if (pType != null) {
				if (longTypeNames) {
					typeAsString = pType.getName();
				} else {
					typeAsString = pType.getShortName();
				}
			} else {
				typeAsString = param.getTypeAsString();
			}
			if (typeAsString == null) {
				typeAsString = "<UNKNOWN>";
			}
			if (typeAsString != null) {
				if (!longTypeNames) {
					int index = typeAsString.lastIndexOf('.');
					if (index != -1) {
						typeAsString = StringUtils.substring(typeAsString, index + 1);
					}
				}
				signatureSb.append(typeAsString);
				if (withParameterNames) {
					signatureSb.append(" ");
				}
			}
			if (withParameterNames) {
				signatureSb.append(pname);
			}
			pos++;
		}
		signatureSb.append(')');
		return signatureSb.toString();
	}

	/**
	 * Calculates method identification in percentage (100= 100%).<br>
	 * <br>
	 * <ul>
	 * <li>If a method is exact same 100% is returned</li>
	 * <li>If a method is has same name, same param size, but x parameters of n
	 * are not equal 50%-100% is returned</li>
	 * <li>If a method is has same name, but param size is not equal 50% is
	 * returned</li>
	 * </ul>
	 * 
	 * @param method
	 * @param itemIdentifier
	 * @param itemParameters
	 * @return percentage
	 */
	public static int calculateMethodIdentificationPercentage(Method method, String itemIdentifier,
			String... itemParameters) {
		if (method == null) {
			return 0;
		}
		if (itemIdentifier == null) {
			return 0;
		}
		String methodName = method.getName();
		if (methodName == null) {
			return 0;
		}
		if (itemParameters == null) {
			itemParameters = new String[] {};
		}
		int percentage;
		if (itemIdentifier.equals(methodName)) {
			percentage = 50;// 50% reached because name is equal
		} else {
			if (!methodName.startsWith("get") && !methodName.startsWith("set")) {
				return 0;
			}
			if (methodName.length() == 3) {
				return 0;
			}
			String methodPartName = StringUtils.substring(methodName, 3);
			String buildMethodPartName = StringUtils.capitalize(itemIdentifier);
			if (!methodPartName.equals(buildMethodPartName)) {
				return 0;
			}
			percentage = 49;// 49% reached because name is not equal but it
							// seems groovy magic for get/set,
							// 49% because if there would exists a method with
							// absolute same name this should matter more!
		}
		/* -------------------------------- */
		/* - start percentage calculation - */
		/* -------------------------------- */

		/* name okay, could be ... */

		List<Parameter> parameters = method.getParameters();
		/* check size same */
		int paramSize = parameters.size();
		if (paramSize != itemParameters.length) {
			return percentage;
		}

		if (paramSize == 0) {
			/* speed up and avoid failures on percentage calculation */
			return percentage + 50;
		}

		/* okay at least same size */
		int pos = 0;
		int percentPerCorrectParam = 50 / paramSize;

		int paramPercents = 0;
		boolean allParamsSame = true;
		for (Parameter p : parameters) {
			String itemParam = itemParameters[pos++];
			if (p == null) {
				/* should never happen */
				continue;
			}
			if (itemParam == null) {
				continue;
			}
			/*
			 * if item parameter is with color we remove this meta information
			 */
			itemParam = StringUtils.substringBefore(itemParam, ":");

			String typeAsString = p.getTypeAsString();
			if (!typeAsString.equals(itemParam)) {
				allParamsSame = false;
			} else {
				paramPercents += percentPerCorrectParam;
			}
		}
		if (allParamsSame) {
			percentage += 50;
		} else {
			if (paramPercents >= 50) {
				/* should never happen but... */
				paramPercents = 49;
			}
			percentage += paramPercents;
		}
		return percentage;
	}

	public static boolean hasGroovyClosureAsParameter(Method m) {
		if (m == null) {
			return false;
		}
		List<Parameter> parameters = m.getParameters();
		if (parameters == null) {
			return false;
		}
		for (Parameter p : parameters) {
			if (p == null) {
				continue;
			}
			if (GROOVY_CLOSURE.equals(p.getTypeAsString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if methods have same signature. Having any null values will result
	 * in <code>false</code> as result!
	 * 
	 * @param method1
	 * @param method2
	 * @return result
	 */
	public static boolean haveSameSignatures(Method method1, Method method2) {
		if (method1 == null) {
			return false;
		}
		if (method2 == null) {
			return false;
		}
		String name1 = method1.getName();
		String name2 = method2.getName();

		if (name1 == null) {
			name1 = "";
		}
		if (name2 == null) {
			name2 = "";
		}

		if (!name1.equals(name2)) {
			return false;
		}
		List<Parameter> parameters1 = method1.getParameters();
		List<Parameter> parameters2 = method2.getParameters();

		if (parameters1.size() != parameters2.size()) {
			return false;
		}
		Iterator<Parameter> p2iterator = parameters2.iterator();
		for (Parameter p1 : parameters1) {
			Parameter p2 = p2iterator.next();
			String p1t = p1.getTypeAsString();
			String p2t = p2.getTypeAsString();
			if (p1t == null) {
				return false;
			}
			if (p2t == null) {
				return false;
			}
			if (!p1t.equals(p2t)) {
				return false;
			}
		}
		return true;

	}

	public static boolean hasSignature(Method method, String dataMethodName, String[] dataParameters,
			boolean shrinkToSimpleTypes) {
		/* validate parameters */
		if (method == null) {
			return false;
		}
		if (dataMethodName == null) {
			return false;
		}
		if (dataParameters == null) {
			dataParameters = new String[] {};
		}

		/* check */
		String methodNameOfType = method.getName();
		if (!methodNameOfType.equals(dataMethodName)) {
			return false;
		}
		List<Parameter> mParams = method.getParameters();
		if (mParams.size() != dataParameters.length) {
			return false;
		}
		int i = 0;
		for (Parameter mParam : mParams) {
			String mParamType = mParam.getTypeAsString();
			String dataParamType = dataParameters[i++];
			if (mParamType == null) {
				return false;
			}
			if (!mParamType.equals(dataParamType)) {
				if (!shrinkToSimpleTypes) {
					return false;
				}
				/* try shorting names */
				int index = mParamType.lastIndexOf(".");
				if (index == -1 || index == mParamType.length() - 1) {
					return false;
				}
				String shortendMParamType = StringUtils.substring(mParamType, index + 1);
				if (!shortendMParamType.equals(dataParamType)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isGetterOrSetter(Method method) {
		if (isGetter(method)){
			return true;
		}
		if (isSetter(method)){
			return true;
		}
		
		return false;
	}

	private static boolean isSetter(Method method) {
		int params = method.getParameters().size();
		if (params!=1){
			return false;
		}
		String methodName = method.getName();
		if (methodName.startsWith("set")) {
			return true;
		}
		return false;
	}

	private static boolean isGetter(Method method) {
		int params = method.getParameters().size();
		if (params!=0){
			return false;
		}
		String methodName = method.getName();
		
		if (methodName.startsWith("is")) {
			return true;
		}
		if (methodName.startsWith("get")) {
			return true;
		}
		return false;
	}

}

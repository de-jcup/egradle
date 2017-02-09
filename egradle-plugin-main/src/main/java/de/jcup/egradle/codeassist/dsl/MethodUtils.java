package de.jcup.egradle.codeassist.dsl;

import static de.jcup.egradle.codeassist.dsl.TypeConstants.*;

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
				typeAsString = pType.getShortName();
			} else {
				typeAsString = param.getTypeAsString();
			}
			if (typeAsString != null) {
				int index = typeAsString.lastIndexOf('.');
				if (index != -1) {
					typeAsString = StringUtils.substring(typeAsString, index + 1);
				}
				signatureSb.append(typeAsString);
				signatureSb.append(" ");
			}
			signatureSb.append(pname);
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
		if (!itemIdentifier.equals(methodName)) {
			return 0;
		}
		/* -------------------------------- */
		/* - start percentage calculation - */
		/* -------------------------------- */
		int percentage = 50; // 50% reached because name is equal
		
		/* name okay, could be ... */

		List<Parameter> parameters = method.getParameters();
		/* check size same */
		int paramSize = parameters.size();
		if (paramSize != itemParameters.length) {
			return percentage;
		}
		
		if (paramSize==0){
			/* speed up and avoid failures on percentage calculation */
			return 100;
		}
		
		/* okay at least same size */
		int pos = 0;
		int percentPerCorrectParam=50/paramSize;
		
		int paramPercents=0;
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

			if (!p.getTypeAsString().equals(itemParam)) {
				allParamsSame = false;
			}else{
				paramPercents+=percentPerCorrectParam;
			}
		}
		if (allParamsSame) {
			percentage += 50;
		}else{
			if (paramPercents>=50){
				/* should never happen but...*/
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

}

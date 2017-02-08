package de.jcup.egradle.codeassist.dsl;

import static de.jcup.egradle.codeassist.dsl.TypeConstants.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MethodUtils {
	
	public static String createSignature(Method method) {
		if (method==null){
			return "null";
		}
		StringBuilder signatureSb = new StringBuilder();
		signatureSb.append(method.getName());
		signatureSb.append('(');
		
		List<Parameter> params = method.getParameters();
		int pos=0;
		for (Parameter param : params) {
			String pname = param.getName();
			if (StringUtils.isBlank(pname)){
				pname="arg"+pos;
			}
			if (pos>0){
				signatureSb.append(", ");
			}
			String typeAsString = null;
			Type pType = param.getType();
			if (pType!=null){
				typeAsString = pType.getShortName();
			}else{
				typeAsString = param.getTypeAsString();
			}
			if (typeAsString!=null){
				int index = typeAsString.lastIndexOf('.');
				if (index!=-1){
					typeAsString=StringUtils.substring(typeAsString, index+1);
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

	public static boolean isMethodIdentified(Method method, String itemIdentifier, String ... itemParameters) {
		if (method==null){
			return false;
		}
		if (itemIdentifier==null){
			return false;
		}
		String methodName = method.getName();
		if (methodName==null){
			return false;
		}
		if (itemParameters==null){
			itemParameters=new String[]{};
		}
		if (itemIdentifier.equals(methodName) ) {
			/* name okay, could be ...*/
			
			boolean allParamsSame = true;
			List<Parameter> parameters = method.getParameters();
			/* check size same */
			if (parameters.size()!=itemParameters.length){
				return false;
			}
			/* same size */
			int pos =0;
			for (Parameter p: parameters) {
				String itemParam = itemParameters[pos++];
				if (p==null){
					/* should never happen */
					return false;
				}
				if (itemParam==null){
					return false;
				}
				/* if item parameter is with color we remove this meta information*/
				itemParam=StringUtils.substringBefore(itemParam, ":");
				
				if (! p.getTypeAsString().equals(itemParam)){
					allParamsSame=false;
					break;
				}
			}
			return allParamsSame;
			
		}
		return false;
	}

	public static boolean hasGroovyClosureAsParameter(Method m) {
		if (m==null){
			return false;
		}
		List<Parameter> parameters = m.getParameters();
		if (parameters==null){
			return false;
		}
		for (Parameter p: parameters) {
			if (p==null){
				continue;
			}
			if (GROOVY_CLOSURE.equals(p.getTypeAsString())){
				return true;
			}
		}
		return false;
	}

}

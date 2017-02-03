package de.jcup.egradle.codeassist.dsl;

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
				signatureSb.append(" ,");
			}
			String typeAsString = null;
			Type pType = param.getType();
			if (pType!=null){
				typeAsString = pType.getShortName();
			}else{
				if (param instanceof XMLParameter){
					XMLParameter xp = (XMLParameter) param;
					typeAsString = xp.getTypeAsString();
				}
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

}

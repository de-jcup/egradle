package de.jcup.egradle.core.api;

import static java.nio.charset.StandardCharsets.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.DSLConstants;
public class LinkToTypeConverter {

	public static class LinkData {

		@Override
		public String toString() {
			return "LinkData [mainName=" + mainName + ", subName=" + subName + ", parameterTypes="
					+ Arrays.toString(parameterTypes) + "]";
		}

		private String mainName;
		private String[] parameterTypes;
		private String subName;
		private String[] parameterNames;

		/**
		 * Returns type name or <code>null</code>
		 * 
		 * @return type name or <code>null</code>
		 */
		public String getMainName() {
			return mainName;
		}

		/**
		 * Returns parameter typess as string array ,never <code>null</code>
		 * 
		 * @return parameter types, or empty array, never <code>null</code>
		 */
		public String[] getParameterTypes() {
			if (parameterTypes == null) {
				return new String[] {};
			}
			return parameterTypes;
		}
		
		public String[] getParameterNames() {
			return parameterNames;
		}

		public String getSubName() {
			return subName;
		}

	}

	/**
	 * Converts to link
	 * 
	 * @param link
	 * @return converted type name, or <code>null</code> if not convertable
	 */
	public LinkData convertLink(String link) {
		LinkData data = internalConvertLink(link);
		cleanupSlashes(data);
		return data;
	}

	private void cleanupSlashes(LinkData data) {
		if (data==null){
			return;
		}
		String typeName = data.mainName;
		if (typeName==null){
			return;
		}
		while (typeName.endsWith("/")) {
			typeName = StringUtils.chop(typeName);
		}
		while (typeName.startsWith("/")) {
			typeName = StringUtils.removeStart(typeName, "/");
		}
		data.mainName=typeName;
	}

	private LinkData internalConvertLink(String link) {
		/* FIXME ATR, 06.02.2017:  logic about link resolving (method) should be used in
		 * language element estimator also! currently the first potential matching method is used! */
		if (link == null) {
			return null;
		}
		if (!isLinkSchemaConvertable(link)) {
			return null;
		}
		try {
			link = URLDecoder.decode(link,UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		String typeName = StringUtils.substring(link, DSLConstants.HYPERLINK_TYPE_PREFIX.length());
		if (StringUtils.isBlank(typeName)) {
			return null;
		}

		LinkData data = new LinkData();
		int index = typeName.indexOf("#");
		if (index==-1){
			data.mainName = typeName;
			return data;
		}
		String[] splitted = StringUtils.split(typeName, "#");
		if (splitted == null || splitted.length ==0 ) {
			data.mainName = typeName;
			return data;
		}
		String methodPart=null;
		if (splitted.length==1){
			data.mainName=null;
			methodPart = splitted[0];
		}else{
			data.mainName = splitted[0];
			methodPart = splitted[1];
		}

		
		if (methodPart == null) {
			return data;
		}
		if (methodPart.indexOf("(") == -1) {
			data.subName=methodPart; // property...
			return data;
		}
		String[] methodPartArray = StringUtils.split(methodPart, "()");
		if (methodPartArray == null || methodPartArray.length != 2) {
			return data;
		}
		String methodName = methodPartArray[0];
		if (methodName==null){
			return data;
		}
		data.subName=methodName;
		
		String methodParams= methodPartArray[1];
		if (methodParams==null){
			return data;
		}
		String[] params = StringUtils.split(methodParams, ",");
		data.parameterNames=new String[params.length];
		for (int i=0;i<params.length;i++){
			String param = params[i];
			
			param = param.trim(); // " String" will be transformed to "String"
			String[] paramSplited = StringUtils.split(param);
			param = paramSplited[0]; //"String text" will be transformed to "String"
			if (paramSplited.length>1){
				data.parameterNames[i]=paramSplited[1];
			}else{
				data.parameterNames[i]="param"+i;
			}
			params[i]=param;
		}
		data.parameterTypes = params;
		return data;
	}

	public boolean isLinkSchemaConvertable(String target) {
		if (target == null) {
			return false;
		}
		return target.startsWith(DSLConstants.HYPERLINK_TYPE_PREFIX);
	}
}

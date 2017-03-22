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
			/* no property or method - just plain type, so guard close */
			data.mainName = typeName;
			return data;
		}
		return handleMethodOrProperty(typeName, data);
	}


	private LinkData handleMethodOrProperty(String typeName, LinkData data) {
		String[] splitted = StringUtils.split(typeName, "#");
		if (splitted == null || splitted.length ==0 ) {
			/* should never happen, but...*/
			data.mainName = typeName;
			return data;
		}
		String methodOrPropertyPart=null;
		if (splitted.length==1){
			data.mainName=null;
			methodOrPropertyPart = splitted[0];
		}else{
			data.mainName = splitted[0];
			methodOrPropertyPart = splitted[1];
		}
		
		if (methodOrPropertyPart == null) {
			return data;
		}
		if (methodOrPropertyPart.indexOf("(") == -1) {
			data.subName=methodOrPropertyPart; // property...
			return data;
		}
		return handleMethod(data, methodOrPropertyPart);
	}

	private LinkData handleMethod(LinkData data, String methodOrPropertyPart) {
		String[] methodPartArray = StringUtils.split(methodOrPropertyPart, "()");
		if (methodPartArray == null || methodPartArray.length ==0) {
			return data;
		}
		String methodName = methodPartArray[0];
		if (methodName==null){
			return data;
		}
		data.subName=methodName;
		return handleMethodParameters(data, methodPartArray);
	}

	private LinkData handleMethodParameters(LinkData data, String[] methodPartArray) {
		if (methodPartArray.length==1){
			/* no parameters set*/
			data.parameterTypes=new String[]{};
			return data;
		}
		String methodParams= methodPartArray[1];
		if (methodParams==null){
			return data;
		}
		addParameters(data, methodParams);
		return data;
	}

	private void addParameters(LinkData data, String methodParams) {
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
	}

	public boolean isLinkSchemaConvertable(String target) {
		if (target == null) {
			return false;
		}
		return target.startsWith(DSLConstants.HYPERLINK_TYPE_PREFIX);
	}
}

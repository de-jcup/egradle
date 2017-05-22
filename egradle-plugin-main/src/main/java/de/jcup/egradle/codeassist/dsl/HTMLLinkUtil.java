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

public class HTMLLinkUtil {
	/**
	 * Appends link to type
	 * 
	 * @param descSb
	 * @param withEndingDot
	 *            - when true at the end of string a dot will be placed - e.g.
	 *            "java.util.String."
	 * @param type
	 * @param linkPostfix
	 *            - can be null
	 */
	public static void appendLinkToType(StringBuilder descSb, boolean withEndingDot, Type type, String linkPostfix) {
		if (type == null) {
			descSb.append("void");
			return;
		}
		descSb.append("<a href='type://");
		descSb.append(type.getName());
		if (linkPostfix != null) {
			descSb.append(linkPostfix);
		}
		descSb.append("'>");
		descSb.append(type.getName());
		descSb.append("</a>");
		if (withEndingDot) {
			descSb.append('.');
		}
	}
}

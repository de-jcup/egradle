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
 package de.jcup.egradle.core.util;

public class TextUtil {
	
	/**
	 * Returns string containing only letters or digits starting from given offset. As soon as another character 
	 * appears in text scanning will stop.<br><br>
	 * Examples:
	 * <ul>
	 * <li>
	 * 	"alpha123 " will return "alpha123"
	 * </li>
	 * <li>
	 * 	"alpha123.abc" will return "alpha123"
	 * </li>
	 * <li>
	 * 	"alpha123(xx)" will return "alpha123"
	 * </li>
	 * </ul>
	 * @param offset
	 * @param text
	 * @return string never <code>null</code>
	 */
	public static String getLettersOrDigitsAt(int offset, String text){
		if (text==null){
			return "";
		}
		if (offset<0){
			return "";
		}
		int textLength = text.length();
		int pos=offset;
		if (pos>=textLength){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		while(true){
			if (pos>=textLength){
				break;
			}
			char c = text.charAt(pos++);
			if (!Character.isLetterOrDigit(c)){
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}

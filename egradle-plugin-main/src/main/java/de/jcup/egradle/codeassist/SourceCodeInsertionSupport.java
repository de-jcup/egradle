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
package de.jcup.egradle.codeassist;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class SourceCodeInsertionSupport {

	public static final String CURSOR_VARIABLE = "$cursor";
	
	private static Pattern REPLACE_CURSOR_POS= Pattern.compile("\\"+CURSOR_VARIABLE);
	
	/**
	 * Calculates insertion data
	 * @param oiriginInsertion - what shall be inserted (the "template")
	 * @param textBeforeColumn - what is before?
	 * @return insertion data
	 */
	public InsertionData prepareInsertionString(String oiriginInsertion, String textBeforeColumn) {
		return prepareInsertionString(oiriginInsertion,textBeforeColumn,true);
	}
	
	private InsertionData prepareInsertionString(String originInsertion, String textBeforeColumn, boolean ignoreIndentOnFirstLine) {
		InsertionData result = new InsertionData();
		if (StringUtils.isEmpty(originInsertion)){
			return result;
		}
		String insertion=originInsertion;
		/* first do indent if necessary */
		if (StringUtils.isNotEmpty(textBeforeColumn)){
			/* build indent*/
			String indention = transformTextBeforeToIndentString(textBeforeColumn);
			
			/* for each line append indent before...*/
			boolean endsWithNewLine=insertion.endsWith("\n");
			StringBuilder sb = new StringBuilder();
			String[] splitted = StringUtils.splitByWholeSeparator(insertion, "\n");
			for (int i=0;i<splitted.length;i++){
				boolean lastLineEmpty = endsWithNewLine;
				lastLineEmpty = lastLineEmpty && i==splitted.length-1;
				lastLineEmpty = lastLineEmpty && StringUtils.isEmpty(splitted[i]);
				
				boolean appendLine=!lastLineEmpty;
				if (!appendLine){
					continue;
				}
				if (i>0 || !ignoreIndentOnFirstLine){
					sb.append(indention);
				}
				sb.append(splitted[i]);
				sb.append("\n");
				
			}
			insertion=sb.toString();
			
		}
		result.cursorOffset = insertion.indexOf("$cursor");
		if (result.cursorOffset!=-1){
			result.sourceCode=REPLACE_CURSOR_POS.matcher(insertion).replaceAll("");
		}else{
			result.sourceCode=insertion;
		}
		return result;
		
	}

	/**
	 * Transform text before column to indent string. Purpose for this method is to have exact same column
	 * as on code completion before. Because text before can also contain parts of the code proposal itself
	 * the text before last whitespace will not be used for indention.<br><br>
	 * 
	 * An example:
	 * <pre>
	 * 	"123 cod" will be transformed to "    "
	 *  "  3 cod" will be transformed to "    "
	 *  "  3 co " will be transformed to "       "
	 * 
	 * </pre>
	 * @param textBeforeColumn
	 * @return transformed string never <code>null</code>
	 */
	String transformTextBeforeToIndentString(String textBeforeColumn) {
		if (textBeforeColumn==null){
			return "";
		}
		StringBuilder indentSb = new StringBuilder();
		int lastOriginWhitespacePos=-1;
		for (int i=0;i<textBeforeColumn.length();i++){
			char c = textBeforeColumn.charAt(i);
			if (Character.isWhitespace(c)){
				indentSb.append(c);
				lastOriginWhitespacePos=i+1;
			}else{
				indentSb.append(" ");
			}
		}
		if (lastOriginWhitespacePos==-1){
			return ""; // there were no origin whitespaces so do not indent
		}
		String indent =  indentSb.substring(0,lastOriginWhitespacePos);
		return indent;
	}
	
	
	public class InsertionData{
		String sourceCode="";
		int cursorOffset=-1;
		
		public String getSourceCode() {
			return sourceCode;
		}
		public int getCursorOffset() {
			return cursorOffset;
		}
	}

}

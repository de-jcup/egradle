package de.jcup.egradle.core.codecompletion;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class SourceCodeInsertionSupport {

	private static Pattern REPLACE_CURSOR_POS= Pattern.compile("\\$cursor");
	
	public InsertionData prepareInsertionString(String oiriginInsertion) {
		return prepareInsertionString(oiriginInsertion,null);
	}
	
	public InsertionData prepareInsertionString(String oiriginInsertion, String textBeforeColumn) {
		return prepareInsertionString(oiriginInsertion,textBeforeColumn,true);
	}
	
	public InsertionData prepareInsertionString(String originInsertion, String textBeforeColumn, boolean ignoreIndentOnFirstLine) {
		InsertionData result = new InsertionData();
		if (StringUtils.isEmpty(originInsertion)){
			return result;
		}
		String insertion=originInsertion;
		/* first do indent if necessary */
		if (StringUtils.isNotEmpty(textBeforeColumn)){
			/* build indent*/
			StringBuilder indentSb = new StringBuilder();
			for (int i=0;i<textBeforeColumn.length();i++){
				char c = textBeforeColumn.charAt(i);
				if (Character.isWhitespace(c)){
					indentSb.append(c);
				}else{
					indentSb.append(" ");
				}
			}
			
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
					sb.append(indentSb);
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
	
	
	public class InsertionData{
		public String sourceCode="";
		public int cursorOffset=-1;
	}

}

package de.jcup.egradle.codeassist;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class SourceCodeInsertionSupport {

	public static final String CURSOR_VARIABLE = "$cursor";
	
	private static Pattern REPLACE_CURSOR_POS= Pattern.compile("\\"+CURSOR_VARIABLE);
	
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
		public String sourceCode="";
		public int cursorOffset=-1;
	}

}

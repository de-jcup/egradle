package de.jcup.egradle.core.codecompletion;

import org.apache.commons.lang3.StringUtils;

/**
 * Reduces code to relevant parts for code completion
 * @author albert
 *
 */
public class RelevantCodeCutter {

	/**
	 * <pre>
					   xx fi bb 
					      ^-- fi
					   xx fi bb 
					        ^-- fi
					   xx .fi bb     
					      ^--- .fi
					   xx fi  bb
					         ^--- empty 
					         
					   xx
					   ^-- offset first character in line
					         ^-- other: given offset
					    01234 678 0123  
					    |
					    |-- offset 0 ->right 0, left nothing!
	 * </pre>
	 * 
	 * @param code when null always an empty string will be returned
	 * @param offset when negative always an empty string will be returned
	 * @return relevant code or empty
	 */
	public String getRelevantCode(String code, int offset) {
		if (code==null){
			return StringUtils.EMPTY;
		}
		if (offset<0){
			return StringUtils.EMPTY;
		}
		String leftText = getLeftText(code,offset);
		String rightText = getRightText(code,offset);
		return leftText+rightText;
	}

	private String getLeftText(String code, int offset) {
		char[] chars = code.toCharArray();
		int start = offset-1;
		if (start<0){
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i=start;i>=0;i--){
			char c = chars[i];
			if (isDelimiter(c)){
				break;
			}
			sb.append(c);
		}
		String result = StringUtils.reverse(sb.toString());
		return result;
	}
	
	private String getRightText(String code, int offset) {
		char[] chars = code.toCharArray();
		if (chars.length<=offset){
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i=offset;i<chars.length;i++){
			char c = chars[i];
			if (isDelimiter(c)){
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	private boolean isDelimiter(char c) {
		if (c==' '){
			return true;
		}
		if (c=='\n'){
			return true;
		}
		if (c=='\t'){
			return true;
		}
		return false;
	}
}

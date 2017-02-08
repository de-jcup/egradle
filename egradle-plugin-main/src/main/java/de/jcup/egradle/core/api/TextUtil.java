package de.jcup.egradle.core.api;

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

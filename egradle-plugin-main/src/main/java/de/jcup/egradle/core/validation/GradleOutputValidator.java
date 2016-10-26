package de.jcup.egradle.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import de.jcup.egradle.core.api.FormatConverter;

public class GradleOutputValidator {
	private static final String COLUMN_IDENTIFIER = ", column";
	private static final String LINE2 = "line:";
	private ValidationResult problem;
	private FormatConverter converter = new FormatConverter();
	private int pos;
	private int maxPos;
	private List<String> data;

	public ValidationResult validate(List<String> lines){
		Validate.notNull(lines);
		this.data=new ArrayList<>(lines);
		problem = new ValidationResult();
		
		maxPos = lines.size()-1;
		for (pos=0;pos<=maxPos;pos++){
			String line = data.get(pos);
			nextLine(line);
		}
		return problem;
	}

	private void nextLine(String line) {
		/* when not where found - try if where in this line otherwise gurad close exit...*/
		if (!problem.whereFound) {
			if (line.indexOf("* Where:") != -1) {
				problem.whereFound = true;
				return;
			}
			return;
		}
		/* where was found */
		handleScriptText(line);
		handleProblemMessage(line);
	}

	private void handleProblemMessage(String line) {
		if (problem.problemType != null) {
			if (problem.problemMessage==null){
				if (line.startsWith(">")) {
					StringBuilder sb = new StringBuilder();
					sb.append(
							line.substring(1).trim());
					
					int mpos = pos+1;
					/* inspect next lines*/
					while (mpos<=maxPos){
						String nextLine = data.get(mpos);
						if (nextLine.length()==0){
							break;
						}
						sb.append("\n");
						sb.append(nextLine);
						
						int columnIdIndex = nextLine.indexOf(COLUMN_IDENTIFIER);
						if (columnIdIndex!=-1){
							/* parse column information*/
							String columnString = nextLine.substring(columnIdIndex+COLUMN_IDENTIFIER.length());
							if (columnString.endsWith(".")){
								columnString=columnString.substring(0, columnString.length()-1);
							}
							int column = converter.convertToInt(columnString,-1);
							problem.column= column;
						}
						mpos++;
						
					}
					pos=mpos;// set pointer to end of former inspection
					problem.problemMessage = sb.toString();
					
				}
			}
			return;
		}
		if (!problem.whatWentWrongFound) {
			problem.whatWentWrongFound = line.indexOf("* What went wrong") != -1;
			return;
		}
		if (problem.problemType == null) {
			for (ProblemType type: ProblemType.values()){
				if (line.startsWith(type.text)) {
					problem.problemType = type;
					break;
				}
			}
		}

		if (problem.problemMessage != null) {
			return;
		}
		
	}

	private void handleScriptText(String line) {
		if (problem.line!=-1){
			return;
		}
		if (problem.problemMessage != null) {
			return;
		}
		/* try to fetch line data*/
		if (line.indexOf("line:")==-1){
			return;
		}
		int scriptTextStart = line.indexOf('\'');
		if (scriptTextStart == -1) {
			return;
		}
		String remaining = line.substring(scriptTextStart+1);
		int scriptTextEnd = remaining.indexOf("'");
		if (scriptTextEnd == -1) {
			return;
		}
		problem.problemScriptPath = remaining.substring(0, scriptTextEnd);
		int lineIndex = remaining.indexOf(LINE2);
		if (lineIndex == -1) {
			return;
		}
		String lineNr = remaining.substring(lineIndex+LINE2.length());
		problem.line = converter.convertToInt(lineNr.trim());
	}
}

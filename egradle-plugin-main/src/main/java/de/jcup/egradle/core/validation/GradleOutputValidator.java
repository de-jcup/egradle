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
package de.jcup.egradle.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import de.jcup.egradle.core.util.FormatConverter;

public class GradleOutputValidator {
	private static final String COLUMN_IDENTIFIER = ", column";
	private static final String LINE2 = "line:";
	private ValidationResult problem;
	private FormatConverter converter = new FormatConverter();
	private int pos;
	private int maxPos;
	private List<String> data;

	public ValidationResult validate(List<String> lines) {
		Validate.notNull(lines);
		this.data = new ArrayList<>(lines);
		problem = new ValidationResult();

		maxPos = lines.size() - 1;
		for (pos = 0; pos <= maxPos; pos++) {
			String line = data.get(pos);
			nextLine(line);
		}
		return problem;
	}

	void nextLine(String line) {
		/* do NOT reorder!*/
		inspectLine_1_whereFound(line);
		inspectLine_2_whatWentWrongFound(line);
		inspectLine_3_scriptPathAndLineInformation(line);
		inspectLine_4_problemType(line);
		inspectLine_5_problemMessage(line);
	}

	private void inspectLine_1_whereFound(String line) {
		/*
		 * when not where found - try if where in this line otherwise guard
		 * close exit...
		 */
		if (!problem.whereFound) {
			if (line.indexOf("* Where:") != -1) {
				problem.whereFound = true;
				return;
			}
			return;
		}
	}

	private void inspectLine_2_whatWentWrongFound(String line) {
		if (!problem.whatWentWrongFound) {
			problem.whatWentWrongFound = (line.indexOf("* What went wrong") != -1);
		}
	}

	private void inspectLine_3_scriptPathAndLineInformation(String line) {
		/* when problem line already detected - guard close */
		if (problem.line != -1) {
			return;
		}
		/* when problem message already fetched - guard close */
		if (problem.problemMessage != null) {
			return;
		}
		/* try to fetch line data */
		if (line.indexOf("line:") == -1) {
			return;
		}
		int scriptTextStart = line.indexOf('\'');
		if (scriptTextStart == -1) {
			return;
		}
		String remaining = line.substring(scriptTextStart + 1);
		int scriptTextEnd = remaining.indexOf("'");
		if (scriptTextEnd == -1) {
			return;
		}
		problem.problemScriptPath = remaining.substring(0, scriptTextEnd);
		int lineIndex = remaining.indexOf(LINE2);
		if (lineIndex == -1) {
			return;
		}
		String lineNr = remaining.substring(lineIndex + LINE2.length());
		problem.line = converter.convertToInt(lineNr.trim());
	}

	private void inspectLine_4_problemType(String line) {
		/* problem message already found - guard close */
		if (problem.problemMessage != null) {
			return;
		}

		/* detect problem type */
		if (problem.problemType == null) {
			for (ProblemType type : ProblemType.values()) {
				if (line.startsWith(type.text)) {
					problem.problemType = type;
					break;
				}
			}
		}
	}

	private void inspectLine_5_problemMessage(String line) {
		/* problem message already found - guard close */
		if (problem.problemMessage != null) {
			return;
		}

		/* problem type detection must be done before */
		if (problem.problemType != null) {
			if (problem.problemMessage == null) {
				if (line.startsWith(">")) {
					StringBuilder sb = new StringBuilder();
					sb.append(line.substring(1).trim());

					int mpos = pos + 1;
					/* inspect next lines */
					while (mpos <= maxPos) {
						String nextLine = data.get(mpos);
						if (nextLine.length() == 0) {
							break;
						}
						sb.append("\n");
						sb.append(nextLine);

						int columnIdIndex = nextLine.indexOf(COLUMN_IDENTIFIER);
						if (columnIdIndex != -1) {
							/* parse column information */
							String columnString = nextLine.substring(columnIdIndex + COLUMN_IDENTIFIER.length());
							if (columnString.endsWith(".")) {
								columnString = columnString.substring(0, columnString.length() - 1);
							}
							int column = converter.convertToInt(columnString, -1);
							problem.column = column;
						}
						mpos++;

					}
					pos = mpos;// set pointer to end of former inspection
					problem.problemMessage = sb.toString();

				}
			}
			return;
		}

	}
}

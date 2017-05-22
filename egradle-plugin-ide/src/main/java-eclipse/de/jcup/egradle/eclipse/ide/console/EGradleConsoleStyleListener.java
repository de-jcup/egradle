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
package de.jcup.egradle.eclipse.ide.console;

import static de.jcup.egradle.eclipse.ide.console.EGradleConsoleColorsConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.ide.IDEActivator;

public class EGradleConsoleStyleListener implements LineStyleListener {
	private final static Collection<ParseData> SHARED_PARSE_DATA = new ArrayList<>();
	static {
		addParseDataByIndex("BUILD SUCCESSFUL", GREEN);
		addParseDataByIndex("BUILD FAILED", RED);
		addParseDataByIndex("SKIPPED", GRAY);
		addParseDataByIndex("UP-TO-DATE", ORANGE);
		addParseDataByIndex("FAILURE:", BRIGHT_RED);
		addParseDataByIndex("test FAILED", RED);
		addParseDataByIndex("* What went wrong:", DARK_GRAY);
		addParseDataByIndex("* Try:", DARK_GRAY);
		addParseDataByIndex("WARNING", BRIGHT_RED);
		addParseDataByIndex("warning", BRIGHT_RED);
		addParseDataByIndex("There were failing tests. See the results at:", BRIGHT_RED);
		
		/* dependencies output */
		addParseDataByIndex("+---", BRIGHT_BLUE);
		addParseDataByIndex("\\---", BRIGHT_BLUE);
		addParseDataByIndex("|", BRIGHT_BLUE);
	}

	static final void addParseDataByIndex(String substring, RGB color) {
		addParseDataByIndex(substring, color,false);
	}
	static final void addParseDataByIndex(String substring, RGB color, boolean bold) {
		ParseData data = new ParseData();
		data.subString = substring;
		data.color = color;
		data.bold=bold;
		SHARED_PARSE_DATA.add(data);
	}

	int lastRangeEnd = 0;

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		if (event == null) {
			return;
		}
		String lineText = event.lineText;
		if (StringUtils.isBlank(lineText)){
			return;
		}
		/* styling */
		StyleRange defStyle;

		boolean atLeastOneStyle = event.styles != null && event.styles.length > 0;
		if (atLeastOneStyle) {
			defStyle = (StyleRange) event.styles[0].clone();
			if (defStyle.background == null) {
				defStyle.background = getColor(BLACK);
			}
		} else {
			defStyle = new StyleRange(1, lastRangeEnd, getColor(BLACK), getColor(WHITE), SWT.NORMAL);
		}

		lastRangeEnd = 0;

		
		List<StyleRange> ranges = new ArrayList<StyleRange>();
		boolean handled = false;
		/* line text */
		if (!handled) {
			if (StringUtils.containsOnly(lineText, "-")) {
				/* only a marker line from gradle */
				addRange(ranges, event.lineOffset, lineText.length(),
						getColor(EGradleConsoleColorsConstants.BRIGHT_BLUE), true);
				handled=true;
			}
		}
		handled = handled || markLine(event, lineText, ranges, handled, SimpleProcessExecutor.MESSAGE__EXECUTION_CANCELED_BY_USER,  BRIGHT_BLUE, true, BLUE,false);
		handled = handled || markLine(event, lineText, ranges, handled, "> Could not find", RED, false, BRIGHT_RED,false);
		handled = handled || markLine(event, lineText, ranges, handled, "Could not resolve all dependencies for configuration", RED, false, BRIGHT_RED,false);
		handled = handled || markLine(event, lineText, ranges, handled, "Could not resolve:", RED, false, BRIGHT_RED,false);
		handled = handled || markLine(event, lineText, ranges, handled, "Could not resolve", RED, false, RED,false);
		handled = handled || markLine(event, lineText, ranges, handled, "Download", BLUE, false, BRIGHT_BLUE,false);
		/* index parts and other*/
		if (!handled) {
			for (ParseData data : SHARED_PARSE_DATA) {
				parse(event, defStyle, lineText, ranges, data);
			}
		}

		if (!ranges.isEmpty()) {
			event.styles = ranges.toArray(new StyleRange[ranges.size()]);
		}
	}

	private boolean markLine(LineStyleEvent event, String lineText, List<StyleRange> ranges, boolean handled,
			String searchText, RGB color1, boolean bold1, RGB color2, boolean bold2) {
		if (!handled) {
			
			if (lineText.startsWith(searchText)) {
				/* download itself is rendered by parsedata, here we only markup the remianing links*/
				addRange(ranges, event.lineOffset, searchText.length(),
						getColor(color1), bold1);
				addRange(ranges, event.lineOffset+searchText.length(), lineText.length(),
						getColor(color2), bold2);
				handled=true;
			}
		}
		return handled;
	}

	private void parse(LineStyleEvent event, StyleRange defStyle, String currentText, List<StyleRange> ranges,
			ParseData data) {
		if (data.isSearchingSimpleSubstring()) {
			parseByIndex(event, defStyle, currentText, ranges, data);
		} else {
			throw new UnsupportedOperationException("Unsupported/unimplemented");
		}

	}

	private void parseByIndex(LineStyleEvent event, StyleRange startStyle, String currentText, List<StyleRange> ranges,
			ParseData data) {
		int fromIndex = 0;
		int pos = 0;
		int length = currentText.length();
		do {
			if (fromIndex >= length) {
				break;
			}
			pos = currentText.indexOf(data.subString, fromIndex);
			fromIndex = pos + 1;

			if (pos != -1) {
				addRange(ranges, event.lineOffset + pos, data.subString.length(), getColor(data.color), data.bold);
			}
		} while (pos != -1);
	}

	private Color getColor(RGB rgb) {
		return IDEActivator.getDefault().getColorManager().getColor(rgb);
	}

	private static class ParseData {
		public boolean bold;
		private String subString;
		private RGB color;

		private boolean isSearchingSimpleSubstring() {
			return subString != null;
		}
	}
	private void addRange(List<StyleRange> ranges, int start, int length, Color foreground, boolean bold) {
		addRange(ranges, start, length, foreground, null, bold);
	}
	private void addRange(List<StyleRange> ranges, int start, int length, Color foreground, Color background, boolean bold) {
		StyleRange range = new StyleRange(start, length, foreground, background);
		if (bold){
			range.fontStyle=SWT.BOLD;
		}
		ranges.add(range);
		lastRangeEnd = lastRangeEnd + range.length;
	}
}
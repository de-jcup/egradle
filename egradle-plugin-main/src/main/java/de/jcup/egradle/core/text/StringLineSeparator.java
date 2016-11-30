package de.jcup.egradle.core.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StringLineSeparator {

	private boolean traceEnabled;

	public List<String> readLinesWithLineDelimiter(InputStream is, String charSetName) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, charSetName));
		int d = 0;
		int i = 0;
		StringBuilder sb = null;

		List<String> result = new ArrayList<>();

		while (true) {
			/*
			 * why not using br.readline() - because the lines shall have all
			 * keep their line endings (\r, \r\n or \n)
			 */
			d = br.read();
			if (d == -1) {
				if (sb != null) {
					addLine(result, i, sb);
				}
				break;
			}
			if (sb == null) {
				sb = new StringBuilder();
			}
			char c = (char) d;
			sb.append(c);

			if (c == '\n') {
				i = addLine(result, i, sb);
				sb = null;
			} else if (c == '\r') {
				/* check if this is a \r\n */
				d = br.read();
				if (d == -1) {
					sb.append(c);
					i = addLine(result, i, sb);
					break;
				}
				c = (char) d;
				if (c == '\n') {
					sb.append(c);
					i = addLine(result, i, sb);
					sb = null;
				} else {
					i = addLine(result, i, sb);
					sb = null;
					sb = new StringBuilder();
					sb.append(c);
				}
			}

		}
		return result;
	}

	private int addLine(List<String> result, int lineNr, StringBuilder sb) {
		String line = sb.toString();
		result.add(line);
		if (traceEnabled) {
			trace(lineNr + ":" + line);
		}
		return lineNr++;
	}

	private void trace(String string) {
		System.out.println("trace:" + string);

	}
}

package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import java.util.Collection;

public class AssertTextLines {

	private Collection<TextLine> lines;

	private AssertTextLines(Collection<TextLine> lines) {
		this.lines = lines;
	}

	public AssertTextLines hasLines(int amount) {
		assertEquals("Not expected amounts of lines!", amount, lines.size());
		return this;
	}

	public AssertTextLines containsOnly(TextLine... expectedLines) {
		if (expectedLines == null) {
			throw new IllegalArgumentException("test case wrong written! lines may not be null!");
		}
		hasLines(expectedLines.length);
		return contains(expectedLines);
	}

	public AssertTextLines contains(TextLine... expectedLines) {
		if (expectedLines == null) {
			throw new IllegalArgumentException("test case wrong written! lines may not be null!");
		}
		String failureMessage = null;
		for (TextLine line : expectedLines) {
			if (!this.lines.contains(line)){
				StringBuilder sb = new StringBuilder();
				sb.append("Did not found:");
				sb.append(line.toString());
				sb.append(" but only:\n");
				
				for (TextLine existingLine: lines){
					sb.append(existingLine.toString());
					sb.append("\n");
				}
				failureMessage=sb.toString();
				break;
			}
		}
		if (failureMessage!=null){
			fail(failureMessage);
		}
		return this;
	}

	public static AssertTextLines assertLines(Collection<TextLine> lines) {
		assertNotNull("given lines was null!", lines);
		return new AssertTextLines(lines);
	}

}
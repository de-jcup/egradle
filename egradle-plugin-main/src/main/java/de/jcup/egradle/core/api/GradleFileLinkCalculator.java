package de.jcup.egradle.core.api;

public class GradleFileLinkCalculator {

	private static final String IDENTIFIER = ".gradle";
	private static final int IDENTIFIER_LENGTH = IDENTIFIER.length();
	private GradleStringTransformer transformer;


	public GradleFileLinkResult createFileLinkString(String line, int offsetInLine) {
		if (line == null) {
			return null;
		}
		int lineLength = line.length();
		if (offsetInLine >= lineLength) {
			return null;
		}
		int gradleSeparatorOffset = line.indexOf(IDENTIFIER);
		if (gradleSeparatorOffset < 0) {
			// contains no .gradle inside so not interesting
			return null;
		}
		int offsetAfterGradleSeparator = gradleSeparatorOffset + IDENTIFIER_LENGTH;
		if (offsetAfterGradleSeparator >= lineLength) {
			return null;
		}
		char c = line.charAt(offsetAfterGradleSeparator);
		boolean foundQuote = false;
		foundQuote = foundQuote || c == '\'';
		foundQuote = foundQuote || c == '\"';
		if (!foundQuote) {
			return null;
		}
		int isUrlSeparatorOffset = line.indexOf("://");
		if (isUrlSeparatorOffset >= 0) {
			// Not a gradle file link but an URL.
			return null;
		}

		char quote = 0;
		int urlOffsetInLine = 0;
		int urlLength = 0;

		// left to ".gradle")
		urlOffsetInLine = gradleSeparatorOffset;
		char ch;
		do {
			urlOffsetInLine--;
			ch = ' ';
			if (urlOffsetInLine > -1) {
				ch = line.charAt(urlOffsetInLine);
			}
			if (ch == '"' || ch == '\'') {
				quote = ch;
			}
			if (urlOffsetInLine < 0) {
				return null;
			}
		} while (quote == 0);
		urlOffsetInLine++;

		/* calculate end offset by resolving quote */
		if (quote != 0) {
			int endOffset = -1;
			int nextQuote = line.indexOf(quote, urlOffsetInLine);
			int nextWhitespace = line.indexOf(' ', urlOffsetInLine);
			if (nextQuote != -1 && nextWhitespace != -1) {
				endOffset = Math.min(nextQuote, nextWhitespace);
			} else if (nextQuote != -1) {
				endOffset = nextQuote;
			} else if (nextWhitespace != -1) {
				endOffset = nextWhitespace;
			}
			if (endOffset != -1) {
				urlLength = endOffset - urlOffsetInLine;
			}
		}

		String linkContent = line.substring(urlOffsetInLine, urlOffsetInLine + urlLength);
		 if (transformer!=null){
			 String transformed = transformer.transform(linkContent);
			 
			 if (transformed!=null){
				 linkContent=transformed;
			 }
			 
		 }

		GradleFileLinkResult result = new GradleFileLinkResult();
		result.linkOffsetInLine = urlOffsetInLine;
		result.linkContent = linkContent;
		result.linkLength = urlLength;
		return result;
	}

	public class GradleFileLinkResult {
		@Override
		public String toString() {
			return "GradleFileLinkResult [linkContent=" + linkContent + ", linkOffsetInLine=" + linkOffsetInLine
					+ ", linkLength=" + linkLength + "]";
		}

		public String linkContent;
		public int linkOffsetInLine;
		public int linkLength;

	}

	/**
	 * Set gradle string transformer which is used to transform link texts. If
	 * the transformer returns null for a link text the result will not be used!
	 * 
	 * @param transformer
	 */
	public void setTransformer(GradleStringTransformer transformer) {
		this.transformer = transformer;
	}
}

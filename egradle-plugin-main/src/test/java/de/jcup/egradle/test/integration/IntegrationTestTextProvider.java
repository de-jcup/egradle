package de.jcup.egradle.test.integration;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.TextProviderException;

public class IntegrationTestTextProvider implements TextProvider {

	private String text;

	IntegrationTestTextProvider(String text) {
		this.text = text;
	}

	@Override
	public String getText(int offset, int length) throws TextProviderException {
		try {
			return text.substring(offset, offset+length);
		} catch (RuntimeException e) {
			throw new TextProviderException("cannot get text", e);
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public int getLineOffset(int offset) throws TextProviderException {
		/* lines in read textfiles will always contain \n... */
		String[] lines = StringUtils.splitByWholeSeparator(text, "\n");
		if (offset==0){
			return 0;
		}
		int linePos = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int ending = linePos + line.length() + 1;// +1 because of \n was
														// removed before
			if (offset >= linePos && offset <= ending) {
				return linePos;
			}
			linePos = ending + 1;
		}
		throw new TextProviderException("Cannot determine line offset for:" + offset);
	}

}

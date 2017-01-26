package de.jcup.egradle.core.api;

public class GradleHyperLinkResult {
	@Override
	public String toString() {
		return "GradleHyperLinkResult [linkContent=" + linkContent + ", linkOffsetInLine=" + linkOffsetInLine
				+ ", linkLength=" + linkLength + "]";
	}

	public String linkContent;
	public int linkOffsetInLine;
	public int linkLength;

}
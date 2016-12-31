package de.jcup.egradle.eclipse.gradleeditor.codecompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;

/* FIXME ATR, 31.12.2016 - make a generic approach, independent from eclipse, so easy testable*/
public class GradleContentAssistProcessor implements IContentAssistProcessor {
	private String errorMessage;

	private String[] defaultProposals = new String[] { "apply plugin: \"\"","apply from: \"\"" };

	public GradleContentAssistProcessor(IAdaptable adaptable) {

	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();

		try {
			int lineOfOffset = document.getLineOfOffset(offset);
			int lineOffset = document.getLineOffset(lineOfOffset);

//			/*
//			 * do not show any content assist in case the offset is not at the
//			 * beginning of a line
//			 */
//			if (offset != lineOffset) {
//				return new ICompletionProposal[0];
//			}
		} catch (BadLocationException e) {
			return new ICompletionProposal[0];
		}
		List<ICompletionProposal> list = new ArrayList<>();
		for (int i=0;i<defaultProposals.length;i++){
			String propsal = defaultProposals[i];
			Image image = EGradleUtil.getImage("/icons/gradle-og.png", Activator.PLUGIN_ID);
			String displayString=propsal+":"+offset;
			IContextInformation contextInformation =null;
			String additionalProposalInfo = "<html><b>additional</b> proposal information<table><tr><td>a1</td><td>a2</td></tr><tr><td>a1</td><td>a2</td></tr></table></html>";
			EnhancedProposal proposal = new EnhancedProposal(propsal, offset, propsal.length(), offset+propsal.length(),image,displayString,contextInformation,additionalProposalInfo);
			list.add(proposal);
		}
		return list.toArray(new ICompletionProposal[list.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		ContextInformation info = new ContextInformation("contextDisplayString:"+offset, "informationDisplayString");
		return new IContextInformation[]{info};
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}

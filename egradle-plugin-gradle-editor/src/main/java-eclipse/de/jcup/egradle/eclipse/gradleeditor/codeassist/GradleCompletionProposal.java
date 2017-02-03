package de.jcup.egradle.eclipse.gradleeditor.codeassist;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class GradleCompletionProposal implements ICompletionProposal, ICompletionProposalExtension3 {

	/** The string to be displayed in the completion proposal popup. */
	private String fDisplayString;
	/** The replacement string. */
	private String fReplacementString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The replacement length. */
	private int fReplacementLength;
	/** The cursor position after this proposal has been applied. */
	private int fCursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	private Image fImage;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;
	private IInformationControlCreator informationControlCreator;
	private LazyLanguageElementHTMLDescriptionBuilder lazyBuilder;


	/**
	 * Creates a new completion proposal. All fields are initialized based on
	 * the provided information.
	 *
	 * @param replacementString
	 *            the actual string to be inserted into the document
	 * @param replacementOffset
	 *            the offset of the text to be replaced
	 * @param replacementLength
	 *            the length of the text to be replaced
	 * @param cursorPosition
	 *            the position of the cursor following the insert relative to
	 *            replacementOffset
	 * @param image
	 *            the image to display for this proposal
	 * @param displayString
	 *            the string to be displayed for the proposal
	 * @param contextInformation
	 *            the context information associated with this proposal
	 * @param additionalProposalInfo
	 *            the additional information associated with this proposal
	 * @param lazyBuilder lazy builder or <code>null</code>
	 */
	public GradleCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, LazyLanguageElementHTMLDescriptionBuilder lazyBuilder) {
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isTrue(cursorPosition >= 0);

		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fImage = image;
		fDisplayString = displayString;
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
		
		this.lazyBuilder=lazyBuilder;
	}

	@Override
	public void apply(IDocument document) {
		try {
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	@Override
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	@Override
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	@Override
	public Image getImage() {
		return fImage;
	}

	@Override
	public String getDisplayString() {
		if (fDisplayString != null){
			return fDisplayString;
		}
		return fReplacementString;
	}

	@Override
	public String getAdditionalProposalInfo() {
		if (lazyBuilder!=null){
			
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING){
				System.out.println(getClass().getSimpleName()+":lazyBuilder creating html called!");
			}
			
			return lazyBuilder.createHTML();
		}
		return fAdditionalProposalInfo;
	}

	/* -------------------------------- */
	/* - CompletionProposalExtension3 - */
	/* -------------------------------- */
	@Override
	public IInformationControlCreator getInformationControlCreator() {
		if (informationControlCreator == null) {
			informationControlCreator=new EGradleInformationControlCreator();
		}
		return informationControlCreator;
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		return null;
	}

	@Override
	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return 0;
	}
	
	protected static class EGradleInformationControlCreator extends AbstractReusableInformationControlCreator {
		@Override
		public IInformationControl doCreateInformationControl(Shell shell) {
			if (SimpleBrowserInformationControl.isAvailableFor(shell)){
				return new SimpleBrowserInformationControl(shell,5);
			}
			return new DefaultInformationControl(shell, true);
		}
	}
}

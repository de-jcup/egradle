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

import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.eclipse.gradleeditor.DefaultEGradleLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class GradleCompletionProposal implements ICompletionProposal, ICompletionProposalExtension3 /*, ICompletionProposalExtension6*/ {

	/** The string to be displayed in the completion proposal popup. */
	private String fDisplayString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The replacement length. */
	private int fReplacementLength;
	
	/** The image to be displayed in the completion proposal popup. */
	private Image fImage;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;
	private IInformationControlCreator informationControlCreator;
	private LazyLanguageElementHTMLDescriptionBuilder lazyBuilder;

	private LazyCursorMovement movement;
	private Proposal proposal;

	/**
	 * Creates a new completion proposal. All fields are initialized based on
	 * the provided information.
	 * @param proposal 
	 *
	 * @param replacementOffset
	 *            the offset of the text to be replaced
	 * @param replacementLength
	 *            the length of the text to be replaced
	 * @param image
	 *            the image to display for this proposal
	 * @param contextInformation
	 *            the context information associated with this proposal
	 * @param additionalProposalInfo
	 *            the additional information associated with this proposal
	 * @param lazyHtmlBuilder lazy builder or <code>null</code>
	 */
	public GradleCompletionProposal(Proposal proposal, int replacementOffset, int replacementLength, Image image, IContextInformation contextInformation, String additionalProposalInfo, LazyLanguageElementHTMLDescriptionBuilder lazyHtmlBuilder) {
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isNotNull(proposal);
		this.proposal=proposal;
		this.movement = new LazyCursorMovement(proposal);
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fImage = image;
		fDisplayString = proposal.getLabel();
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
		
		this.lazyBuilder=lazyHtmlBuilder;
	}

	@Override
	public void apply(IDocument document) {
		try {
			document.replace(fReplacementOffset, fReplacementLength, proposal.getCode());
		} catch (BadLocationException x) {
			// ignore
		}
	}

	@Override
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + movement.getCursorMove(), 0);
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
		return "";
	}
	
	@Override
	public String getAdditionalProposalInfo() {
		if (lazyBuilder!=null){
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
	
	protected class EGradleInformationControlCreator extends AbstractReusableInformationControlCreator {
		@Override
		public IInformationControl doCreateInformationControl(Shell shell) {
			if (SimpleBrowserInformationControl.isAvailableFor(shell)){
				SimpleBrowserInformationControl control = new SimpleBrowserInformationControl(shell,20);
				if (lazyBuilder!=null){
					control.setBrowserEGradleLinkListener(new DefaultEGradleLinkListener(lazyBuilder.getFgColor(), lazyBuilder.getBgColor(), lazyBuilder.getCommentColor(), lazyBuilder.getBuilder()));
				}
				return control;
			}
			return new DefaultInformationControl(shell, true);
		}
	}


}

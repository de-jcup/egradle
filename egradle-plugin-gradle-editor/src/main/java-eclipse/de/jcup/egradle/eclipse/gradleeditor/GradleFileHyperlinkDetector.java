/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this editorFile except in compliance with the License.
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
package de.jcup.egradle.eclipse.gradleeditor;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.egradle.core.api.GradleFileLinkCalculator;
import de.jcup.egradle.core.api.GradleFileLinkCalculator.GradleFileLinkResult;
import de.jcup.egradle.eclipse.api.EclipseResourceHelper;

/* FIXME ATR: implement correct!*/
/* FIXME ATR: The xml definition in plugin.xml makes no sense, because Source config returns fixed array of hyperlink detectors and this one was added extra to work!*/
public class GradleFileHyperlinkDetector extends AbstractHyperlinkDetector {

	private File editorFile;
	public GradleFileHyperlinkDetector(IAdaptable adaptable) {
		Assert.isNotNull(adaptable, "Adaptable may not be null!");
		IFile ifile = adaptable.getAdapter(IFile.class);
		try {
			editorFile = getResourceHelper().toFile(ifile);
		} catch (CoreException e) {
			/*
			 * if not working - ignore, so hyper link detection will return null
			 */
		}

	}

	private EclipseResourceHelper getResourceHelper() {
		return EclipseResourceHelper.DEFAULT;
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (editorFile == null) {
			return null;
		}
		if (region == null) {
			return null;
		}
		if (textViewer == null) {
			return null;
		}
		IDocument document = textViewer.getDocument();
		if (document == null) {
			return null;
		}

		int offset = region.getOffset();

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		int offsetInLine = offset - lineInfo.getOffset();

		GradleFileLinkCalculator linkHelper= new GradleFileLinkCalculator();
		GradleFileLinkResult result = linkHelper.createFileLinkString(line, offsetInLine);
		if (result==null){
			return null;
		}
		try {
			File folder = editorFile.getParentFile();
			File target = new File(folder, result.linkContent);
			if (!target.exists()) {
				return null;
			}
			IRegion urlRegion = new Region(lineInfo.getOffset() + result.linkOffsetInLine, result.linkLength);

			IFileStore fileStore = EFS.getLocalFileSystem().getStore(target.toURI());
			if (fileStore==null){
				return null;
			}
			GradleFileHyperlink	gradleFileHyperlink = new GradleFileHyperlink(urlRegion, fileStore);
			return new IHyperlink[] { gradleFileHyperlink };
		} catch (RuntimeException e) {
			return null;
		}

	}

}

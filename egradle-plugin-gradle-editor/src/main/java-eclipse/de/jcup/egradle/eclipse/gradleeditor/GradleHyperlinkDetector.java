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

import de.jcup.egradle.core.util.GradleFileLinkCalculator;
import de.jcup.egradle.core.util.GradleHyperLinkResult;
import de.jcup.egradle.core.util.GradleResourceLinkCalculator;
import de.jcup.egradle.core.util.GradleStringTransformer;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;

/**
 * Hyperlink detector for all kind of hyperlinks in egradle editor.
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleHyperlinkDetector extends AbstractHyperlinkDetector {
    private GradleStringTransformer transformer;
    private File editorFile;

    public GradleHyperlinkDetector(IAdaptable adaptable) {
        Assert.isNotNull(adaptable, "Adaptable may not be null!");
        IFile ifile = adaptable.getAdapter(IFile.class);
        try {
            editorFile = getResourceHelper().toFile(ifile);
        } catch (CoreException e) {
            /*
             * if not working - ignore, so hyper link detection will return null
             */
        }
        transformer = adaptable.getAdapter(GradleStringTransformer.class);

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

        GradleFileLinkCalculator linkCalculator = new GradleFileLinkCalculator();
        linkCalculator.setTransformer(transformer);
        GradleHyperLinkResult result = linkCalculator.createFileLinkString(line, offsetInLine);
        if (result != null) {
            IHyperlink[] fileLink = handleFileLink(lineInfo, result);
            if (fileLink != null) {
                /* was resolveable as file link - so return */
                return fileLink;
            }
        }
        /* not a file link so try as resource */
        GradleResourceLinkCalculator resCalculator = new GradleResourceLinkCalculator();
        result = resCalculator.createResourceLinkString(line, offsetInLine);
        if (result != null) {
            return handlerResourceLink(textViewer, lineInfo, result);
        }
        return null;

    }

    private IHyperlink[] handlerResourceLink(ITextViewer textViewer, IRegion lineInfo, GradleHyperLinkResult result) {
        try {
            IRegion urlRegion = new Region(lineInfo.getOffset() + result.linkOffsetInLine, result.linkLength);
            IDocument document = textViewer.getDocument();
            String fullText = null;
            if (document != null) {
                fullText = document.get();
            }
            GradleResourceHyperlink gradleResourceLink = new GradleResourceHyperlink(urlRegion, result.linkContent, fullText);
            return new IHyperlink[] { gradleResourceLink };
        } catch (RuntimeException e) {
            return null;
        }
    }

    private IHyperlink[] handleFileLink(IRegion lineInfo, GradleHyperLinkResult result) {
        try {
            File folder = editorFile.getParentFile();
            String fileName = result.linkContent;

            File target = new File(folder, fileName);
            if (!target.exists()) {
                target = new File(fileName);
            }
            if (!target.exists()) {
                return null;
            }

            IFileStore fileStore = EFS.getLocalFileSystem().getStore(target.toURI());
            if (fileStore == null) {
                return null;
            }
            IRegion urlRegion = new Region(lineInfo.getOffset() + result.linkOffsetInLine, result.linkLength);
            GradleFileHyperlink gradleFileHyperlink = new GradleFileHyperlink(urlRegion, fileStore);
            return new IHyperlink[] { gradleFileHyperlink };
        } catch (RuntimeException e) {
            return null;
        }
    }

}

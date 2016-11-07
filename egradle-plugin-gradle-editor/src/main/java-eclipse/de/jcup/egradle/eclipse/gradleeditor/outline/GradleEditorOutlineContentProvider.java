package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.model.DefaultTokenOutlineModelBuilder;
import de.jcup.egradle.core.model.OutlineModel;
import de.jcup.egradle.core.model.OutlineModel.Item;
import de.jcup.egradle.core.token.filter.ClosingBracesFilter;
import de.jcup.egradle.core.token.filter.CommentFilter;
import de.jcup.egradle.core.token.filter.MultiTokenFilter;
import de.jcup.egradle.core.token.filter.ParameterFilter;
import de.jcup.egradle.core.token.filter.UnknownTokenFilter;
import de.jcup.egradle.core.token.parser.TokenParser;
import de.jcup.egradle.core.token.parser.TokenParserResult;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY = new Object[] {};

	private TokenParser parser = new TokenParser();

	private GradleEditor editor;

	private MultiTokenFilter filter;

	private OutlineModel model;
	
	private Object monitor = new Object();

	GradleEditorOutlineContentProvider(GradleEditor editor){
		this.editor=editor;
		
		filter = new MultiTokenFilter();
		filter.add(new ParameterFilter());
		filter.add(new CommentFilter());
		filter.add(new UnknownTokenFilter());
		filter.add(new ClosingBracesFilter());
	}

	@Override
	public Object[] getElements(Object inputElement) {
		 if (inputElement instanceof IDocument) {
			IDocument document = (IDocument) inputElement;
			String dataAsString = document.get();
			
			/* resolve charset to use - currently only workaround via editor instance */
			String charset = null;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput){
				IFileEditorInput fie= (IFileEditorInput)input;
				IFile file = fie.getFile();
				try {
					charset= file.getCharset();
				} catch (CoreException e) {
					EGradleUtil.log(e);
				}
			}
			
			try (InputStream is = new ByteArrayInputStream(dataAsString.getBytes())) {
				TokenParserResult ast = parser.parse(is, charset);
				
				DefaultTokenOutlineModelBuilder builder = new DefaultTokenOutlineModelBuilder(ast.getRoot(), filter);
				synchronized(monitor){
					model = builder.build();
				}
				return model.getRoot().getChildren();
			} catch (IOException e) {
				EGradleUtil.log("Was not able to parse string:" + dataAsString, e);
			}
		}
		return new Object[] { "no content" };
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Item) {
			Item item = (Item) parentElement;
			return item.getChildren();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			return item.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			return item.hasChildren();
		}
		return false;
	}
	
	public Item tryToFindByOffset(int offset){
		synchronized (monitor) {
			return model.getItemAt(offset);
		}
	}

}

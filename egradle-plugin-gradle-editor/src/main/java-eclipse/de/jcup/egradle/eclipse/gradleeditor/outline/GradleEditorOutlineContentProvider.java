package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.OutlineModelBuilder;
import de.jcup.egradle.core.outline.OutlineModelBuilder.OutlineModelBuilderException;
import de.jcup.egradle.core.outline.groovyantlr.GroovyASTOutlineModelBuilder;
import de.jcup.egradle.core.outline.groovyantlr.WantedOutlineModelBuilder;
import de.jcup.egradle.core.outline.token.DefaultTokenOutlineModelBuilder;
import de.jcup.egradle.core.token.parser.TokenParser;
import de.jcup.egradle.core.token.parser.TokenParserResult;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY = new Object[] {};

	private ModelType modelType;

	private TokenParser parser = new TokenParser();

	private GradleEditor editor;


	private OutlineModel model;

	private Object monitor = new Object();

	GradleEditorOutlineContentProvider(GradleEditor editor) {
		this.editor = editor;
	}

	public ModelType getModelType() {
		if (modelType == null) {
			modelType = ModelType.GROOVY_FULL_ANTLR;
		}
		return modelType;
	}

	public enum ModelType {
		TOKEN,

		GROOVY_FULL_ANTLR,

		WANTED
	}
	
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IDocument) {
			IDocument document = (IDocument) inputElement;
			String dataAsString = document.get();

			/*
			 * resolve charset to use - currently only workaround via editor
			 * instance
			 */
			String charset = null;
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fie = (IFileEditorInput) input;
				IFile file = fie.getFile();
				try {
					charset = file.getCharset();
				} catch (CoreException e) {
					EGradleUtil.log(e);
				}
			}

			try (InputStream is = new ByteArrayInputStream(dataAsString.getBytes())) {
				Object[] elements = null;
				ModelType type = getModelType();

				switch (type) {
				case TOKEN:
					elements = buildTokenModel(charset, is);
					break;
				case GROOVY_FULL_ANTLR:
					elements = buildGroovyASTModel(charset, is);
					break;
				case WANTED:
					elements = buildWantedModel(charset, is);
					break;
				default:
					elements = new Object[] { type + " not supported as modeltype!" };
				}
				return elements;
			} catch (Exception e) {
				EGradleUtil.log("Was not able to parse string:" + dataAsString, e);
			}
		}
		return new Object[] { "no content" };
	}

	private Object[] buildGroovyASTModel(String charset, InputStream is) throws Exception {
		GroovyASTOutlineModelBuilder builder = new GroovyASTOutlineModelBuilder(is);
		return createModelAndGetRootElements(builder);
	}

	private Object[] buildWantedModel(String charset, InputStream is) throws Exception {
		WantedOutlineModelBuilder builder = new WantedOutlineModelBuilder(is);
		return createModelAndGetRootElements(builder);
	}

	private Object[] buildTokenModel(String  charset, InputStream is) throws Exception {
		TokenParserResult ast = parser.parse(is, charset);
		OutlineModelBuilder builder = new DefaultTokenOutlineModelBuilder(ast.getRoot());
		return createModelAndGetRootElements(builder);
	}

	private Object[] createModelAndGetRootElements(OutlineModelBuilder builder) throws OutlineModelBuilderException {
		synchronized (monitor) {
			model = builder.build();
		}
		return model.getRoot().getChildren();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof OutlineItem) {
			OutlineItem outlineItem = (OutlineItem) parentElement;
			return outlineItem.getChildren();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem outlineItem = (OutlineItem) element;
			return outlineItem.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem outlineItem = (OutlineItem) element;
			return outlineItem.hasChildren();
		}
		return false;
	}

	public OutlineItem tryToFindByOffset(int offset) {
		synchronized (monitor) {
			return model.getItemAt(offset);
		}
	}

}

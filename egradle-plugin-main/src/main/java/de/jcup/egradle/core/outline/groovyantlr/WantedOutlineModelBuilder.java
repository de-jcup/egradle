package de.jcup.egradle.core.outline.groovyantlr;

import static org.codehaus.groovy.antlr.parser.GroovyTokenTypes.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineItemType;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.OutlineModelBuilder;
import de.jcup.egradle.core.outline.OutlineModelImpl;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;

/**
 * Builds a outline model containing simply all AST parts from groovy antlr
 * 
 * @author Albert Tregnaghi
 *
 */
public class WantedOutlineModelBuilder implements OutlineModelBuilder {
	private InputStream is;

	public WantedOutlineModelBuilder(InputStream is) {
		this.is = is;
	}

	@Override
	public OutlineModel build() throws OutlineModelBuilderException {
		OutlineModelImpl model = new OutlineModelImpl();
		if (is == null) {
			return model;
		}
		InputStreamReader reader = new InputStreamReader(is);
		SourceBuffer sourceBuffer = new SourceBuffer();
		UnicodeEscapingReader r2 = new UnicodeEscapingReader(reader, sourceBuffer);
		GroovyLexer lexer = new GroovyLexer(r2);
		r2.setLexer(lexer);

		GroovyRecognizer parser = GroovyRecognizer.make(lexer);
		parser.setSourceBuffer(sourceBuffer);
		try {
			parser.compilationUnit();
			AST first = parser.getAST();

			Context context = new Context();
			OutlineItem rootItem = model.getRoot();
			walkThrough(context, rootItem, null, first);

		} catch (RecognitionException | TokenStreamException e) {
			throw new OutlineModelBuilderException("Cannot build outline model because AST parsing problems", e);
		}

		return model;
	}

	private class Context {
		private int offset;
	}

	private void walkThrough(Context context, OutlineItem parentItem, OutlineItem currentItem, AST current) {
		parentItem = checkForNewParentItem(parentItem, currentItem, current);

		OutlineItem newItem = createNewItem(context, current);
		/* switch current item and add closed parts */
		if (newItem != null) {
			addToParent(context, parentItem, currentItem);
			currentItem = newItem;
		}
		if (currentItem != null) {
			updateItem(context, currentItem, current);
		}

		/* dive into children */
		AST element = current.getFirstChild();
		if (element != null) {
			walkThrough(context, parentItem, currentItem, element);
		}
		AST next = current.getNextSibling();
		if (next != null) {
			walkThrough(context, parentItem, currentItem, next);
		} else {
			addToParent(context, parentItem, currentItem);
		}
	}

	private void addToParent(Context context ,OutlineItem parentItem, OutlineItem currentItem) {
		if (currentItem == null) {
			return;
		}
		if (parentItem == null) {
			return;
		}
		/* close last part */
		if (parentItem.hasChild(currentItem)) {
			/* already added, just a loop */
			return;
		}
		parentItem.add(currentItem);
	}

	private OutlineItem checkForNewParentItem(OutlineItem parentItem, OutlineItem currentItem, AST current) {
		return parentItem;
	}

	private void updateItem(Context context, OutlineItem item, AST ast) {
		if (ast instanceof GroovySourceAST) {
			GroovySourceAST gast = (GroovySourceAST) ast;
		}
		switch (ast.getType()) {
		case VARIABLE_DEF:// variable...
			item.setItemType(OutlineItemType.VARIABLE);
			break;
		case IDENT:// identifier
			item.setName(ast.getText());
			break;
		case TYPE:
			break;
		case MODIFIERS:
			return;
		default:
			return;
		}
	}

	/**
	 * creates new item or <code>null</code>
	 * 
	 * @param context
	 * 
	 * @param current
	 * @return
	 */
	private OutlineItem createNewItem(Context context, AST current) {
		if (!(current instanceof GroovySourceAST)) {
			System.out.println("--ignoring on create:"+current);
			return null;
		}
		GroovySourceAST gast = (GroovySourceAST) current;
		switch (gast.getType()) {
		case VARIABLE_DEF:
			OutlineItem item = new OutlineItem();
			int length = gast.getColumnLast()-gast.getColumn();
			item.setOffset(context.offset);
			context.offset+=length;
			item.setLength(length);
			return item;
		default:
			int length2 = gast.getColumnLast()-gast.getColumn();
			System.out.println("ignoring on create:"+gast+", length="+length2);
			return null;
		}
	}

}

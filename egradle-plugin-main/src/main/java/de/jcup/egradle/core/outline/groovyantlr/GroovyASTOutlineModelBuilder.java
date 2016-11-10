package de.jcup.egradle.core.outline.groovyantlr;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import de.jcup.egradle.core.outline.OutlineItem;
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
public class GroovyASTOutlineModelBuilder implements OutlineModelBuilder {
	private InputStream is;

	public GroovyASTOutlineModelBuilder(InputStream is) {
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

			OutlineItem rootItem = model.getRoot();
			addGivenElementAndItsSiblingsToItem(model, rootItem, first);

		} catch (RecognitionException | TokenStreamException e) {
			throw new OutlineModelBuilderException("Cannot build outline model because AST parsing problems", e);
		}

		return model;
	}

	private void addGivenElementAndItsSiblingsToItem(OutlineModelImpl model, OutlineItem parentItem, AST current) {
		OutlineItem outlineItem = null;
		// if (current instanceof GroovySourceAST){
		outlineItem = createItem(current);
		parentItem.add(outlineItem);
		// }else{
		// item = parentItem; // keep at same line...
		// }

		AST element = current.getFirstChild();
		if (element != null) {
			addGivenElementAndItsSiblingsToItem(model, outlineItem, element);
		}
		AST next = current.getNextSibling();
		if (next != null) {
			addGivenElementAndItsSiblingsToItem(model, parentItem, next);
		}
	}

	private OutlineItem createItem(AST ast) {
		if (ast == null) {
			throw new IllegalArgumentException("ast may not be null!");
		}
		OutlineItem outlineItem = new OutlineItem();
		if (ast instanceof GroovySourceAST) {
			GroovySourceAST source = (GroovySourceAST) ast;
			int length = source.getColumnLast() - source.getColumn();
			outlineItem.setLength(length);
		} else {
			outlineItem.setLength(0);
		}

		outlineItem.setOffset(0); /*
									 * FIXME ATR, 09.11.2016 : this is only to
									 * test and still a problem !
									 */
		outlineItem.setName(ast.toString());// +" -
		// children:"+ast.getNumberOfChildren());
		int type = ast.getType();
		outlineItem.setInfo("type:" + type);
		switch (type) {
		case GroovyTokenTypes.CLOSABLE_BLOCK:
			break;
		case GroovyTokenTypes.EXPR:
		case GroovyTokenTypes.METHOD_CALL:
			break;
		default:
			break;
		}
		return outlineItem;
	}

}

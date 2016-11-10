package de.jcup.egradle.core.model;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

import de.jcup.egradle.eclipse.api.EGradleUtil;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;

public class GroovyASTOutlineModelBuilder implements OutlineModelBuilder {
	private InputStream is;

	public GroovyASTOutlineModelBuilder(InputStream is) {
		this.is = is;
	}

	@Override
	public OutlineModel build() {
		GroovyASTOutlineModel model = new GroovyASTOutlineModel();
		if (is==null){
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
			System.out.println(first.toStringTree());
			System.out.println(":::");
			
			Item rootItem = model.getRoot();
			addGivenChildAndItsSiblingsToItem(model, rootItem, first);

		} catch (RecognitionException | TokenStreamException e) {
			EGradleUtil.log(e);
			return model;
		}

		return model;
	}

	private void addGivenChildAndItsSiblingsToItem(ItemCreator<AST> creator, Item parentItem, AST current) {
		Item item = creator.createItem(current);
		parentItem.add(item);
		
		AST child = current.getFirstChild();
		if (child!=null){
			addGivenChildAndItsSiblingsToItem(creator, item,child);
		}
		AST next = current.getNextSibling();
		if (next!=null){
			addGivenChildAndItsSiblingsToItem(creator, parentItem, next);
		}
	}

}

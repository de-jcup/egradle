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
package de.jcup.egradle.core.model.groovyantlr;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelImpl;

/**
 * Builds a outline model containing simply all AST parts from groovy antlr
 * 
 * @author Albert Tregnaghi
 *
 */
public class GroovyASTModelBuilder implements ModelBuilder {

	private static final GroovyTokenTypeDebugInfoInspector INSPECTOR = new GroovyTokenTypeDebugInfoInspector();

	private InputStream is;

	public GroovyASTModelBuilder(InputStream is) {
		this.is = is;
	}

	@Override
	public Model build(BuildContext context) throws ModelBuilderException {
		ModelImpl model = new ModelImpl();
		if (is == null) {
			return model;
		}
		InputStreamReader reader = new InputStreamReader(is);
		ExtendedSourceBuffer sourceBuffer = new ExtendedSourceBuffer();
		UnicodeEscapingReader r2 = new UnicodeEscapingReader(reader, sourceBuffer);
		GroovyLexer lexer = new GroovyLexer(r2);
		r2.setLexer(lexer);

		GroovyRecognizer parser = GroovyRecognizer.make(lexer);
		parser.setSourceBuffer(sourceBuffer);
		try {
			parser.compilationUnit();
			AST first = parser.getAST();

			Item rootItem = model.getRoot();
			addGivenElementAndItsSiblingsToItem(sourceBuffer, model, rootItem, first);

		} catch (RecognitionException | TokenStreamException e) {
			throw new ModelBuilderException("Cannot build outline model because AST parsing problems", e);
		}

		return model;
	}

	private void addGivenElementAndItsSiblingsToItem(ExtendedSourceBuffer sourceBuffer, ModelImpl model,
			Item parentItem, AST current) {
		Item item = createItem(sourceBuffer, current);
		parentItem.add(item);

		AST element = current.getFirstChild();
		if (element != null) {
			addGivenElementAndItsSiblingsToItem(sourceBuffer, model, item, element);
		}
		AST next = current.getNextSibling();
		if (next != null) {
			addGivenElementAndItsSiblingsToItem(sourceBuffer, model, parentItem, next);
		}
	}

	private Item createItem(ExtendedSourceBuffer sourceBuffer, AST ast) {
		if (ast == null) {
			throw new IllegalArgumentException("ast may not be null!");
		}
		Item item = new Item();
		if (ast instanceof GroovySourceAST) {
			GroovySourceAST source = (GroovySourceAST) ast;
			int length = source.getColumnLast() - source.getColumn();
			item.setLength(length);
		} else {
			item.setLength(0);
		}

		item.setOffset(sourceBuffer.getOffset(ast.getLine(), ast.getColumn()));
		item.setName(ast.toString());
		int type = ast.getType();
		String typeStr = INSPECTOR.getGroovyTokenTypeName(type);
		item.setInfo("type:" + type + "=" + typeStr + ", line:" + ast.getLine() + ", column:" + ast.getColumn()
				+ ", offset=" + item.getOffset());
		return item;
	}

}

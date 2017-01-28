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

import static org.codehaus.groovy.antlr.parser.GroovyTokenTypes.*;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import de.jcup.egradle.core.api.Filter;
import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemFilter;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelImpl;

/**
 * Builds a outline model containing gradle specific outline items
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleModelBuilder implements ModelBuilder {
	private InputStream is;

	private ItemFilter postCreationFilter;
	private Filter preCreationFilter;
	private GradleModelBuilderSupport support = new GradleModelBuilderSupport();

	public GradleModelBuilder(InputStream is) {
		this.is = is;
	}

	/**
	 * Set pre creation filter to filter AST parts not wanted to be source for
	 * building items (can be used to speed up)
	 * 
	 * @param preCreationFilter
	 */
	public void setPreCreationFilter(Filter preCreationFilter) {
		this.preCreationFilter = preCreationFilter;
	}

	/**
	 * Set a post creation filter - is used when adding created items to model.
	 * If it filters the created item, the item (and all children) will not be
	 * listed inside model. At postCreationFilter time the items have already
	 * the correct gradle item type
	 * 
	 * @param postCreationFilter
	 */
	public void setPostCreationFilter(ItemFilter postCreationFilter) {
		this.postCreationFilter = postCreationFilter;
	}

	public ItemFilter getPostCreationFilter() {
		if (postCreationFilter == null) {
			postCreationFilter = ItemFilter.NO_ITEMS_FILTERED;
		}
		return postCreationFilter;
	}

	@Override
	public Model build(BuildContext builderContext) throws ModelBuilderException {
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

			Context context = new Context();
			context.init(sourceBuffer);

			Item rootItem = model.getRoot();
			walkThroughASTandSiblings(context, rootItem, first);

		} catch (RecognitionException | TokenStreamException e) {

			RecognitionException re = RecognitionExceptionResolver.getSharedInstance().resolveRecognitionException(e);
			Error error = new Error();
			if (re == null) {
				error.setLineNumber(1); // fall back, always use line 1 when not
										// clear..
				error.setMessage(e.getMessage());
			} else {
				int line = re.getLine();
				int column = re.getColumn();
				int offset = sourceBuffer.getOffset(line, column);
				error.setLineNumber(line);
				error.setMessage(re.getMessage());
				error.setCharStart(offset);
				error.setCharEnd(offset + 1);

			}
			if (builderContext != null) {
				builderContext.add(error);
			}
		} catch (RuntimeException e) {
			throw new ModelBuilderException("Cannot build outline model because of AST parsing problems", e);
		}

		return model;
	}

	/**
	 * Method itself will NOT look into children - so no recursion hell triggering... Only next sibling is resolved and tried to build as item!
	 * So build method should not handle to next sibling parts!
	 * @param context
	 * @param parent
	 * @param current
	 * @throws ModelBuilderException
	 */
	protected void walkThroughASTandSiblings(Context context, Item parent, AST current) throws ModelBuilderException {
		while (current != null) {
			Item item = buildItem(context, parent, current);
			if (item != null) {
				if (!getPostCreationFilter().isFiltered(item)) {
					parent.add(item);
				}
			}
			current = current.getNextSibling();
		}
	}

	/**
	 * Builds new item or <code>null</code>
	 * 
	 * @param context
	 * @param parent
	 * 
	 * @param current
	 * @return new item or <code>null</code>
	 * @throws ModelBuilderException
	 */
	protected Item buildItem(Context context, Item parent, AST current) throws ModelBuilderException {
		if (getPreCreationFilter().isFiltered(current)) {
			return null;
		}
		Item item;
		int type = current.getType();
		switch (type) {
		case CLASS_DEF:
			item = buildClass(context, current);
			break;
		case EXPR:
			item = buildExpression(context, parent, current);
			break;
		case VARIABLE_DEF:
			item = buildVariableDef(context, current);
			break;
		case ASSIGN:
			item = buildAssign(context, current);
			break;
		case METHOD_DEF:
			item = buildMethodDef(context, current);
			break;
		case PACKAGE_DEF:
			item = buildPackageDef(context, current);
			break;
		case IMPORT:
			item = buildImport(context, current);
			break;
		case MAP_CONSTRUCTOR:
			item = buildMap(context, parent, current);
			break;
		case ELIST:
			item = buildList(context, parent, current);
			break;
		default:
			item = null;
		}
		if (item != null) {
			item.setClosed(true);
		}

		return item;

	}

	Item buildList(Context context, Item parent, AST list) throws ModelBuilderException {
		AST firstListElement = list.getFirstChild();
		walkThroughASTandSiblings(context, parent, firstListElement);
		/* the list itself is not added */
		return null;
	}

	Item buildMap(Context context, Item parent, AST map) throws ModelBuilderException {
		return buildItem(context, parent, map.getFirstChild());
	}

	Item buildAssign(Context context, AST assign) throws ModelBuilderException {
		/* library = [...]*/
		/* public library = [...]*/
		
		Item item = null;

		AST assignmentIdentifier = assign.getFirstChild();
		if (assignmentIdentifier== null) {
			return null;
		}
		int firstType = assignmentIdentifier.getType();
		if (IDENT != firstType) {
			return null;
		}
		AST assignedValue = assignmentIdentifier.getNextSibling();
		
		String name = support.resolveAsSimpleString(assignmentIdentifier);
		
		item = support.createItem(context, assignmentIdentifier);
		item.setName(name);
		item.setItemType(ItemType.ASSIGNMENT);
		
		if (assignedValue!=null){
			walkThroughASTandSiblings(context, item, assignedValue);
		}
		
		return item;
	}
	
	Item buildImport(Context context, AST current) {
		Item item = null;

		AST modifiers = current.getFirstChild();
		if (modifiers == null) {
			return null;
		}
		AST packageName = modifiers.getNextSibling();
		if (packageName == null) {
			return null;
		}
		item = support.createItem(context, current);
		String name = support.resolveAsSimpleString(packageName);
		item.setName(name);
		item.setItemType(ItemType.IMPORT);
		return item;
	}

	Item buildPackageDef(Context context, AST current) {
		Item item = null;

		AST modifiers = current.getFirstChild();
		if (modifiers == null) {
			return null;
		}
		AST packageName = modifiers.getNextSibling();
		if (packageName == null) {
			return null;
		}
		item = support.createItem(context, current);
		String name = support.resolveAsSimpleString(packageName);
		item.setName(name);
		item.setItemType(ItemType.PACKAGE);
		return item;
	}
	
	Item buildVariableDef(Context context, AST current) throws ModelBuilderException {
		/* def variable = "" */
		/* variable = "" */
		/* def String variable = "" */
		/* String variable = "" <-- no modifiers! */
		Item item = null;

		AST modifiers = null;
		AST type = null;
		AST first = current.getFirstChild();
		if (first == null) {
			return null;
		}
		int firstType = first.getType();
		if (GroovyTokenTypes.TYPE == firstType) {
			type = first;
		} else if (GroovyTokenTypes.MODIFIERS == firstType) {
			modifiers = first;
		} else {
			return null;
		}
		if (type == null) {
			type = modifiers.getNextSibling();
			if (type == null) {
				return null;
			}

		}
		/* type */
		String typeDefText = null;
		AST typeDef = type.getFirstChild();
		if (typeDef != null) {
			typeDefText = typeDef.getText();
		}
		AST name = type.getNextSibling();
		String nameString;
		if (name != null) {
			nameString = name.getText();
		} else {
			nameString = "<unknown>";
		}
		item = support.createItem(context, current);
		item.setName(nameString);
		support.appendModifiers(item, modifiers);
		item.setType(typeDefText);
		item.setItemType(ItemType.VARIABLE);
		return item;
	}

	Item buildMethodDef(Context context, AST current) throws ModelBuilderException {
		/* def method(params) */
		Item item = null;

		AST modifiers = null;
		AST type = null;
		AST first = current.getFirstChild();
		if (first == null) {
			return null;
		}
		int firstType = first.getType();
		if (GroovyTokenTypes.TYPE == firstType) {
			type = first;
		} else if (GroovyTokenTypes.MODIFIERS == firstType) {
			modifiers = first;
		} else {
			return null;
		}
		if (type == null) {
			type = modifiers.getNextSibling();
			if (type == null) {
				return null;
			}

		}
		/* type */
		String typeDefText = null;
		AST typeDef = type.getFirstChild();
		if (typeDef != null) {
			typeDefText = typeDef.getText();
		}
		AST name = type.getNextSibling();
		String nameString;
		if (name != null) {
			nameString = name.getText();
		} else {
			nameString = "<unknown>";
		}
		item = support.createItem(context, current);
		item.setName(nameString);
		support.appendModifiers(item, modifiers);
		item.setType(typeDefText);
		item.setItemType(ItemType.METHOD);

		AST parameters = name.getNextSibling();
		if (parameters != null) {
			if (parameters.getType() == GroovyTokenTypes.PARAMETERS) {
				support.appendParameters(item, parameters);
			}

			AST slist = parameters.getNextSibling();
			if (slist.getType() == GroovyTokenTypes.SLIST) {
				walkThroughASTandSiblings(context, item, slist.getFirstChild());
			}
		}

		return item;
	}

	Item buildClass(Context context, AST current) throws ModelBuilderException {
		Item item = null;
		AST classDefModifiers = current.getFirstChild();
		if (classDefModifiers == null) {
			return null;
		}
		AST classDefName = classDefModifiers.getNextSibling();
		if (classDefName == null) {
			return null;
		}
		item = support.createItem(context, current);
		item.setItemType(ItemType.CLASS);
		item.setName(classDefName.getText());
		support.appendModifiers(item, classDefModifiers);
		return item;
	}

	Item buildExpression(Context context, Item parent, AST expression) throws ModelBuilderException {
		AST next = expression.getFirstChild();
		if (next == null) {
			return null;
		}
		if (GroovyTokenTypes.DOT == next.getType()) {
			next = next.getFirstChild();
			if (next == null) {
				return null;
			}
		}
		
		if (GroovyTokenTypes.ASSIGN == next.getType()) {
			return buildAssign(context, next);
		}
		if (GroovyTokenTypes.METHOD_CALL != next.getType()) {
			return null;
		}
		AST methodCall = expression.getFirstChild();
		if (methodCall == null) {
			return null;
		}
		if (GroovyTokenTypes.DOT == methodCall.getType()) {
			methodCall = methodCall.getFirstChild();
			if (methodCall == null) {
				return null;
			}
		}
		AST methodChild = methodCall.getFirstChild();
		if (methodChild == null) {
			return null;
		}
		AST ename = methodChild;
		String enameString = support.resolveMethodCallName(methodCall);
		if (enameString == null) {
			return null;
		}
		ItemType outlineType = null;
		if (methodCall.getType() == METHOD_CALL) {

			if (parent != null) {
				if (ItemType.DEPENDENCIES == parent.getItemType()) {
					outlineType = ItemType.DEPENDENCY;
				} else if (ItemType.REPOSITORIES == parent.getItemType()) {
					outlineType = ItemType.REPOSITORY;
				}
			}
			if (outlineType == null) {
				outlineType = ItemType.METHOD_CALL;
			}
		} else {
			return null;
		}
		Item item = support.createItem(context, expression);
		item.setItemType(outlineType);
		item.setName(enameString);
		item.setClosed(true);

		AST lastAst = ename.getNextSibling();

		if ("task".equals(enameString) || enameString.startsWith("task ")) {
			item.setItemType(ItemType.TASK);
			lastAst = support.handleTaskClosure(enameString, item, lastAst);
		} else if (enameString.startsWith("tasks.")) {
			item.setItemType(ItemType.TASKS);
		} else if (enameString.startsWith("apply ")) {
			item.setItemType(ItemType.APPLY_SETUP);
			support.handleApplyType(item, lastAst);
		} else if (outlineType == ItemType.DEPENDENCY) {
			return support.handleDependencyAndReturnItem(methodCall, item);
		} else if (outlineType == ItemType.REPOSITORY) {
			/* ? */
		} else if (outlineType == ItemType.METHOD_CALL) {
			if (methodCall.getFirstChild() != null) {
				/* ( child */
				AST m1 = methodCall.getFirstChild();
				AST m2 = m1.getNextSibling();
				while (m2 != null) {
					m2 = m1.getNextSibling();
					if (m2 != null) {
						m1 = m2;
					}
				}
				/* child ){ */
				lastAst = m1;

			}
		}

		if (lastAst != null) {
			int type = lastAst.getType();
			if (GroovyTokenTypes.CLOSABLE_BLOCK == type) {
				item.setClosureBlock(true);
				String name = item.getName();
				if (name == null) {
					return item;
				}
				if ("repositories".equals(name)) {
					item.setItemType(ItemType.REPOSITORIES);
				} else if ("allprojects".equals(name)) {
					item.setItemType(ItemType.ALL_PROJECTS);
				} else if ("subprojects".equals(name)) {
					item.setItemType(ItemType.SUB_PROJECTS);
				} else if ("dependencies".equals(name)) {
					item.setItemType(ItemType.DEPENDENCIES);
				} else if ("sourceSets".equals(name)) {
					item.setItemType(ItemType.SOURCESETS);
				} else if ("main".equals(name)) {
					item.setItemType(ItemType.MAIN);
				} else if ("jar".equals(name)) {
					item.setItemType(ItemType.JAR);
				} else if ("test".equals(name)) {
					item.setItemType(ItemType.TEST);
				} else if ("clean".equals(name)) {
					item.setItemType(ItemType.CLEAN);
				} else if ("buildscript".equals(name)|| name.startsWith("buildscript.")) {
					item.setItemType(ItemType.BUILDSCRIPT);
				} else if ("configurations".equals(name) || name.startsWith("configurations.")) {
					item.setItemType(ItemType.CONFIGURATIONS);
				} else if ("doFirst".equals(name)) {
					item.setItemType(ItemType.DO_FIRST);
				} else if ("doLast".equals(name)) {
					item.setItemType(ItemType.DO_LAST);
				} else if ("eclipse".equals(name)) {
					item.setItemType(ItemType.ECLIPSE);
				} else if (name.startsWith("task ") || name.startsWith("task.")) {
					item.setItemType(ItemType.TASK);
				} else if (name.startsWith("tasks.")) {
					item.setItemType(ItemType.TASKS);
				} else if (name.startsWith("apply ")) {
					item.setItemType(ItemType.APPLY_SETUP);
				} else if (name.startsWith("project ") || name.equals("project")|| name.startsWith("project.")  ) {
					item.setItemType(ItemType.PROJECT);
				} else {
					item.setItemType(ItemType.CLOSURE);
				}
				item.setAPossibleParent(true);
				/* inspect children... */
				walkThroughASTandSiblings(context, item, lastAst.getFirstChild());
			}
		}

		return item;
	}

	private Filter getPreCreationFilter() {
		if (preCreationFilter == null) {
			preCreationFilter = Filter.NOTHING_FILTERED;
		}
		return preCreationFilter;
	}

	class Context {
		ExtendedSourceBuffer buffer;

		protected void init(ExtendedSourceBuffer sourceBuffer) {
			this.buffer=sourceBuffer;
			this.buffer.appendLineEndToLastLineIfMissing();
		}
	}

}

/*
 * Copyright 2017 Albert Tregnaghi
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemFilter;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelImpl;
import de.jcup.egradle.core.util.Filter;

public abstract class AbstractGroovyModelBuilder implements ModelBuilder {

	protected InputStream is;
	private ItemFilter postCreationFilter;
	private Filter preCreationFilter;
	private AbstractGroovyModelBuilderSupport support;
	private FirstItemTypeUpdater firstItemTypeUpdater = new FirstItemTypeUpdater();
	private LastItemTypeUpdater lastItemTypeUpdater = new LastItemTypeUpdater();

	public AbstractGroovyModelBuilder() {
		super();
		support = createModelBuilderSupport();
	}

	protected abstract AbstractGroovyModelBuilderSupport createModelBuilderSupport();

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
	 * Method itself will NOT look into children - so no recursion hell
	 * triggering... Only next sibling is resolved and tried to build as item!
	 * So build method should not handle to next sibling parts!
	 * 
	 * @param context
	 * @param parent
	 * @param current
	 * @throws ModelBuilderException
	 */
	private void walkThroughASTandSiblings(Context context, Item parent, AST current) throws ModelBuilderException {
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
	private Item buildItem(Context context, Item parent, AST current) throws ModelBuilderException {
		if (getPreCreationFilter().isFiltered(current)) {
			return null;
		}
		Item item;
		int type = current.getType();
		switch (type) {
		case CLASS_DEF:
			item = buildClass(context, current, ItemType.CLASS);
			break;
		case INTERFACE_DEF:
			item = buildClass(context, current, ItemType.INTERFACE);
			break;
		case ENUM_DEF:
			item = buildClass(context, current, ItemType.ENUM);
			break;
		case EXPR:
			item = buildExpression(context, parent, current);
			break;
		case VARIABLE_DEF:
			item = buildVariableDef(context, current);
			break;
		case ENUM_CONSTANT_DEF:
			item = buildEnumConstantDef(context, current);
			break;
		case ASSIGN:
			item = buildAssign(context, current);
			break;
		case CTOR_IDENT:
			item = buildConstructorDef(parent.getName(), context, current);
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

	private Item buildList(Context context, Item parent, AST list) throws ModelBuilderException {
		AST firstListElement = list.getFirstChild();
		walkThroughASTandSiblings(context, parent, firstListElement);
		/* the list itself is not added */
		return null;
	}

	private Item buildMap(Context context, Item parent, AST map) throws ModelBuilderException {
		return buildItem(context, parent, map.getFirstChild());
	}

	private Item buildAssign(Context context, AST assign) throws ModelBuilderException {
		/* library = [...] */
		/* public library = [...] */

		Item item = null;

		AST assignmentIdentifier = assign.getFirstChild();
		if (assignmentIdentifier == null) {
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

		if (assignedValue != null) {
			walkThroughASTandSiblings(context, item, assignedValue);
		}

		return item;
	}

	private Item buildImport(Context context, AST current) {
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

	private Item buildPackageDef(Context context, AST current) {
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

	private Item buildVariableDef(Context context, AST current) throws ModelBuilderException {
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

	private Item buildEnumConstantDef(Context context, AST current) throws ModelBuilderException {
		/* def variable = "" */
		/* variable = "" */
		/* def String variable = "" */
		/* String variable = "" <-- no modifiers! */
		Item item = null;

		AST modifiers = null;
		AST first = current.getFirstChild();
		if (first == null) {
			return null;
		}
		AST name = null;
		int firstType = first.getType();
		if (GroovyTokenTypes.ANNOTATIONS == firstType) {
			name = first.getNextSibling();
			if (name == null) {
				return null;
			}

		} else {
			return null;
		}
		String nameString = name.getText();
		item = support.createItem(context, current);
		item.setName(nameString);
		support.appendModifiers(item, modifiers);
		item.setItemType(ItemType.ENUM_CONSTANT);
		return item;
	}

	private Item buildMethodDef(Context context, AST current) throws ModelBuilderException {
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
			if (slist != null) {
				if (slist.getType() == GroovyTokenTypes.SLIST) {
					walkThroughASTandSiblings(context, item, slist.getFirstChild());
				}
			}
		}

		return item;
	}

	private Item buildConstructorDef(String parentItemName, Context context, AST current) throws ModelBuilderException {
		/* def method(params) */
		Item item = null;

		AST modifiers = null;
		AST first = current.getFirstChild();
		if (first == null) {
			return null;
		}
		int firstType = first.getType();
		if (GroovyTokenTypes.MODIFIERS == firstType) {
			modifiers = first;
		} else {
			return null;
		}
		AST parameters = modifiers.getNextSibling();
		if (parameters == null) {
			return null;
		}
		item = support.createItem(context, current);
		item.setItemType(ItemType.CONSTRUCTOR);
		item.setName(parentItemName);
		if (parameters.getType() == GroovyTokenTypes.PARAMETERS) {
			support.appendParameters(item, parameters);
		}
		AST slist = parameters.getNextSibling();
		if (slist != null) {
			if (slist.getType() == GroovyTokenTypes.SLIST) {
				walkThroughASTandSiblings(context, item, slist.getFirstChild());
			}
		}

		return item;
	}

	private Item buildClass(Context context, AST current, ItemType classType) throws ModelBuilderException {
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
		item.setItemType(classType);
		item.setName(classDefName.getText());
		support.appendModifiers(item, classDefModifiers);
		AST lastAst = classDefName;
		while (lastAst.getNextSibling() != null) {
			lastAst = lastAst.getNextSibling();
		}
		/* inspect children... */
		walkThroughASTandSiblings(context, item, lastAst.getFirstChild());

		return item;
	}

	protected Item buildExpression(Context context, Item parent, AST expression) throws ModelBuilderException {
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
		/* ------------------------------------------------------------- */
		/* ------------------------ METHOD_CALL ------------------------ */
		/* ------------------------------------------------------------- */
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
		if (lastAst.getType() == ELIST) {
			lastAst = appendMethodCallParameters(context, lastAst, item);
		}

		if ("task".equals(enameString) || enameString.startsWith("task ")) {
			item.setItemType(ItemType.TASK);
			lastAst = support.handleTaskClosure(enameString, item, lastAst);
		} else if (enameString.startsWith("tasks.")) {
			item.setItemType(ItemType.TASKS);
			if (enameString.startsWith("tasks.withType")) {
				lastAst = support.handleTasksWithTypeClosure(enameString, item, lastAst);
			}
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
			String[] params = item.getParameters();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					String param = params[i];
					if ("groovy.lang.Closure".equals(param)) {
						item.setClosureBlock(true);
						break;
					}
				}
			}
		}

		if (lastAst != null) {
			int type = lastAst.getType();
			if (GroovyTokenTypes.CLOSABLE_BLOCK == type) {
				item.setClosureBlock(true);
				String[] params = item.getParameters();
				if (params == null || params.length == 0) {
					item.setParameters("groovy.lang.Closure");
				} else {
					/* last parameter should be a closure! */
					String lastParam = params[params.length - 1];
					if (!"groovy.lang.Closure".equals(lastParam)) {
						/* missing - must change */
						List<String> paramList = new ArrayList<>(Arrays.asList(params));
						paramList.add("groovy.lang.Closure");
						item.setParameters(paramList.toArray(new String[paramList.size()]));
					}
				}
			}
		}
		if (item.isClosureBlock()) {
			String name = item.getName();
			if (name == null) {
				return item;
			}
			handleItemNames(item, name);
		}
		if (lastAst != null) {
			item.setAPossibleParent(true);
			/* inspect children... */
			walkThroughASTandSiblings(context, item, lastAst.getFirstChild());
		}
		return item;
	}

	private void handleItemNames(Item item, String name) {
		handleItemNames(item, name, firstItemTypeUpdater);
		String lastMethodChainPartLastMethodChainPart = resolveLastMethodChainPart(name);
		if (lastMethodChainPartLastMethodChainPart != null) {
			handleItemNames(item, lastMethodChainPartLastMethodChainPart, lastItemTypeUpdater);
		}
	}

	private void handleItemNames(Item item, String name, ItemTypeUpdater updater) {
		if ("repositories".equals(name)) {
			updater.setItemType(item, ItemType.REPOSITORIES);
		} else if ("plugins".equals(name)) {
			updater.setItemType(item, ItemType.PLUGINS);
		} else if ("allprojects".equals(name)) {
			updater.setItemType(item, ItemType.ALL_PROJECTS);
		} else if ("subprojects".equals(name)) {
			updater.setItemType(item, ItemType.SUB_PROJECTS);
		} else if ("dependencies".equals(name)) {
			updater.setItemType(item, ItemType.DEPENDENCIES);
		} else if ("sourceSets".equals(name)) {
			updater.setItemType(item, ItemType.SOURCESETS);
		} else if ("main".equals(name)) {
			updater.setItemType(item, ItemType.MAIN);
		} else if ("jar".equals(name)) {
			updater.setItemType(item, ItemType.JAR);
		} else if ("war".equals(name)) {
			updater.setItemType(item, ItemType.WAR);
		} else if ("ear".equals(name)) {
			updater.setItemType(item, ItemType.EAR);
		} else if ("zip".equals(name)) {
			updater.setItemType(item, ItemType.ZIP);
		} else if ("test".equals(name)) {
			updater.setItemType(item, ItemType.TEST);
		} else if ("clean".equals(name)) {
			updater.setItemType(item, ItemType.CLEAN);
		} else if ("buildscript".equals(name) || name.startsWith("buildscript.")) {
			updater.setItemType(item, ItemType.BUILDSCRIPT);
		} else if ("configurations".equals(name) || name.startsWith("configurations.")) {
			updater.setItemType(item, ItemType.CONFIGURATIONS);
		} else if ("configure".equals(name) || name.startsWith("configure ")) {
			updater.setItemType(item, ItemType.CONFIGURE);
		} else if ("doFirst".equals(name)) {
			updater.setItemType(item, ItemType.DO_FIRST);
		} else if ("doLast".equals(name)) {
			updater.setItemType(item, ItemType.DO_LAST);
		} else if ("afterEvaluate".equals(name)) {
			updater.setItemType(item, ItemType.AFTER_EVALUATE);
		} else if ("eclipse".equals(name)) {
			updater.setItemType(item, ItemType.ECLIPSE);
		} else if (name.startsWith("task ") || name.startsWith("task.")) {
			updater.setItemType(item, ItemType.TASK);
		} else if (name.startsWith("tasks.")) {
			updater.setItemType(item, ItemType.TASKS);
		} else if (name.startsWith("apply ")) {
			updater.setItemType(item, ItemType.APPLY_SETUP);
		} else if (name.startsWith("project ") || name.equals("project") || name.startsWith("project.")) {
			updater.setItemType(item, ItemType.PROJECT);
		} else {
			updater.setItemType(item, ItemType.CLOSURE);
		}
	}

	/**
	 * Resolves method chain last part after . ("e.g. a "xyz.doLast" will return
	 * "doLast" - a "xyz" returns <code>null</code>)
	 * 
	 * @param item
	 * @return last part after dot, otherwise <code>null</code>
	 */
	private String resolveLastMethodChainPart(String name) {
		if (name == null) {
			return null;
		}
		if (name.indexOf('.') == -1) {
			return null;
		}
		String lastCall = StringUtils.substringAfterLast(name, ".");
		return lastCall;
	}

	/**
	 * <pre>
	 * fileTree baseDir {                                       fileTree {      
	 * 
	 * }                                                        }
	 * </pre>
	 * 
	 * becomes in antlr model:
	 * 
	 * <pre>
	 * EXPR1                                                    EXPR2
	 *  x <command> METHOD_CALL                                  x { METHOD_CALL   
	 *   x  fileTree - IDENT                                      x fileTree - IDENT   
	 *   x  ELIST                                                 x { CLOSEABLE_BLOCK
	 *    x    { METHOD_CALL                                       x IMPLICIT_PARAMETERS
	 *      x    baseDir IDENT
	 *      x    { CLOSEABLE_BLOCK
	 *       x   IMPLICIT_PARAMETERS
	 * </pre>
	 * 
	 * @param context
	 * @throws ModelBuilderException
	 */
	private AST appendMethodCallParameters(Context context, AST elist, Item methodCallItem)
			throws ModelBuilderException {
		List<String> parameterList = support.resolveParameterList(elist);
		methodCallItem.setParameters(parameterList.toArray(new String[parameterList.size()]));
		return elist;
	}

	private Filter getPreCreationFilter() {
		if (preCreationFilter == null) {
			preCreationFilter = Filter.NOTHING_FILTERED;
		}
		return preCreationFilter;
	}

	protected abstract class ItemTypeUpdater {
		abstract void setItemType(Item item, ItemType type);
	}

	protected class FirstItemTypeUpdater extends ItemTypeUpdater {
		void setItemType(Item item, ItemType type) {
			item.setItemType(type);
		}
	}

	protected class LastItemTypeUpdater extends ItemTypeUpdater {
		void setItemType(Item item, ItemType type) {
			if (type == ItemType.CLOSURE) {
				/* no dedicated type found so set null */
				item.setLastChainedItemType(null);
				return;
			}
			item.setLastChainedItemType(type);
		}
	}

	protected class Context {
		ExtendedSourceBuffer buffer;

		void init(ExtendedSourceBuffer sourceBuffer) {
			this.buffer = sourceBuffer;
			this.buffer.appendLineEndToLastLineIfMissing();
		}
	}

}
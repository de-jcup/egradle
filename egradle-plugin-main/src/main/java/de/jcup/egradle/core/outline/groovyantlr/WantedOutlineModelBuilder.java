package de.jcup.egradle.core.outline.groovyantlr;

import static org.codehaus.groovy.antlr.parser.GroovyTokenTypes.*;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineItemType;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.OutlineModelBuilder;
import de.jcup.egradle.core.outline.OutlineModelImpl;
import de.jcup.egradle.core.outline.OutlineModifier;
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
	private ASTFilterStrategy filterStrategy;

	public WantedOutlineModelBuilder(InputStream is) {
		this(is, null);
	}

	public WantedOutlineModelBuilder(InputStream is, ASTFilterStrategy filterStrategy) {
		this.is = is;
		if (filterStrategy == null) {
			filterStrategy = new ASTFilterStrategy() {
			};
		}
		this.filterStrategy = filterStrategy;
	}

	@Override
	public OutlineModel build() throws OutlineModelBuilderException {
		OutlineModelImpl model = new OutlineModelImpl();
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
			context.buffer = sourceBuffer;

			OutlineItem rootItem = model.getRoot();
			startParsing(context, rootItem, first);

		} catch (RecognitionException | TokenStreamException e) {
			throw new OutlineModelBuilderException("Cannot build outline model because of AST parsing problems", e);
		}

		return model;
	}

	protected void startParsing(Context context, OutlineItem root, AST current) throws OutlineModelBuilderException {
		/* walk through all siblings */
		walkThroughASTandSiblings(context, root, current);

	}

	private void walkThroughASTandSiblings(Context context, OutlineItem parent, AST current)
			throws OutlineModelBuilderException {
		while (current != null) {
			OutlineItem item = buildItem(context, parent, current);
			if (item != null) {
				parent.add(item);
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
	 * @return
	 * @throws OutlineModelBuilderException
	 */
	protected OutlineItem buildItem(Context context, OutlineItem parent, AST current)
			throws OutlineModelBuilderException {
		OutlineItem item;
		switch (current.getType()) {
		case CLASS_DEF:
			item = createClass(context, current);
			walkThroughASTandSiblings(context, item, current.getFirstChild());
			break;
		case EXPR:
			item = createExpression(context, parent, current);
			break;
		case VARIABLE_DEF:
			item = createVariableDef(context, current);
			break;
		default:
			item = null;
		}
		if (item != null) {
			item.setClosed(true);
		}

		return item;

	}

	private void appendModifiers(OutlineItem item, AST modifiers) throws OutlineModelBuilderException {
		if (modifiers == null) {
			return;
		}
		if (modifiers.getType() != GroovyTokenTypes.MODIFIERS) {
			return;
		}
		AST modifierAst = modifiers.getFirstChild();
		if (modifierAst == null) {
			return;
		}
		/* currently just skip annotations at all */
		while (modifierAst != null) {
			if (modifierAst.getType() != GroovyTokenTypes.ANNOTATION) {
				break;
			}
			// AST annotations = modifierAst;
			modifierAst = modifierAst.getNextSibling();
		}
		if (modifierAst == null) {
			return;
		}
		String modifierString = modifierAst.getText();
		OutlineModifier oModifier = OutlineModifier.DEFAULT;
		if (StringUtils.isNotBlank(modifierString)) {
			if ("private".equals(modifierString)) {
				oModifier = OutlineModifier.PRIVATE;
			} else if ("protected".equals(modifierString)) {
				oModifier = OutlineModifier.PROTECTED;
			} else if ("public".equals(modifierString)) {
				oModifier = OutlineModifier.PUBLIC;
			}
		}
		item.setModifier(oModifier);
	}

	private OutlineItem createVariableDef(Context context, AST current) throws OutlineModelBuilderException {
		OutlineItem item;

		AST modifiers = current.getFirstChild();
		if (modifiers == null) {
			return null;
		}
		AST type = modifiers.getNextSibling();
		/* type */
		if (type == null) {
			return null;
		}
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
		item = createItem(context, current);
		item.setName(nameString);
		appendModifiers(item, modifiers);
		item.setType(typeDefText);
		item.setItemType(OutlineItemType.VARIABLE);
		return item;
	}

	private OutlineItem createClass(Context context, AST current) throws OutlineModelBuilderException {
		OutlineItem item;
		AST classDefModifiers = current.getFirstChild();
		if (classDefModifiers == null) {
			return null;
		}
		AST classDefName = classDefModifiers.getNextSibling();
		if (classDefName == null) {
			return null;
		}
		item = createItem(context, current);
		appendModifiers(item, classDefModifiers);
		return item;
	}

	private OutlineItem createExpression(Context context, OutlineItem parent, AST current)
			throws OutlineModelBuilderException {
		AST next = current.getFirstChild();
		if (next == null) {
			return null;
		}
		if (GroovyTokenTypes.DOT == next.getType()) {
			next = next.getFirstChild();
			if (next == null) {
				return null;
			}
		}
		if (GroovyTokenTypes.METHOD_CALL != next.getType()) {
			return null;
		}
		AST methodCall = current.getFirstChild();
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
		/* TODO ATR: expression can have << inside -e.g. task myTask(type:xxx){ blubb }<< xxx so
		 * the first element is not the name of the task but an elist!
		 */
		AST ename = methodCall.getFirstChild();
		if (ename == null) {
			return null;
		}
		AST astForName = methodChild;
		String enameString = resolveName(astForName);
		if (enameString == null) {
			return null;
		}
		
		if (filterStrategy.isExpressionIgnored(enameString)) {
			return null;
		}

		OutlineItemType outlineType = null;
		if (methodCall.getType() == METHOD_CALL) {

			if (parent != null) {
				if (OutlineItemType.DEPENDENCIES == parent.getItemType()) {
					outlineType = OutlineItemType.DEPENDENCY;
				}else if (OutlineItemType.REPOSITORIES == parent.getItemType()) {
					outlineType = OutlineItemType.REPOSITORY;
				}
			}
			if (outlineType == null) {
				outlineType = OutlineItemType.METHOD_CALL;
			}
		} else {
			return null;
		}
		OutlineItem item = createItem(context, current);
		item.setItemType(outlineType);
		item.setName(enameString);
		item.setClosed(true);

		AST lastAst = ename.getNextSibling();
		if ("task".equals(enameString)) {
			item.setItemType(OutlineItemType.TASK_SETUP);
			lastAst = handleTaskClosure(enameString, item, lastAst);
		} else if (enameString.startsWith("tasks")) {
			item.setItemType(OutlineItemType.TASK_SETUP);
		} else if (enameString.equals("apply")) {
			item.setItemType(OutlineItemType.APPLY_SETUP);
			handleApplyType(item, lastAst);
		}
		if (outlineType == OutlineItemType.DEPENDENCY) {
			return handleDependencyAndReturnItem(methodCall, item);
		}
		if (outlineType==OutlineItemType.REPOSITORY){
			return item;
		}
		if (lastAst != null) {
			if (GroovyTokenTypes.CLOSABLE_BLOCK == lastAst.getType()) {
				if (item.getItemType() == OutlineItemType.TASK_SETUP) {
					item.setItemType(OutlineItemType.TASK_CLOSURE);
				} else {
					String name = item.getName();
					if ("repositories".equals(name)) {
						item.setItemType(OutlineItemType.REPOSITORIES);
					} else if ("allprojects".equals(name)) {
						item.setItemType(OutlineItemType.ALL_PROJECTS);
					} else if ("subprojects".equals(name)) {
						item.setItemType(OutlineItemType.SUB_PROJECTS);
					} else if ("dependencies".equals(name)) {
						item.setItemType(OutlineItemType.DEPENDENCIES);
					} else if ("test".equals(name)) {
						item.setItemType(OutlineItemType.TEST);
					} else if ("clean".equals(name)) {
						item.setItemType(OutlineItemType.CLEAN);
					} else if ("buildscript".equals(name)) {
						item.setItemType(OutlineItemType.BUILDSCRIPT);
					} else {
						item.setItemType(OutlineItemType.CLOSURE);
					}
				}
				/* inspect children... */
				walkThroughASTandSiblings(context, item, lastAst.getFirstChild());
			}
		}

		return item;
	}

	private OutlineItem handleDependencyAndReturnItem(AST methodCall, OutlineItem item) {
		AST configuration = methodCall.getFirstChild();
		AST configurationParameter = null;

		if (configuration != null) {
			configurationParameter = configuration.getNextSibling();
		}
		if (configurationParameter == null) {
			return item;
		}
		String depencyName = resolveAsSimpleString(configurationParameter);
		item.setConfiguration(configuration.getText());
		item.setName(depencyName);
		return item;
	}

	private void handleApplyType(OutlineItem item, AST lastAst) {
		if (lastAst == null) {
			return;
		}
		if (GroovyTokenTypes.ELIST != lastAst.getType()) {
			return;
		}
		/* parameter -e.g. apply from/plugin 'bla' */
		AST elist = lastAst;

		AST applyKind = elist.getFirstChild();

		if (applyKind == null) {
			return;
		}
		if (GroovyTokenTypes.LABELED_ARG != applyKind.getType()) {
			return;
		}
		AST applyLabel = applyKind.getFirstChild();
		if (applyLabel == null) {
			return;
		}
		String typeStr = applyLabel.getText();
		if ("plugin".equals(typeStr)) {
			item.setItemType(OutlineItemType.APPLY_PLUGIN);
			item.setName("apply plugin");
		} else if ("from".equals(typeStr)) {
			item.setItemType(OutlineItemType.APPLY_FROM);
			item.setName("apply from");
		}
		AST applyTarget = applyLabel.getNextSibling();
		if (applyTarget == null) {
			return;
		}
		String target = resolveAsSimpleString(applyTarget);
		item.setTarget(target);
	}

	private String resolveAsSimpleString(AST ast) {
		if (ast == null) {
			return "";
		}
		int type = ast.getType();
		if (GroovyTokenTypes.STRING_LITERAL == type) {
			return ast.getText();
		} else if (GroovyTokenTypes.STRING_CONSTRUCTOR == type) {
			return resolveStringOfFirstChildAndSiblings(ast);
		} else {
			AST firstChild = ast.getFirstChild();
			if (GroovyTokenTypes.EXPR == type) {
				return resolveName(firstChild);
			} else if (GroovyTokenTypes.SLIST == type) {
				return resolveStringOfFirstChildAndSiblings(ast);
			} else if (GroovyTokenTypes.ELIST == type) {
				return resolveStringOfFirstChildAndSiblings(ast, ", ");
			} else if (GroovyTokenTypes.DOT == type) {
				if (firstChild == null) {
					return "";
				}
				StringBuilder sb = new StringBuilder();
				sb.append(resolveAsSimpleString(firstChild));
				sb.append('.');
				sb.append(resolveAsSimpleString(firstChild.getNextSibling()));
				return sb.toString();
			} else if (GroovyTokenTypes.LABELED_ARG == type) {
				if (firstChild == null) {
					return "";
				}
				StringBuilder sb = new StringBuilder();
				sb.append(resolveAsSimpleString(firstChild));
				sb.append(':');
				sb.append(resolveAsSimpleString(firstChild.getNextSibling()));
				return sb.toString();
			}
		}
		return ast.toString();
	}

	private String resolveStringOfFirstChildAndSiblings(AST ast) {
		return resolveStringOfFirstChildAndSiblings(ast, null);
	}

	private String resolveStringOfFirstChildAndSiblings(AST ast, String separator) {
		StringBuilder sb = new StringBuilder();
		AST part = ast.getFirstChild();
		while (part != null) {
			sb.append(resolveAsSimpleString(part));
			part = part.getNextSibling();
			if (part != null) {
				if (separator != null) {
					sb.append(separator);
				}
			}
		}
		return sb.toString();
	}

	private AST handleTaskClosure(String enameString, OutlineItem item, AST lastAst) {
		if (lastAst == null) {
			return null;
		}
		lastAst = hadleTaskNameResolving(enameString, item, lastAst);
		lastAst = handleTaskTypeResolving(item, lastAst);
		return lastAst;
	}

	private AST handleTaskTypeResolving(OutlineItem item, AST lastAst) {
		if (lastAst == null) {
			return null;
		}
		if (GroovyTokenTypes.ELIST != lastAst.getType()) {
			return lastAst;
		}
		/* parameter -e.g. task mytask (type: xyz) */
		AST elist = lastAst;
		AST nextSibling = elist.getNextSibling();

		AST labeledArg = elist.getFirstChild();

		if (labeledArg == null) {
			return nextSibling;
		}
		if (GroovyTokenTypes.LABELED_ARG != labeledArg.getType()) {
			return nextSibling;
		}
		AST type = labeledArg.getFirstChild();
		if (type == null) {
			return nextSibling;
		}
		if (GroovyTokenTypes.STRING_LITERAL == type.getType()) {
			if (!"type".equals(type.getText())) {
				return nextSibling;
			}
			AST expr = type.getNextSibling();
			if (expr == null || GroovyTokenTypes.EXPR != expr.getType()) {
				return nextSibling;
			}
			AST ident = expr.getFirstChild();
			if (ident == null || GroovyTokenTypes.IDENT != ident.getType()) {
				return nextSibling;
			}
			item.setType(ident.getText());
		}
		return nextSibling;

	}

	private AST hadleTaskNameResolving(String enameString, OutlineItem item, AST lastAst) {
		if (lastAst == null) {
			return null;
		}
		if (lastAst.getType() == ELIST) {
			AST elist = lastAst;
			AST methodCall2 = elist.getFirstChild();
			if (methodCall2 != null) {
				AST name2 = methodCall2.getFirstChild();
				if (name2 != null) {
					enameString = enameString + " " + name2.getText();
					item.setName(enameString);
				}
				lastAst = name2.getNextSibling();
			}
		}
		return lastAst;
	}

	/**
	 * naming with dot: xyz.abc.bla.tests =>
	 * 
	 * . ->. ->. ->xyz abc bla tests
	 */
	private String resolveName(AST ast) {
		StringBuilder sb = new StringBuilder();
		resolveName(sb, ast);
		return sb.toString();
	}

	private void resolveName(StringBuilder sb, AST ast) {
		if (ast == null) {
			return;
		}
		if (ast.getType() == GroovyTokenTypes.DOT) {
			/* is dot */
			AST content = ast.getFirstChild();
			resolveName(sb, content);
			AST next = ast.getNextSibling();
			if (next != null) {
				if (next.getType() == GroovyTokenTypes.IDENT) {
					sb.append('.');
					sb.append(next.getText());
				}
			}
		} else {
			/* no dot, so content separated with DOT */
			sb.append(ast.getText());
			AST next = ast.getNextSibling();
			if (next != null) {
				if (next.getType() == GroovyTokenTypes.IDENT) {
					sb.append('.');
					sb.append(next.getText());
				}
			}

		}

	}

	private OutlineItem createItem(Context context, AST ast) {
		OutlineItem item = new OutlineItem();
		int column = ast.getColumn();
		int line = ast.getLine();
		item.setColumn(column);
		item.setLine(line);
		item.setOffset(context.buffer.getOffset(line, column));

		if (ast instanceof GroovySourceAST) {
			GroovySourceAST gast = (GroovySourceAST) ast;
			int offset1 = item.getOffset();
			int offset2 = context.buffer.getOffset(gast.getLineLast(), gast.getColumnLast());

			int length = offset2 - offset1;
			if (length < 0) {
				/* fall back */
				length = gast.getColumnLast() - column;
			}
			item.setLength(length);
		} else {
			item.setLength(1);
		}
		return item;
	}

	private class Context {
		private ExtendedSourceBuffer buffer;
	}

}

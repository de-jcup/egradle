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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import antlr.collections.AST;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;
import de.jcup.egradle.core.model.Modifier;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder.Context;

class GradleModelBuilderSupport {

	void appendParameters(Item item, AST parameters) {
		AST paramDef = parameters.getFirstChild();

		List<String> paramDefList = new ArrayList<>();
		appendParameterDef(paramDef, paramDefList);

		String[] paramArray = paramDefList.toArray(new String[paramDefList.size()]);
		item.setParameters(paramArray);
	}

	void appendParameterDef(AST paramDef, List<String> paramDefList) {
		if (paramDef == null) {
			return;
		}
		if (paramDef.getType() != GroovyTokenTypes.PARAMETER_DEF) {
			return;
		}
		appendParameterDefChilren(paramDef, paramDefList);

		AST nextParamDef = paramDef.getNextSibling();
		if (nextParamDef == null) {
			return;
		}
		appendParameterDef(nextParamDef, paramDefList);

	}

	void appendParameterDefChilren(AST paramDef, List<String> paramDefList) {
		AST modifiers = paramDef.getFirstChild();
		if (modifiers == null) {
			return;
		}
		AST type = modifiers.getNextSibling();
		if (type == null) {
			return;
		}
		AST name = type.getNextSibling();
		if (name == null) {
			return;
		}
		String paramText = name.getText();
		paramDefList.add(paramText);
	}

	void appendModifiers(Item item, AST modifiers) throws ModelBuilderException {
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
		Modifier oModifier = Modifier.DEFAULT;
		if (StringUtils.isNotBlank(modifierString)) {
			if ("private".equals(modifierString)) {
				oModifier = Modifier.PRIVATE;
			} else if ("protected".equals(modifierString)) {
				oModifier = Modifier.PROTECTED;
			} else if ("public".equals(modifierString)) {
				oModifier = Modifier.PUBLIC;
			}
		}
		item.setModifier(oModifier);
	}

	Item handleDependencyAndReturnItem(AST methodCall, Item item) {
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

	void handleApplyType(Item item, AST lastAst) {
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
			item.setItemType(ItemType.APPLY_PLUGIN);
			item.setName("apply plugin");
		} else if ("from".equals(typeStr)) {
			item.setItemType(ItemType.APPLY_FROM);
			item.setName("apply from");
		}
		AST applyTarget = applyLabel.getNextSibling();
		if (applyTarget == null) {
			return;
		}
		String target = resolveAsSimpleString(applyTarget);
		item.setTarget(target);
	}

	String resolveAsSimpleString(AST ast) {
		if (ast == null) {
			return "";
		}
		int type = ast.getType();
		if (GroovyTokenTypes.STRING_LITERAL == type) {
			return ast.getText();
		} else if (GroovyTokenTypes.STRING_CONSTRUCTOR == type) {
			return resolveStringOfFirstChildAndSiblings(ast);
		} else if (GroovyTokenTypes.METHOD_CALL == type) {
			return "";
		} else {

			AST firstChild = ast.getFirstChild();
			if (GroovyTokenTypes.EXPR == type) {
				return resolveExpressionName(ast);
			} else if (GroovyTokenTypes.SL == type) {
				return resolveStringOfFirstChildAndSiblings(ast) + " <<";
			} else if (GroovyTokenTypes.CLOSABLE_BLOCK == type) {
				return "";
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

	private String resolveExpressionName(AST firstChild) {
		String x = resolveStringOfFirstChildAndSiblings(firstChild);
		return x;
	}

	String resolveStringOfFirstChildAndSiblings(AST ast) {
		return resolveStringOfFirstChildAndSiblings(ast, null);
	}

	String resolveStringOfFirstChildAndSiblings(AST ast, String separator) {
		StringBuilder sb = new StringBuilder();
		AST part = ast.getFirstChild();
		if (part == null) {
			return "";
		}
		boolean wasMethodCall = false;
		if (GroovyTokenTypes.METHOD_CALL == part.getType()) {
			wasMethodCall = true;
			part = part.getFirstChild();
		}
		if (part == null) {
			if (wasMethodCall) {
				return "()";
			}
			return "";
		}
		while (part != null) {
			sb.append(resolveAsSimpleString(part));
			if (wasMethodCall) {
				sb.append("(");
				AST next = part.getNextSibling();
				if (next != null) {
					sb.append(resolveAsSimpleString(part.getNextSibling()));
				}
				sb.append(")");
				return sb.toString();
			}
			part = part.getNextSibling();
			if (part != null) {
				if (separator != null) {
					sb.append(separator);
				}
			}
		}
		return sb.toString();
	}

	AST handleTaskClosure(String enameString, Item item, AST lastAst) {
		if (lastAst == null) {
			return null;
		}
		lastAst = handleTaskNameResolving(enameString, item, lastAst);
		lastAst = handleTaskTypeResolving(item, lastAst);
		return lastAst;
	}

	AST handleTaskTypeResolving(Item item, AST lastAst) {
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

	AST handleTaskNameResolving(String enameString, Item item, AST lastAst) {
		if (lastAst == null) {
			return null;
		}
		if (lastAst.getType() == ELIST) {
			AST elist = lastAst;
			AST methodCall2 = elist.getFirstChild();
			if (methodCall2 != null) {
				if (GroovyTokenTypes.SL == methodCall2.getType()) {
					/* << */
					methodCall2 = methodCall2.getFirstChild();
				}
				AST name2 = methodCall2.getFirstChild();
				if (name2 != null) {
					enameString = enameString + name2.getText();
					item.setName(enameString);
					lastAst = name2.getNextSibling();
				}
			}
		}
		return lastAst;
	}

	/**
	 * naming with dot: xyz.abc.bla.tests =>
	 * 
	 * . ->. ->. ->xyz abc bla tests
	 */
	String resolveName(AST ast) {
		StringBuilder sb = new StringBuilder();
		resolveName(sb, ast);
		return sb.toString();
	}

	void resolveName(StringBuilder sb, AST ast) {
		if (ast == null) {
			return;
		}
		if (ast.getType() == GroovyTokenTypes.ELIST) {
			appendELISTParts(sb, ast);
		} else if (ast.getType() == GroovyTokenTypes.SL) {

		} else if (ast.getType() == GroovyTokenTypes.DOT) {
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
				} else {
					if (next.getType() == GroovyTokenTypes.CLOSABLE_BLOCK) {
						return;
					}
					if (next.getType() == GroovyTokenTypes.ELIST) {
						sb.append(" ");
						appendELISTParts(sb, next);
					}
				}
			}

		}

	}

	private void appendELISTParts(StringBuilder sb, AST next) {
		AST firstChild = next.getFirstChild();
		sb.append(resolveAsSimpleString(firstChild));
	}

	Item createItem(Context context, AST ast) {
		Item item = new Item();
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

	/**
	 * Resolve name from given object<br>
	 * <br>
	 * 
	 * <pre>
	 * configure{} -> "configure"
	 * configure('bla') {} ->"configure('bla')
	 * configure(){} -> "configure"
	 * </pre>
	 * 
	 * @param methodCall
	 * @return name
	 */
	String resolveMethodCallName(AST methodCall) {
		if (GroovyTokenTypes.METHOD_CALL != methodCall.getType()) {
			return "<no method call/>";
		}
		StringBuilder sb = new StringBuilder();

		String methodParams = null;

		AST firstChild = methodCall.getFirstChild();
		if (firstChild == null) {
			return sb.toString();
		}
		String methodName = resolveName(firstChild);
		if (methodName == null) {
			methodName = "<noMethodName/>";
		}
		AST next = firstChild.getNextSibling();
		if (next != null) {
			int type = next.getType();
			if (type != CLOSABLE_BLOCK) {
				methodParams = resolveName(next);
			}
		}
		sb.append(methodName);
		if (methodParams != null && methodParams.length()>0) {
			sb.append(" ");
			sb.append(methodParams);
		}
		return sb.toString();
	}

}

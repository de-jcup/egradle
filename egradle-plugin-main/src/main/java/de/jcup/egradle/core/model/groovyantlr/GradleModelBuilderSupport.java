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

	private void appendParameterDef(AST paramDef, List<String> paramDefList) {
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

	private void appendParameterDefChilren(AST paramDef, List<String> paramDefList) {
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
		String methodName = resolveAsSimpleString(methodCall,true);
		item.setConfiguration(configuration.getText());
		item.setName(depencyName);
		item.setIdentifier(methodName);
		
		return item;
	}

	void handleApplyType(Item item, AST nextAST) {
		if (nextAST == null) {
			return;
		}
		if (GroovyTokenTypes.ELIST != nextAST.getType()) {
			return;
		}
		/* parameter -e.g. apply from/plugin 'bla' */
		AST elist = nextAST;

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

	AST handleTasksWithTypeClosure(String enameString, Item item, AST nextAST) {
		AST newLastAst = nextAST;
		if (enameString.startsWith("tasks.withType")){
			String type = StringUtils.substringAfterLast(enameString," ");
			item.setType(type);
			
			if (nextAST == null) {
				return null;
			}
			if (nextAST.getType() == ELIST) {
				AST elist = nextAST;
				AST methodCall2 = elist.getFirstChild();
				if (methodCall2 != null) {
					if (GroovyTokenTypes.SL == methodCall2.getType()) {
						/* << */
						methodCall2 = methodCall2.getFirstChild();
					}
					AST taskType = methodCall2.getFirstChild();
					item.setName("tasks.withType("+taskType+")");
					AST other = elist.getNextSibling();
					newLastAst=other;
				}
			}
			return newLastAst;
			
		}
		return newLastAst;
	}
	
	
	/**
	 * @param enameString
	 * @param item
	 * @param nextAST
	 * @return next AST to inspect for further details. If the next hierarchy part is a closure the closure element (CLOSABLE_BLOCK=50) must be returned!
	 */
	AST handleTaskClosure(String enameString, Item item, AST nextAST) {
		if (nextAST == null) {
			return null;
		}
		ASTResultInfo nextASTData = handleTaskNameResolving(enameString, item, nextAST);
		if (nextASTData==null){
			return null;
		}
		if (nextASTData.terminated){
			return nextASTData.nextAST;
		}
		nextAST=nextASTData.nextAST;
		nextAST = handleTaskTypeResolving(item, nextAST);
		return nextAST;
	}

	/**
	 * @param item
	 * @param nextAST
	 * @return next AST to inspect for further details. If the next hierarchy part is a closure the closure element (CLOSABLE_BLOCK=50) must be returned!
	 */
	private AST handleTaskTypeResolving(Item item, AST nextAST) {
		if (nextAST == null) {
			return null;
		}
		if (GroovyTokenTypes.ELIST != nextAST.getType()) {
			return nextAST;
		}
		/* parameter -e.g. task mytask (type: xyz) */
		AST elist = nextAST;
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

	/**
	 * Returns next ast to inspect for further details. If the next hierarchy part is a closure the closure element (CLOSABLE_BLOCK=50) must be returned!
	 * @param enameString
	 * @param item
	 * @param nextAST
	 * @return
	 */
	private ASTResultInfo handleTaskNameResolving(String enameString, Item item, AST nextAST) {
		if (nextAST == null) {
			return null;
		}
		ASTResultInfo result = new ASTResultInfo();
		result.nextAST=nextAST;
		if (nextAST.getType() == ELIST) {
			AST elist = nextAST;
			AST methodCall2 = elist.getFirstChild();
			if (methodCall2 != null) {
				if (GroovyTokenTypes.SL == methodCall2.getType()) {
					/* << */
					AST slChild = methodCall2.getFirstChild();
					if (slChild!=null){
						result.nextAST=slChild.getNextSibling();
						/* handle type in another way */
						AST bracketChild = slChild.getFirstChild();
						if (bracketChild!=null){
							AST elistOfBracket = bracketChild.getNextSibling();
							handleTaskTypeResolving(item,elistOfBracket);
						}
					}
					result.terminated=true;
					
					return result;
				}
				AST name2 = methodCall2.getFirstChild();
				if (name2 != null) {
					enameString = enameString + name2.getText();
					item.setName(enameString);
					result.nextAST = name2.getNextSibling();
				}
			}
		}
		return result;
	}
	
	private class ASTResultInfo{
		private AST nextAST;
		private boolean terminated;
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
	/**
	 * Will NOT resolve method call names! (does it not greedy...)
	 * @param ast
	 * @return
	 */
	String resolveAsSimpleString(AST ast) {
		return resolveAsSimpleString(ast,false);
	}
	/**
	 * Resolve AST parts as simple string 
	 * @param ast
	 * @param greedy - if true, even method call's etc. are resolved!
	 * @return string
	 */
	private String resolveAsSimpleString(AST ast, boolean greedy) {
		if (ast == null) {
			return "";
		}
		int type = ast.getType();
		if (GroovyTokenTypes.STRING_LITERAL == type) {
			return ast.getText();
		} else if (GroovyTokenTypes.STRING_CONSTRUCTOR == type) {
			return resolveStringOfFirstChildAndSiblings(ast);
		} else if (GroovyTokenTypes.METHOD_CALL == type) {
			if (greedy){
				return resolveStringOfFirstChildAndSiblings(ast);
			}
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

	/**
	 * Creates an item, filled with information by context. Will do initial
	 * setup for item (e.g. offset, length etc.)
	 * 
	 * @param context
	 * @param ast
	 * @return item, never <code>null</code>
	 */
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
			int lineLast = gast.getLineLast();
			int columnLast = gast.getColumnLast();
			int offset2 = context.buffer.getOffset(lineLast, columnLast);

			int length = offset2 - offset1;
			if (length < 0) {
				/* fall back */
				length = columnLast - column;
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
			return "--no method call--";
		}

		AST firstChild = methodCall.getFirstChild();
		if (firstChild == null) {
			return "--no method child--";
		}
		String methodName = resolveName(firstChild);
		if (methodName == null) {
			methodName = "--no method name--";
		}
		return methodName;
	}

	private void resolveName(StringBuilder sb, AST ast) {
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
				int type = next.getType();
				if (type == GroovyTokenTypes.IDENT) {
					sb.append('.');
					sb.append(next.getText());
				} else if (type == ELIST) {
					appendELIST(sb, next);
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
						appendELIST(sb, next);
					}
				}
			}

		}

	}

	private void appendELIST(StringBuilder sb, AST next) {
		sb.append(" ");
		appendELISTParts(sb, next);
	}

	private void appendELISTParts(StringBuilder sb, AST next) {
		AST firstChild = next.getFirstChild();
		sb.append(resolveAsSimpleString(firstChild));
	}

	private String resolveExpressionName(AST firstChild) {
		String x = resolveStringOfFirstChildAndSiblings(firstChild);
		return x;
	}

	private String resolveStringOfFirstChildAndSiblings(AST ast) {
		return resolveStringOfFirstChildAndSiblings(ast, null);
	}

	private String resolveStringOfFirstChildAndSiblings(AST ast, String separator) {
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

	/**
	 * Resolves parameter list
	 * 
	 * @param elist
	 * @return list, never <code>null</code>
	 */
	public List<String> resolveParameterList(AST elist) {
		List<String> list = new ArrayList<>();
		if (elist == null) {
			return list;
		}

		if (elist.getType() != ELIST) {
			return list;
		}
		resolveParameterList(list, elist);

		return list;
	}

	private String resolveAsParameterDescription(AST ast) {
		if (ast == null) {
			return "";
		}
		int type = ast.getType();
		switch (type) {
		case STRING_LITERAL:
		case STRING_CH:
		case STRING_CONSTRUCTOR:
		case STRING_NL:
			return "java.lang.String";
		case ARRAY_DECLARATOR:
			return "Object[]";
		case ELIST:
			return "java.util.List";
		case LABELED_ARG:
			return "java.util.Map";
		case LITERAL_boolean:
			return "boolean";
		case LITERAL_byte:
			return "byte";
		case LITERAL_char:
			return "char";
		case NUM_BIG_DECIMAL:
			return "java.math.BigDecimal";
		case NUM_BIG_INT:
			return "java.math.BigInteger";
		case LITERAL_short:
			return "short";
		case NUM_INT:
		case LITERAL_int:
			return "int";
		case NUM_FLOAT:
		case LITERAL_float:
			return "float";
		case NUM_LONG:
		case LITERAL_long:
			return "long";
		case NUM_DOUBLE:
		case LITERAL_double:
			return "double";
		case LIST_CONSTRUCTOR:
			return "java.util.List";
		case MAP_CONSTRUCTOR:
			return "java.util.Map";
		case SPREAD_MAP_ARG:
			return "java.util.Map";
		}
		/* unknown, so return object : name - so after build of model maybe by variable reference check its possible to determine the correct type (in future)*/
		return "Object:" + resolveAsSimpleString(ast);
	}

	private void resolveParameterList(List<String> list, AST ast) {
		if (ast == null) {
			return;
		}
		AST firstChild = ast.getFirstChild();
		if (ast.getType() == GroovyTokenTypes.ELIST) {
			resolveParameterList(list, firstChild);
		} else if (ast.getType() == GroovyTokenTypes.METHOD_CALL) {
			resolveParameterList(list, firstChild);
		} else if (ast.getType() == GroovyTokenTypes.SL) {

		} else if (ast.getType() == GroovyTokenTypes.DOT) {
			/* is dot */
			AST content = firstChild;
			String simpleString = resolveAsParameterDescription(content);
			if (simpleString != null) {
				list.add(simpleString);
			}
			AST next = ast.getNextSibling();
			if (next != null) {
				int type = next.getType();
				if (type == GroovyTokenTypes.IDENT) {
					simpleString = resolveAsParameterDescription(next);
					if (simpleString != null) {
						list.add(simpleString);
					}
				} else if (type == ELIST) {
					resolveParameterList(list, next);
				}else if (type == GroovyTokenTypes.CLOSABLE_BLOCK) {
					list.add("groovy.lang.Closure");
				}
			}
		} else {
			/* no dot, so content separated with DOT */
			String simpleString = resolveAsParameterDescription(ast);
			if (simpleString != null) {
				list.add(simpleString);
			}
			AST next = ast.getNextSibling();
			if (next != null) {
				if (next.getType() == GroovyTokenTypes.IDENT) {
					simpleString = next.getText();
					list.add(simpleString);
				} else {
					if (next.getType() == GroovyTokenTypes.CLOSABLE_BLOCK) {
						list.add("groovy.lang.Closure");
					}
					if (next.getType() == GroovyTokenTypes.ELIST) {
						resolveParameterList(list, next);
					}
				}
			}

		}
	}

	
}

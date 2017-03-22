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
 package de.jcup.egradle.library.access;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public class GradleBuildScriptResultInspector {

	public void inspect(ASTNode node){
		inspect(node,0);
	}
	
	private void inspect(ASTNode node, int indent) {
		if (node == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("   ");
		}
		int childIndent = indent + 1;
		String in = sb.toString();
		System.out.println(in + "node:" + node.getClass());

		if (node instanceof AnnotatedNode) {
			AnnotatedNode an = (AnnotatedNode) node;
			// ClassNode declaringClass = an.getDeclaringClass();
			// if (declaringClass != null) {
			// System.out.println(in + "-declaring class:" + declaringClass);
			// }
		}
		// System.out.println(in+"-meta:"+node.getNodeMetaData());
		if (node instanceof Statement) {
			Statement statement = (Statement) node;
			// System.out.println(in+"-labels:"+statement.getStatementLabels());
			if (statement instanceof BlockStatement) {
				BlockStatement blockStatement = (BlockStatement) statement;
				inspectBlockStatement(childIndent, blockStatement);
			} else if (statement instanceof ReturnStatement) {
				ReturnStatement rs = (ReturnStatement) statement;
				inspectReturnStatement(in, childIndent, rs);
			}
		} else if (node instanceof Expression) {
			Expression expr = (Expression) node;

			if (node instanceof MethodCallExpression) {
				MethodCallExpression mc = (MethodCallExpression) node;
				inspectMethodCallExpression(mc, childIndent, in);
			} else if (node instanceof ConstantExpression) {
				ConstantExpression ce = (ConstantExpression) node;
				inspectConstantExpression(ce, in);
			} else if (node instanceof ArgumentListExpression) {
				ArgumentListExpression ale = (ArgumentListExpression) node;
				inspectArgumentList(ale, childIndent);
			} else if (node instanceof ClosureExpression) {
				ClosureExpression ce = (ClosureExpression) node;
				inspectClosureExpression(in, childIndent, ce);
			} else if (node instanceof TupleExpression) {
				TupleExpression te = (TupleExpression) node;
				List<Expression> children = te.getExpressions();
				for (Expression child : children) {
					inspect(child, childIndent);
				}
			}else if (node instanceof VariableExpression) {
				VariableExpression ve = (VariableExpression) node;
				inspectVariableExpression(childIndent, in,ve);
			} else if (node instanceof NamedArgumentListExpression) {
				NamedArgumentListExpression ne = (NamedArgumentListExpression) node;
				inspectNamedArgumentList(childIndent, in, ne);
			} else if (node instanceof GStringExpression){
				GStringExpression gs = (GStringExpression) node;
				inspectGStringExpression(childIndent, in, gs);
			} else if (node instanceof PropertyExpression){
				PropertyExpression pe = (PropertyExpression) node;
				inspectPropertyExpression(in, pe);
			}else {
				System.out.println(in + "-unhandled expression -type:" + expr.getType().getName());
				// inspect(, childIndent);
			}
		} else if (node instanceof ClassNode) {
			ClassNode classNode = (ClassNode) node;
			inspectClassNode(childIndent, in, classNode);
		} else if (node instanceof MethodNode) {
			MethodNode methodNode = (MethodNode) node;
			inspectMethodNode(childIndent, in, methodNode);
		} else if (node instanceof Parameter) {
			Parameter p = (Parameter) node;
			inspectParameter(in, p);
		} else{
			System.out.println("!!!!!!!!!!!!!!!!!!!! unhandled Node:"+node);
		}

	}

	private void inspectVariableExpression(int childIndent, String in, VariableExpression ve) {
		System.out.println(in+"-variable origin type:"+ve.getOriginType().getName());
		
	}

	private void inspectClosureExpression(String in, int childIndent, ClosureExpression ce) {
		System.out.println(in+"-parameters:");
		for (Parameter p : ce.getParameters()){
			inspectParameter(in+"  ", p);
		}
		System.out.println(in+"-type:");
		inspectClassNode(childIndent, in, ce.getType().getComponentType());
		
		System.out.println(in+"-code:");
		inspect(ce.getCode(), childIndent);
	}

	private void inspectBlockStatement(int childIndent, BlockStatement blockStatement) {
		List<Statement> children = blockStatement.getStatements();
		for (Statement child : children) {
			inspect(child, childIndent);
		}
	}

	private void inspectReturnStatement(String in, int childIndent, ReturnStatement rs) {
		System.out.println(in+"// return statement:"+rs.getText());
		Expression expression = rs.getExpression();
		inspect(expression, childIndent);
	}

	private void inspectNamedArgumentList(int childIndent, String in, NamedArgumentListExpression ne) {
		List<MapEntryExpression> mapEntryExpressions = ne.getMapEntryExpressions();
		for (MapEntryExpression me: mapEntryExpressions){
			Expression key = me.getKeyExpression();
			System.out.println(in+"- key:");
			inspect(key, childIndent);
			
			Expression val = me.getValueExpression();
			System.out.println(in+"- val:");
			inspect(val, childIndent);

		}
	}

	private void inspectParameter(String in, Parameter p) {
		System.out.print(in + "name:" + p.getName());
		System.out.println(", type:" + p.getType());
	}

	private void inspectMethodNode(int childIndent, String in, MethodNode methodNode) {
		System.out.println(in + "methodName:" + methodNode.getName());
		System.out.println(in+"class="+methodNode.getClass());
		if (true)
			return;
		Parameter[] params = methodNode.getParameters();
		for (Parameter p : params) {
			inspect(p, childIndent);
		}
	}

	private void inspectClassNode(int childIndent, String in, ClassNode classNode) {
		System.out.print(in + "*classNode:");
		if (classNode==null){
			System.out.println("null");
			return;
		}
		System.out.println(classNode.getName());
		List<MethodNode> methods = classNode.getAllDeclaredMethods();
		System.out.println(in + "-methods:");
		for (MethodNode method : methods) {
			inspect(method, childIndent);
		}
	}

	private void inspectPropertyExpression(String in, PropertyExpression pe) {
		System.out.println(in+"property:"+pe.getPropertyAsString());
	}

	private void inspectGStringExpression(int childIndent, String in, GStringExpression gs) {
		List<Expression> values = gs.getValues();
		System.out.println(in+"**Gstring values:");
		for (Expression val: values){
			inspect(val,childIndent);
		}
	}

	private void inspectArgumentList(ArgumentListExpression ale, int childIndent) {
		List<Expression> children = ale.getExpressions();
		for (Expression child : children) {
			inspect(child, childIndent);
		}
	}

	private void inspectConstantExpression(ConstantExpression ce, String in) {
		
		String constantName = ce.getConstantName();
		if (constantName!=null){
			System.out.println(in + "name:" + constantName);
		}
		System.out.println(in+"constvalue:"+ce.getValue());
	}

	private void inspectMethodCallExpression(MethodCallExpression mc, int childIndent, String in) {
		String name = mc.getMethodAsString();
		System.out.println(in+"-method as string:"+name);
		System.out.println("- generic types:"+ mc.getGenericsTypes());
		System.out.println("- type:"+mc.getType().getName());
		Expression method = mc.getMethod();
		inspect(method, childIndent);
//		System.out.println(in+"-type:"+mc.getType());
		Expression args = mc.getArguments();
		System.out.println(in + "-arguments:");
		inspect(args, childIndent);
		// System.out.println(in + "-method:");
		// System.out.println(in + "-target:");
		// inspect(mc.getMethodTarget(), childIndent);
		ASTNode receiver = mc.getReceiver();
		if (receiver!=null){
			System.out.println(in+"// Receiver found ...");
			inspect(receiver, childIndent);
		}
	}
}

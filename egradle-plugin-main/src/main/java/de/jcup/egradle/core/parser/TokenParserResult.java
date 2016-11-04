package de.jcup.egradle.core.parser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenParserResult {
	
	private static Map<Integer,String> INDENT_CACHE = new HashMap<>();
	private TokenFilter filter;
	private String description = "NONE";

	private List<Token> tokens=new ArrayList<>();
	
	public void add(Token token) {
		if (filter!=null){
			if (filter.isFiltered(token)){
				return;
			}
		}
		tokens.add(token);
	}

	public List<Token> getTokens() {
		return tokens;
	}
	
	public void print(){
		print(System.out);
	}
	
	public void print(PrintStream stream){
		stream.println(this);
		for (Token token: tokens){
			printToken(1, token, stream);
		}
	}
	
	/**
	 * When filter is set adding tokens are filtered and only not ignored parts are accepted by {@link #add(Token)}
	 * @param filter filter to set
	 */
	public void setTokenFilter(TokenFilter filter){
		this.filter=filter;
	}
	

	@Override
	public String toString() {
		return "TokenParserResult [description=" + description + "]";
	}

	private void printToken(int indent, Token token, PrintStream stream) {
		String indentStr = INDENT_CACHE.get(indent);
		if (indentStr==null){
			StringBuilder sb = new StringBuilder();
			for (int i=0;i<indent;i++){
				sb.append(' ');
			}
			indentStr=sb.toString();
			INDENT_CACHE.put(indent, indentStr);
		}
		stream.print(indentStr);
		stream.println(token);
		if (token.hasChildren()){
			for (Token child: token.getChildren()){
				printToken(indent+3, child, stream);
			}
		}
		
	}

	private List<Problem> problems = new ArrayList<>();
	
	public boolean hasProblems(){
		return problems.size()>0;
	}
	
	public List<Problem> getProblems() {
		return Collections.unmodifiableList(problems);
	}
	
	public void addProblem(String messge , int offset) {
		Problem problem = new Problem();
		problem.offset=offset;
		problem.messsage=messge;
		problems.add(problem);
	}
	
	public class Problem{
		private String messsage;
		private int offset;
		public String getMesssage() {
			return messsage;
		}
		public int getOffset() {
			return offset;
		}
	}

}

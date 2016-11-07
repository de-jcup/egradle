package de.jcup.egradle.core.token.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenImpl;

public class TokenParserResult {
	
	private String description = "NONE";
	private TokenImpl root;
	
	TokenParserResult(TokenImpl root){
		this.root=root;
	}
	
	public Token getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return "TokenParserResult [description=" + description + "]";
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

	public void print() {
		root.print();
	}


}

package de.jcup.egradle.core.model.groovyantlr;

import antlr.collections.AST;
import de.jcup.egradle.core.api.Filter;

public class GroovyTokenTypefilter implements Filter{

		private int filteredType;

		public GroovyTokenTypefilter(int type){
			this.filteredType=type;
		}
	
		@Override
		public boolean isFiltered(Object obj) {
			boolean isAst = obj instanceof AST;
			if (!isAst){
				return false;
			}
			AST ast = (AST) obj;
			int type = ast.getType();
			if (filteredType==type){
				return true;
			}
			return false;
		}
		
	}
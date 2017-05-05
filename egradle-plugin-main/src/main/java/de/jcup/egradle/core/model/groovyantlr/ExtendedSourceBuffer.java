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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.antlr.LineColumn;
import org.codehaus.groovy.antlr.SourceBuffer;

import de.jcup.egradle.core.text.OffsetCalculator;

/**
 * Full override of SourceBuffer -reason: SourceBuffer does not support offset calculation (which is necessary for eclipse) and also
 * handles not different line endings correct!
 * @author Albert Tregnaghi
 *
 */
class ExtendedSourceBuffer extends SourceBuffer {

	 	private final List<StringBuilder> lines = new ArrayList<>();
	    private StringBuilder current;
	    private boolean frozen;
		private boolean crBefore;
	    
	    public ExtendedSourceBuffer() {
	        //lines.add(new StringBuffer()); // dummy row for position [0] in the List
	        synchronized(lines){
	        	startNewLine();
	        }
	    }

	    /**
	     * Obtains a snippet of the source code within the bounds specified
	     * @param start (inclusive line/ inclusive column)
	     * @param end (inclusive line / exclusive column)
	     * @return specified snippet of source code as a String, or null if no source available
	     */
	    public String getSnippet(LineColumn start, LineColumn end) {
	    	synchronized(lines){
	    		 // preconditions
		        if (start == null || end == null) { return null; } // no text to return
		        if (start.equals(end)) { return null; } // no text to return
		        if (lines.size() == 1 && current.length() == 0) { return null; } // buffer hasn't been filled yet

		        // working variables
		        int startLine = start.getLine();
		        int startColumn = start.getColumn();
		        int endLine = end.getLine();
		        int endColumn = end.getColumn();

		        // reset any out of bounds requests
		        if (startLine < 1) { startLine = 1;}
		        if (endLine < 1) { endLine = 1;}
		        if (startColumn < 1) { startColumn = 1;}
		        if (endColumn < 1) { endColumn = 1;}
		        if (startLine > lines.size()) { startLine = lines.size(); }
		        if (endLine > lines.size()) { endLine = lines.size(); }

		        // obtain the snippet from the buffer within specified bounds
		        StringBuffer snippet = new StringBuffer();
		        for (int i = startLine - 1; i < endLine;i++) {
		            String line = lines.get(i).toString();
		            if (startLine == endLine) {
		                // reset any out of bounds requests (again)
		                if (startColumn > line.length()) { startColumn = line.length();}
		                if (startColumn < 1) { startColumn = 1;}
		                if (endColumn > line.length()) { endColumn = line.length() + 1;}
		                if (endColumn < 1) { endColumn = 1;}

		                line = line.substring(startColumn - 1, endColumn - 1);
		            } else {
		                if (i == startLine - 1) {
		                    if (startColumn - 1 < line.length()) {
		                        line = line.substring(startColumn - 1);
		                    }
		                }
		                if (i == endLine - 1) {
		                    if (endColumn - 1 < line.length()) {
		                        line = line.substring(0,endColumn - 1);
		                    }
		                }
		            }
		            snippet.append(line);
		        }
		        return snippet.toString();
	    	}
	       
	    }

	    /**
	     * Writes the specified character into the buffer
	     * @param c
	     */
	    public void write(int c) {
	    	synchronized(lines){
	    		if (frozen){
	    			throw new IllegalStateException("lines are frozen!");
	    		}
	    		
	    		if (crBefore && c !='\n') {
	    			/* not a windows new line (\ but a mac new line - so we must start the new line*/
	    			startNewLine();
	    		}
	    		/* reset - no more necessary*/
	    		crBefore=false;
	    		
	    		if (c != -1) {
	    			current.append((char)c);
	    		}
	    		if (c == '\n') {
	    			startNewLine(); /* handles abc\n as also abc\r\n!*/
	    		}
	    		if (c == '\r'){
	    			/* this can be also a \r\n - so we ignore*/
	    			crBefore=true;
	    		}
	    	}
	    }

		private void startNewLine() {
			current = new StringBuilder();
			lines.add(current);
		}
	
	/* ----------------------------------------------------------- */
	/* own parts:*/
	/* ---------------------------------*/
	OffsetCalculator calculator;
	StringBuilder[] frozenLinesAsArray;
	
	public int getOffset(int line, int column) {
		ensureFrozen();
		return getCalculator().calculatetOffset(frozenLinesAsArray, line, column);
	}

	void ensureFrozen() {
		synchronized(lines){
			if (!frozen){
				frozen=true;
				if (lines==null || lines.size()==0){
					frozenLinesAsArray=new StringBuilder[]{new StringBuilder()};
				}else{
					frozenLinesAsArray = lines.toArray(new StringBuilder[lines.size()]);
				}
			}
		}
	}

	void assertNotFrozen(){
		if (frozen){
			throw new IllegalStateException("frozen!");
		}
	}
	
	private OffsetCalculator getCalculator() {
		if (calculator == null) {
			calculator = new OffsetCalculator();
		}
		return calculator;
	}

	/**
	 * Its necessary to have line endings on each line. If last line has none, these will be appended
	 */
	public void appendLineEndToLastLineIfMissing() {
		ensureFrozen();
		StringBuilder lastLine = frozenLinesAsArray[frozenLinesAsArray.length-1];
		int length = lastLine.length();
		boolean needsAppend= false;
		needsAppend = needsAppend || length==0;
		needsAppend = needsAppend || !("\n".equals(lastLine.substring(length-1)));
		
		if (needsAppend){
			lastLine.append("\n");
		}
	}
	

}
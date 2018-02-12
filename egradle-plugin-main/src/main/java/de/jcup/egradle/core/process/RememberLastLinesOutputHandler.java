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
 package de.jcup.egradle.core.process;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class RememberLastLinesOutputHandler implements OutputHandler {

	ArrayBlockingQueue<String> queue;
	OutputHandler chainedOutputHandler;
	
	/**
	 * Creates an output outputHandler which remembers maximum entries
	 * max maximum entry amount to remember. When output exceeds amount of entries only the newest will remain
	 * @param max 
	 */
	public RememberLastLinesOutputHandler(int max) {
		if (max>0){
			queue = new ArrayBlockingQueue<>(max);
		}
	}
	
	public void setChainedOutputHandler(OutputHandler chainedOutputHandler) {
		this.chainedOutputHandler = chainedOutputHandler;
	}

	@Override
	public void output(String line) {
		if (queue!=null){
			if (queue.remainingCapacity() == 0) {
				queue.poll();
			}
			queue.add(line);
		}
		if (chainedOutputHandler!=null){
			chainedOutputHandler.output(line);
		}
	}

	public List<String> createOutputToValidate() {
		if (queue==null){
			return Collections.emptyList();
		}
		List<String> list = Arrays.asList(queue.toArray(new String[queue.size()]));
		return list;
	}
}

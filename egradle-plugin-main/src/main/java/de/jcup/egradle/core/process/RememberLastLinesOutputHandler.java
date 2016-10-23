package de.jcup.egradle.core.process;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import de.jcup.egradle.core.process.OutputHandler;

public class RememberLastLinesOutputHandler implements OutputHandler {

	ArrayBlockingQueue<String> queue;
	OutputHandler chainedOutputHandler;
	
	/**
	 * Creates an output handler which remembers maximum entries
	 * max maximum entry amount to remember. When output exceeds amount of entries only the newest will remain
	 */
	public RememberLastLinesOutputHandler(int max) {
		queue = new ArrayBlockingQueue<>(max);
	}
	
	public void setChainedOutputHandler(OutputHandler chainedOutputHandler) {
		this.chainedOutputHandler = chainedOutputHandler;
	}

	@Override
	public void output(String line) {
		if (queue.remainingCapacity() == 0) {
			queue.poll();
		}
		queue.add(line);
		if (chainedOutputHandler!=null){
			chainedOutputHandler.output(line);
		}
	}

	public List<String> createOutputToValidate() {
		List<String> list = Arrays.asList(queue.toArray(new String[queue.size()]));
		return list;
	}
}

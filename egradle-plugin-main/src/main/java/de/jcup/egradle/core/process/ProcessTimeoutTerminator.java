package de.jcup.egradle.core.process;

public class ProcessTimeoutTerminator {
	
	/**
	 * Wait for check in milliseconds
	 */
	static final int WAIT_FOR_CHECK = 200;
	
	private Process process;
	private long timeStarted;
	private OutputHandler outputHandler;
	private long timeOutInSeconds;

	public ProcessTimeoutTerminator(Process process, OutputHandler outputHandler, long timeOutInSeconds) {
		this.timeOutInSeconds = timeOutInSeconds;
		this.process = process;
		this.outputHandler = outputHandler;
	}
	
	/**
	 * Does a restart of terminator timeout
	 */
	public void reset() {
		resetTimeStarted();
	}
	
	private void resetTimeStarted() {
		timeStarted = System.currentTimeMillis();
	}

	/**
	 * Starts time out terminator
	 */
	public void start() {
		if (timeOutInSeconds == ProcessExecutor.ENDLESS_RUNNING) {
			/*
			 * when endless running is active the thread makes no sense, so just
			 * do a guard close
			 */
			return;
		}
		Thread timeoutCheckThread = new Thread(new TimeOutTerminatorRunnable(), "process-timeout-terminator");
		timeoutCheckThread.start();
	}


	private class TimeOutTerminatorRunnable implements Runnable{

		@Override
		public void run() {

			long timeOutInMillis = timeOutInSeconds * 1000;

			resetTimeStarted();

			while (process.isAlive()) {
				try {
					Thread.sleep(WAIT_FOR_CHECK);
				} catch (InterruptedException e) {
					break;
				}
				long timeAlive = System.currentTimeMillis() - timeStarted;
				if (timeAlive > timeOutInMillis) {
					if (! process.isAlive()){
						/* no termination necessary, process already terminated */
						break;
					}
					outputHandler.output("Timeout reached (" + timeOutInSeconds + " seconds) - destroy process");
					process.destroy();
					break;
				}
			}

		}
	}
	

	
}
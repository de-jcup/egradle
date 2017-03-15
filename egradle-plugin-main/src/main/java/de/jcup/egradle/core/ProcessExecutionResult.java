package de.jcup.egradle.core;

import org.apache.commons.lang3.ArrayUtils;

import de.jcup.egradle.core.process.ProcessExecutor;

public class ProcessExecutionResult {

	private Integer processResult;
	private Exception exception;
	private String[] commands;
	private boolean canceledByuser;

	/**
	 * @return <code>true</code> when not canceled and process results is {@link ProcessExecutor#PROCESS_RESULT_OK}
	 */
	public boolean isOkay() {
		return !canceledByuser &&  ProcessExecutor.PROCESS_RESULT_OK.equals(processResult);
	}
	
	/**
	 * @return <code>true</code> when canceled or process results is something else than {@link ProcessExecutor#PROCESS_RESULT_OK}
	 */
	public boolean isNotOkay(){
		return !isOkay();
	}
	
	public void setCanceledByuser(boolean canceled) {
		 this.canceledByuser=canceled;
	}
	
	public boolean isCanceledByuser() {
		return canceledByuser;
	}

	public void setCommands(String... commands) {
		this.commands = commands;
	}

	public void setException(Exception e) {
		this.exception = e;

	}

	public void setProcessResult(int processResult) {
		this.processResult = processResult;
	}

	@Override
	public String toString() {
		return "Result [processResult=" + processResult + ", exception=" + exception + "]";
	}

	public int getResultCode() {
		if (processResult == null) {
			return -1;
		}
		return processResult;
	}

	public String createDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Executed: ");
		if (ArrayUtils.isNotEmpty(commands)) {
			for (String command : commands) {
				sb.append(command);
				sb.append(" ");
			}
		}
		sb.append("\n\n");
		if (isCanceledByuser()){
			sb.append("Proccess was canceled by user");
		}else if (!isOkay()) {
			if (processResult == null) {
				sb.append("Process was terminated by unknown reason, no exit code available");
			} else {
				sb.append("Build failed with exit code " + getResultCode());
			}
		}
		return sb.toString();
	}

	

}
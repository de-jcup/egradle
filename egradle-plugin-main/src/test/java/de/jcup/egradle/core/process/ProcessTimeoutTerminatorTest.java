package de.jcup.egradle.core.process;

import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class ProcessTimeoutTerminatorTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10); 
	
	@Test
	public void terminator_with_timeout_1_second_is_not_stopping_until_last_restart_call_is_reset_calling()
			throws Exception {
		/* prepare */
		Process process = mock(Process.class, "process always alive");
		when(process.isAlive()).thenReturn(true);
		OutputHandler outputHandler = mock(OutputHandler.class);

		/* execute */
		ProcessTimeoutTerminator terminatorToTest = new ProcessTimeoutTerminator(process, outputHandler, 1);
		terminatorToTest.start();

		/* test */
		long timeStart = System.currentTimeMillis();
		do {
			terminatorToTest.reset();
			Thread.sleep(100);
			verify(process, times(0)).destroy(); // as long as this loop is
													// running the process may
													// not be destroyed
		} while (System.currentTimeMillis() - timeStart < 2000);// wait 2
																// seconds

		/* wait for terminator to end itself */
		while (terminatorToTest.isRunning()){
			Thread.sleep(200);
		}
		
		/* now terminator must have tried to destroy the process */
		verify(process, times(1)).destroy();  

	}

}

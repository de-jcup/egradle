package de.jcup.egradle.core.process;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.domain.CancelStateProvider;
import de.jcup.egradle.core.process.SimpleProcessExecutor.ProcessCancelTerminator;

public class SimpleProcessExecutorTest {

	private Process mockedProcess;
	private ProcessContext mockedProcessContext;
	private ProcessConfiguration mockedProcessConfiguration;
	private EnvironmentProvider mockedEnvironmentProvider;
	private CancelStateProvider mockedCancelStateProvider;

	@Before
	public void before() {
		mockedProcess = mock(Process.class);
		mockedProcessContext =mock(ProcessContext.class);
		mockedProcessConfiguration = mock(ProcessConfiguration.class);
		mockedEnvironmentProvider = mock(EnvironmentProvider.class);
		mockedCancelStateProvider = mock(CancelStateProvider.class);

		when(mockedProcess.isAlive()).thenReturn(true);
		when(mockedProcessContext.getCancelStateProvider()).thenReturn(mockedCancelStateProvider);
		when(mockedCancelStateProvider.isCanceled()).thenReturn(false);
	}

	@Test
	public void process_execute_terminates_process_when_cancelation_is_done_by_cancel_state_provider()
			throws Exception {

		/* prepare*/
		SimpleProcessExecutor executorToTest = new SimpleProcessExecutor(null, false, 0){
			@Override
			Process startProcess(ProcessBuilder pb) throws IOException {
				return mockedProcess;
			}
		};
		
		/* execute */
		executorToTest.execute(mockedProcessConfiguration, mockedEnvironmentProvider, mockedProcessContext, "test");
		
		/* test */
		verify(mockedProcess, never()).destroy();
		when(mockedCancelStateProvider.isCanceled()).thenReturn(true);
		
		Thread.sleep(ProcessCancelTerminator.TIME_TO_WAIT_FOR_NEXT_CANCEL_CHECK+200);
		verify(mockedProcess).destroy();
	}

}

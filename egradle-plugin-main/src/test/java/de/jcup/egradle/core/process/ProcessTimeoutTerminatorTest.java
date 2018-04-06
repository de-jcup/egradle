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
		while (terminatorToTest.isRunning()) {
			Thread.sleep(200);
		}

		/* now terminator must have tried to destroy the process */
		verify(process, times(1)).destroy();

	}

}

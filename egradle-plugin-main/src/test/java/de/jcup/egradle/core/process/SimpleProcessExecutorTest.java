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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.domain.CancelStateProvider;

/**
 * Testcase for canceling. A process execution is simulated in one thread. in
 * another a cancel request by user is done. main thread (test) is blocked until
 * cancel request goes through
 * 
 * @author Albert Tregnaghi
 *
 */
public class SimpleProcessExecutorTest {

    private static final int TIME_TO_WAIT_FOR_CANCEL = 300;
    private static final int TIME_TO_WAIT_FOR_PROCESS_NO_LONGER_ACTIVE = TIME_TO_WAIT_FOR_CANCEL + 500;

    private Process mockedProcess;
    private ProcessContext mockedProcessContext;
    private ProcessConfiguration mockedProcessConfiguration;
    private EnvironmentProvider mockedEnvironmentProvider;
    private CancelExecutionSimulation cancelExecutionSimulation;
    private boolean processAlive;

    @Before
    public void before() {
        mockedProcess = mock(Process.class);
        mockedProcessContext = mock(ProcessContext.class);
        mockedProcessConfiguration = mock(ProcessConfiguration.class);
        mockedEnvironmentProvider = mock(EnvironmentProvider.class);

        processAlive = true;
        cancelExecutionSimulation = new CancelExecutionSimulation();

        when(mockedProcessContext.getCancelStateProvider()).thenReturn(cancelExecutionSimulation);
    }

    @Test
    public void isAlive_returns_falue_from_process() throws Exception {
        /* prepare */
        SimpleProcessExecutor executorToTest = new SimpleProcessExecutor(null, false, 0);
        when(mockedProcess.isAlive()).thenReturn(false);

        /* execute */
        boolean result1 = executorToTest.isAlive(mockedProcess);

        when(mockedProcess.isAlive()).thenReturn(true);
        boolean result2 = executorToTest.isAlive(mockedProcess);

        /* test */
        assertFalse(result1);
        assertTrue(result2);

        verify(mockedProcess, times(2)).isAlive();
    }

    @Test
    public void waitFor_calls_process_wait_for_3_seconds() throws Exception {
        /* prepare */
        SimpleProcessExecutor executorToTest = new SimpleProcessExecutor(null, false, 0);

        /* execute */
        executorToTest.waitFor(mockedProcess);

        /* test */
        verify(mockedProcess).waitFor(3, TimeUnit.SECONDS);
    }

    @Test(timeout = 3000)
    public void process_execute_terminates_process_when_cancelation_is_done_by_cancel_state_provider() throws Exception {

        /* prepare */
        SimpleProcessExecutor executorToTest = new SimpleProcessExecutor(null, false, 0) {
            @Override
            Process startProcess(ProcessBuilder pb) throws IOException {
                return mockedProcess;
            }

            @Override
            boolean isAlive(Process p) {
                /*
                 * inconvenient workaround for process handling, but mockito often did not
                 * change the mocked result , so this way is necessary
                 */
                return processAlive;
            }

            @Override
            void waitFor(Process p) throws InterruptedException {
                Thread.sleep(100);
            }
        };
        /* start new thread which will return true for canceled after start */
        new Thread(new ProcessExecutionSimulation(), "process-exec-simulation").start();
        new Thread(cancelExecutionSimulation, "cancel-simulation").start();
        /* execute */
        executorToTest.execute(mockedProcessConfiguration, mockedEnvironmentProvider, mockedProcessContext, "test");

        /* test */

        verify(mockedProcess).destroy();
    }

    private class CancelExecutionSimulation implements Runnable, CancelStateProvider {

        private boolean isCanceled = false;

        @Override
        public void run() {
            try {
                Thread.sleep(TIME_TO_WAIT_FOR_CANCEL);
            } catch (InterruptedException e) {

            } finally {
                isCanceled = true;
                System.out.println("canceling done");
            }
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

    }

    private class ProcessExecutionSimulation implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(TIME_TO_WAIT_FOR_PROCESS_NO_LONGER_ACTIVE);
            } catch (InterruptedException e) {
            }
            processAlive = false;
            System.out.println("process no longer alive");

        }

    }

}

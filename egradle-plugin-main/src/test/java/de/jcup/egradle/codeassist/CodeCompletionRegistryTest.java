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
package de.jcup.egradle.codeassist;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;

public class CodeCompletionRegistryTest {

    private CodeCompletionRegistry registryToTest;

    @Before
    public void before() {
        registryToTest = new CodeCompletionRegistry();
    }

    @Test
    public void registrated_listener_is_informed_about_rebuild() {
        /* prepare */
        RegistryListener mockedRegistryListener = mock(RegistryListener.class);
        registryToTest.addListener(mockedRegistryListener);

        /* execute */
        registryToTest.init();

        /* test */
        // 2 times because of DESTROY and LOAD_PLUGINS event type
        verify(mockedRegistryListener, times(2)).onCodeCompletionEvent(any());

    }

    @Test
    public void deregistrated_listener_is_NO_longer_informed_about_rebuild() {
        /* prepare */
        RegistryListener mockedRegistryListener = mock(RegistryListener.class);
        registryToTest.addListener(mockedRegistryListener);
        registryToTest.removeListener(mockedRegistryListener);

        /* execute */
        registryToTest.init();

        /* test */
        verify(mockedRegistryListener, times(0)).onCodeCompletionEvent(any());

    }

}

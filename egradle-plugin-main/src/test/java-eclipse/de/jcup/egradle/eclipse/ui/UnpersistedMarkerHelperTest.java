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
package de.jcup.egradle.eclipse.ui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;

public class UnpersistedMarkerHelperTest {

    private UnpersistedMarkerHelper helperToTest;

    @Before
    public void before() {
        helperToTest = new UnpersistedMarkerHelper("test.me");
    }

    @Test
    public void when_nothing_registered_nothing_happens_on_removeAllRegisteredMarkers() throws Exception {
        helperToTest.removeAllRegisteredMarkers();
    }

    @Test
    public void when_marker_is_registered_but_exists_return_true_and_get_type_throws_exception_the_marker_will_be_still_removed() throws Exception {
        /* prepare */
        IMarker marker = mock(IMarker.class);
        when(marker.exists()).thenReturn(true);
        when(marker.getType()).thenThrow(new CoreException(Status.CANCEL_STATUS));
        helperToTest.handleMarkerAdded(marker);
        assertTrue(helperToTest.hasRegisteredMarkers());

        /* execute */
        helperToTest.removeAllRegisteredMarkers();

        /* test */
        assertFalse(helperToTest.hasRegisteredMarkers());
        verify(marker).getType();
        verify(marker, never()).delete();
    }

    @Test
    public void when_marker_is_registered_but_exists_return_false_the_marker_will_be_still_removed() throws Exception {
        /* prepare */
        IMarker marker = mock(IMarker.class);
        when(marker.exists()).thenReturn(false);
        when(marker.getType()).thenReturn("specialtype");
        helperToTest.handleMarkerAdded(marker);
        assertTrue(helperToTest.hasRegisteredMarkers());

        /* execute */
        helperToTest.removeAllRegisteredMarkers();

        /* test */
        assertFalse(helperToTest.hasRegisteredMarkers());
        verify(marker, never()).getType();
        verify(marker, never()).delete();
    }

    @Test
    public void when_marker_is_registered_but_exists_return_true_and_get_type_throws_no_exception_the_marker_will_be_removed() throws Exception {
        /* prepare */
        IMarker marker = mock(IMarker.class);
        when(marker.exists()).thenReturn(true);
        when(marker.getType()).thenReturn("specialtype");
        helperToTest.handleMarkerAdded(marker);
        assertTrue(helperToTest.hasRegisteredMarkers());

        /* execute */
        helperToTest.removeAllRegisteredMarkers();

        /* test */
        assertFalse(helperToTest.hasRegisteredMarkers());
        verify(marker).delete();
    }

}

package de.jcup.egradle.eclipse.ui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

public class UnpersistedMarkerHelperTest {

	private UnpersistedMarkerHelper helperToTest;

	@Before
	public void before(){
		helperToTest = new UnpersistedMarkerHelper("test.me");
	}
	
	@Test
	public void when_nothing_registered_nothing_happens_on_removeAllRegisteredMarkers() throws Exception{
		helperToTest.removeAllRegisteredMarkers();
	}
	
	@Test
	public void when_marker_is_registered_but_exists_return_true_and_get_type_throws_exception_the_marker_will_be_still_removed() throws Exception{
		/* prepare*/
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
		verify(marker,never()).delete();
	}
	
	@Test
	public void when_marker_is_registered_but_exists_return_false_the_marker_will_be_still_removed() throws Exception{
		/* prepare*/
		IMarker marker = mock(IMarker.class);
		when(marker.exists()).thenReturn(false);
		when(marker.getType()).thenReturn("specialtype");
		helperToTest.handleMarkerAdded(marker);
		assertTrue(helperToTest.hasRegisteredMarkers());
		
		/* execute */
		helperToTest.removeAllRegisteredMarkers();
		
		/* test */
		assertFalse(helperToTest.hasRegisteredMarkers());
		verify(marker,never()).getType();
		verify(marker,never()).delete();
	}
	
	@Test
	public void when_marker_is_registered_but_exists_return_true_and_get_type_throws_no_exception_the_marker_will_be_removed() throws Exception{
		/* prepare*/
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

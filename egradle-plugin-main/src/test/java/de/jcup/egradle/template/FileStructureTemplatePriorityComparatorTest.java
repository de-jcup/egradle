package de.jcup.egradle.template;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class FileStructureTemplatePriorityComparatorTest {

	private FileStructureTemplatePriorityComparator comparatorToTest;

	@Before
	public void before() {
		comparatorToTest = new FileStructureTemplatePriorityComparator();
	}

	@Test
	public void prio_1__compared__prio_1__results_in_0() {
		/* prepare */
		FileStructureTemplate o1 = mock(FileStructureTemplate.class);
		FileStructureTemplate o2 = mock(FileStructureTemplate.class);
		
		when(o1.getPriority()).thenReturn(1);
		when(o2.getPriority()).thenReturn(1);
		
		/* execute + test */
		assertEquals(0, comparatorToTest.compare(o1, o2));
	}
	
	@Test
	public void prio_0__compared__prio_1__results_in_n1() {
		/* prepare */
		FileStructureTemplate o1 = mock(FileStructureTemplate.class);
		FileStructureTemplate o2 = mock(FileStructureTemplate.class);
		
		when(o1.getPriority()).thenReturn(0);
		when(o2.getPriority()).thenReturn(1);
		
		/* execute + test */
		assertEquals(-1, comparatorToTest.compare(o1, o2));
	}
	
	@Test
	public void prio_1__compared__prio_0__results_in_1() {
		/* prepare */
		FileStructureTemplate o1 = mock(FileStructureTemplate.class);
		FileStructureTemplate o2 = mock(FileStructureTemplate.class);
		
		when(o1.getPriority()).thenReturn(1);
		when(o2.getPriority()).thenReturn(0);
		
		/* execute + test */
		assertEquals(1, comparatorToTest.compare(o1, o2));
	}
	
	@Test
	public void prio_3__compared__prio_1__results_in_1() {
		/* prepare */
		FileStructureTemplate o1 = mock(FileStructureTemplate.class);
		FileStructureTemplate o2 = mock(FileStructureTemplate.class);
		
		when(o1.getPriority()).thenReturn(3);
		when(o2.getPriority()).thenReturn(1);
		
		/* execute + test */
		assertEquals(1, comparatorToTest.compare(o1, o2));
	}
	
	@Test
	public void prio_1__compared__prio_3__results_in_n1() {
		/* prepare */
		FileStructureTemplate o1 = mock(FileStructureTemplate.class);
		FileStructureTemplate o2 = mock(FileStructureTemplate.class);
		
		when(o1.getPriority()).thenReturn(1);
		when(o2.getPriority()).thenReturn(3);
		
		/* execute + test */
		assertEquals(-1, comparatorToTest.compare(o1, o2));
	}

}

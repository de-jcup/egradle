package de.jcup.egradle.core.process;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RememberLastLinesOutputHandlerTest {

	private RememberLastLinesOutputHandler outputHandlerToTest;

	@Before
	public void before() {
		outputHandlerToTest = new RememberLastLinesOutputHandler(10);
	}
	@Test
	public void a_remembering_with_max_zero__does_not_use_queue_at_all_but_triggers_delegate() {
		/* prepare + execute*/
		outputHandlerToTest = new RememberLastLinesOutputHandler(0);
		
		/* test */
		assertNull(outputHandlerToTest.queue);
		
		List<String> output = outputHandlerToTest.createOutputToValidate();
		assertNotNull(output);
		assertTrue(output.isEmpty());
	}
	
	@Test
	public void chained_outputhandler_called_on_output(){
		/* prepare*/
		OutputHandler chainedOutputHandler = mock(OutputHandler.class);
		outputHandlerToTest.setChainedOutputHandler(chainedOutputHandler);

		/* execute*/
		outputHandlerToTest.output("line1");
		
		/* test */
		verify(chainedOutputHandler).output("line1");
	}
	@Test
	public void chained_outputhandler_called_on_output_even_when_max_is_zero(){
		/* prepare*/
		outputHandlerToTest = new RememberLastLinesOutputHandler(0);
		OutputHandler chainedOutputHandler = mock(OutputHandler.class);
		outputHandlerToTest.setChainedOutputHandler(chainedOutputHandler);

		/* execute*/
		outputHandlerToTest.output("line1");
		
		/* test */
		verify(chainedOutputHandler).output("line1");
	}

	@Test
	public void when_no_output_done_queue_is_empty() {
		/* test */
		assertTrue(outputHandlerToTest.queue.isEmpty());
	}

	@Test
	public void when_no_output_done_queue_has_10_empty_left() {
		/* test */
		assertEquals(10, outputHandlerToTest.queue.remainingCapacity());
	}

	@Test
	public void when_one_output_done_queue_has_one_entry() {
		/* execute */
		outputHandlerToTest.output("line1");
		/* test */
		assertEquals(9, outputHandlerToTest.queue.remainingCapacity());
	}

	@Test
	public void when_ten_outputs_done_queue_has_non_empty_left() {
		/* execute */
		for (int i = 1; i < 11; i++) {
			outputHandlerToTest.output("line1");
		}
		/* test */
		assertEquals(0, outputHandlerToTest.queue.remainingCapacity());
	}

	@Test
	public void when_eleven_outputs_done_queue_throws_no_error() {
		/* execute */
		for (int i = 1; i < 12; i++) {
			outputHandlerToTest.output("line1");
		}
		/* test */
		assertEquals(0, outputHandlerToTest.queue.remainingCapacity());
	}

	@Test
	public void one_ouptput_results_list_with_result_containing_element() {
		/* prepare */
		outputHandlerToTest.output("line1");

		/* execute */
		List<String> output = outputHandlerToTest.createOutputToValidate();

		/* test */
		assertEquals(1, output.size());
	}

	@Test
	public void two_ouptput_results_in_list_with_result_containing_elements_first_element_on_first_position() {
		/* prepare */
		outputHandlerToTest.output("line1");
		outputHandlerToTest.output("line2");

		List<String> expected = new ArrayList<>();
		expected.add("line1");
		expected.add("line2");

		List<String> output = outputHandlerToTest.createOutputToValidate();

		/* test */
		assertEquals(expected, output); // equals on list is also checking
										// ordering!
	}

	@Test
	public void _20_ouptput_results_in_list_with_result_containing_20_elements_only_last_elements_inside() {
		/* prepare */
		for (int i=0;i<20;i++){
			outputHandlerToTest.output("line_"+i);
		}

		List<String> expected = new ArrayList<>();
		for (int i=10;i<20;i++){
			expected.add("line_"+i);
		}

		List<String> output = outputHandlerToTest.createOutputToValidate();

		/* test */
		assertEquals(10, output.size());
		assertEquals(expected, output); // equals on list is also checking
										// ordering!
	}

}

package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Handles offset calculation by given lines. Handles also CR+LF, CR, LF correctly. See <a href="https://en.wikipedia.org/wiki/Newline">Wikipedia - NewLine</a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class OffsetCalculatorTest {

	private OffsetCalculator calculatorToTest;


	@Before
	public void before() {
		calculatorToTest = new OffsetCalculator();
	}
	
	@Test
	public void for_empty_list_getOffset_line_1_column_1_returns_0() {
		/* prepare */
		String[] lines = new String[]{};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 1,1);
		
		/* test */
		
		assertEquals(-1,offset);
		
	}

	
	@Test
	public void for_text_hello_line_1_column_1_returns_0() {
		/* prepare */
		String[] lines = new String[]{"Hello"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 1,1);
		
		/* test */
		
		assertEquals(0,offset);
	}
	
	
	@Test
	public void for_text_hello_line_2_column_1_returns_UNKNOWN() {
		/* prepare */
		String[] lines = new String[]{"Hello"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 2,1);
		
		/* test */
		
		assertEquals(-1,offset);
		
	}
	
	@Test
	public void for_text_12345_line_1_column_6_returns_UNKNOWN() {
		/* prepare */
		String[] lines = new String[]{"12345"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 1,6);
		
		/* test */
		
		assertEquals(-1,offset);
		
		
	}
	
	@Test
	public void for_text_12345_newline_6__line_1_column_6_returns_6() {
		/* prepare */
		String[] lines = new String[]{"12345\n","6"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 1,6);
		
		/* test */
		
		assertEquals(5,offset); // 6 because \n is a character and has offset... but offset starts at 0 and not on 1 like column
		
	}
	
	@Test
	public void for_text_12345_newline_6__line_1_column_7_returns_UNKNOWN() {
		/* prepare */
		String[] lines = new String[]{"12345\n","6"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 1,7);
		
		/* test */
		
		assertEquals(-1 ,offset); // -1 because line has only 6 characters: 12345\n
		
	}
	
	/**
	 * Unix new line variant with \n - line feed
	 */
	@Test
	public void newline_unix__for_text_12345_lf_6__line_2_column_1_returns_6() {
		/* prepare */
		String[] lines = new String[]{"12345\n","6"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 2,1);
		
		/* test */
		
		assertEquals(6,offset);
		
	}
	/**
	 * Mac OS new line variant with \r - carriage return
	 */
	@Test
	public void newline_mac__for_text_12345_cr_6__line_2_column_1_returns_6() {
		/* prepare */
		String[] lines = new String[]{"12345\r","6"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 2,1);
		
		/* test */
		
		assertEquals(6,offset);
		
	}
	
	/**
	 * Windows new line variant with \r\n - carraige return + line feed
	 */
	@Test
	public void newline_windows__for_text_12345_cr_lf_6_line_2_column_1_returns_7() {
		/* prepare */
		String[] lines = new String[]{"12345\r\n","6"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 2,1);
		
		/* test */
		
		assertEquals(7,offset);
		
	}

	/**
	 * Windows and Mac variant mixed
	 */
	@Test
	public void newline_windows_and_mac_mixed__for_text_12345_cr_lf_6__lf_7_line_3_column_1_returns_9() {
		/* prepare */
		String[] lines = new String[]{"12345\r\n","6\r","7"};

		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 3,1);
		
		/* test */
		
		assertEquals(9,offset);
		
	}
	
	/**
	 * Windows and Unix  variant mixed
	 */
	@Test
	public void newline_windows_and_unix_mixed__for_text_12345_cr_lf_6__cr_7_line_3_column_1_returns_10() {
		/* prepare */
		String[] lines = new String[]{"12345\r\n","6\r","7"};
		StringBuilder sb = new StringBuilder();
		for (String line: lines){
			sb.append(line);
		}
		int expectedOffset=sb.length()-1; // offset is not length, because 0..
		
		
		/* execute */
		int offset = calculatorToTest.calculatetOffset(lines, 3,1);
		
		/* test */
		
		assertEquals(expectedOffset,offset);
		
	}
	
	@Test
	public void _4_lines_of_code_length_of_start_of_first_correct_calculated() {
		/* prepare */
		List<String> lines = new ArrayList<>();
		lines.add("def variable1='Hello world... from groovy'\n");
		lines.add("\n");
		lines.add("\n");
		lines.add("def variable2='Hello world... from groovy'");
		String[] linesArray = lines.toArray(new String[lines.size()]);

		/* execute */
		int offset = calculatorToTest.calculatetOffset(linesArray, 3,1);
		
		/* test */
		String text = "def variable1='Hello world... from groovy'\n\n\n";
		int expectedOffsetOfVariable2 = text.length()-1;
		assertEquals(expectedOffsetOfVariable2,offset);
		
	}

}

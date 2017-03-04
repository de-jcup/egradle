package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GradleResourceLinkCalculatorTest {

	private GradleResourceLinkCalculator calculator;

	@Before
	public void before(){
		calculator = new GradleResourceLinkCalculator();
	}

	@Test
	public void null_0_returns_null() {
		assertNull(calculator.createResourceLinkString(null, 0));
	}
	
	@Test
	public void null_n1_returns_null() {
		assertNull(calculator.createResourceLinkString(null, -1));
	}
	
	@Test
	public void Test_n1_returns_null() {
		assertNull(calculator.createResourceLinkString("Test", -1));
	}
	
	@Test
	public void Test_0_returns_NOT_null() {
		assertNotNull(calculator.createResourceLinkString("Test", 0));
	}
	
	@Test
	public void TestBracketOpen_0_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test(", 0);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void Test_lt_0_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test<", 0);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void lt_Test_gt_0_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("<Test>", 1);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(1, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void BracketOpenTestBracketClose_1_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("(Test)", 1);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(1, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void TestArray_1_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test[]", 1);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void Test_space_Array_1_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test []", 1);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void Test_curly_brace_0_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test{", 0);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void Test_space_curly_brace_0_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("Test {", 0);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(0, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void BracketOpenTestArrayBracketClose_2_returns_NOT_null__text_contains_only_Test() {
		GradleHyperLinkResult result = calculator.createResourceLinkString("(Test[])", 2);
		assertNotNull(result);
		assertEquals("Test", result.linkContent);
		assertEquals(1, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
	
	@Test
	public void Test_3_returns_NOT_null() {
		assertNotNull(calculator.createResourceLinkString("Test", 3));
	}
	
	@Test
	public void Test_4_returns_null() {
		assertNull(calculator.createResourceLinkString("Test", 4));
	}
	@Test
	public void Test_5_returns_null() {
		assertNull(calculator.createResourceLinkString("Test", 5));
	}
	
	@Test
	public void hello_World_is_good__pos0_returns_null() {
		
		String line = "hello World is good";
		//             0123456
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 0);
		assertNull(result);
	}
	
	@Test
	public void hello_World_is_good__pos4_returns_null() {
		
		String line = "hello World is good";
		//             0123456
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 4);
		assertNull(result);
	}
	
	@Test
	public void hello_World_is_good__pos5_returns_null() {
		
		String line = "hello World is good";
		//             0123456
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 5);
		if (result!=null){
			fail("expected null but result was:"+result);
		}
	}
	
	@Test
	public void hello_World_is_good__pos6_returns_not_null__World_is_linkContent_offset_6_length_5() {
		
		String line = "hello World is good";
		//             0123456
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 6);
		assertNotNull(result);
		assertEquals("World",result.linkContent);
		assertEquals(6, result.linkOffsetInLine);
		assertEquals(5, result.linkLength);
	}
	
	@Test
	public void hello_World_is_good__pos8_returns_not_null__World_is_linkContent_offset_6_length_5() {
		
		String line = "hello World is good";
		//             012345678901234
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 8);
		assertNotNull(result);
		assertEquals("World",result.linkContent);
		assertEquals(6, result.linkOffsetInLine);
		assertEquals(5, result.linkLength);
	}
	
	@Test
	public void hello_World_is_good__pos10_returns_not_null__World_is_linkContent_offset_6_length_5() {
		
		String line = "hello World is good";
		//             012345678901234
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 10);
		assertNotNull(result);
		assertEquals("World",result.linkContent);
		assertEquals(6, result.linkOffsetInLine);
		assertEquals(5, result.linkLength);
	}

	@Test
	public void hello_World_is_good__pos11_returns_null() {
		
		String line = "hello World is good";
		//             0123456789012345678
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
		assertNull(result);
	}
	
	@Test
	public void hello_World_is_good__pos15_returns_null() {
		
		String line = "hello World is good";
		//             0123456789012345678
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
		assertNull(result);
	}
	
	@Test
	public void hello_World_is_Good__pos10_returns_not_null__World_is_linkContent_offset_6_length_5() {
		
		String line = "hello World is Good";
		//             012345678901234
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 10);
		assertNotNull(result);
		assertEquals("World",result.linkContent);
		assertEquals(6, result.linkOffsetInLine);
		assertEquals(5, result.linkLength);
	}
	
	@Test
	public void hello_World_is_Good__pos15_returns_not_null__Good_is_linkContent_offset_15_length_4() {
		
		String line = "hello World is Good";
		//             0123456789012345
		GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
		assertNotNull(result);
		assertEquals("Good",result.linkContent);
		assertEquals(15, result.linkOffsetInLine);
		assertEquals(4, result.linkLength);
	}
}

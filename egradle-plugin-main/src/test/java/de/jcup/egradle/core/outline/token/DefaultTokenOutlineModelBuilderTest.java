package de.jcup.egradle.core.outline.token;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.token.DefaultTokenOutlineModelBuilder;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenImpl;
import de.jcup.egradle.core.token.filter.TokenFilter;

public class DefaultTokenOutlineModelBuilderTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private DefaultTokenOutlineModelBuilder builderToTest;
	private Token mockedRootToken;
	private TokenFilter mockedFilter;

	@Before
	public void before() {
		mockedRootToken = mock(TokenImpl.class);
		when(mockedRootToken.getOffset()).thenReturn(10);
		when(mockedRootToken.getLength()).thenReturn(2);
		
		mockedFilter = mock(TokenFilter.class);
		
		builderToTest = new DefaultTokenOutlineModelBuilder(mockedRootToken, mockedFilter);
	}
	@Test
	public void builder_called_with_null_token_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		
		new DefaultTokenOutlineModelBuilder(null, mockedFilter);
	}
	
	@Test
	public void builder_called_with_null_tokenfilter_throws_illegal_argument() {
		
		expectedException.expect(IllegalArgumentException.class);
		
		new DefaultTokenOutlineModelBuilder(mockedRootToken, null);
	}
	
	@Test
	public void builder_called_with_token_1_item_having_no_children_returns_not_null_as_model() throws Exception {
		assertNotNull(builderToTest.build());
	}
	
	/**
	 * <pre>
	 * 			root
	 * 			|__token1
	 * 			|__token2 <-- filtered!
	 * 			|__token3
	 * 			  |__token4
	 * 
	 * </pre>
	 * @throws Exception 
	 */
	@Test
	public void builder_token_1_has_offsets_of_token2_and_token3__token3_with_child_token4_as_child_filter_ignores_token3_so_only_token1_and_token4_are_added() throws Exception{
		/* prepare */
		TokenImpl mockedToken1 = mock(TokenImpl.class);
		when(mockedToken1.getOffset()).thenReturn(11);
		when(mockedToken1.getLength()).thenReturn(2);
		
		TokenImpl mockedToken2 = mock(TokenImpl.class);
		when(mockedToken2.getOffset()).thenReturn(12);
		when(mockedToken2.getLength()).thenReturn(2);
		
		TokenImpl mockedToken3 = mock(TokenImpl.class);
		when(mockedToken3.getOffset()).thenReturn(13);
		when(mockedToken3.getLength()).thenReturn(2);
		
		TokenImpl mockedToken4 = mock(TokenImpl.class);
		when(mockedToken4.getOffset()).thenReturn(14);
		when(mockedToken4.getLength()).thenReturn(2);
		
		when(mockedRootToken.hasChildren()).thenReturn(true);
		when(mockedRootToken.getChildren()).thenReturn(Arrays.asList(mockedToken1, mockedToken2, mockedToken3));

		when(mockedToken3.hasChildren()).thenReturn(true);
		when(mockedToken3.getChildren()).thenReturn(Collections.singletonList(mockedToken4));

		when(mockedFilter.isFiltered(mockedToken2)).thenReturn(true);
		
		/* execute */
		OutlineModel model = builderToTest.build();
		
		/* test */
		OutlineItem root = model.getRoot();
		OutlineItem[] childrenUnmodifable = root.getChildren();
		assertEquals(2, childrenUnmodifable.length);
		
		OutlineItem x1 = childrenUnmodifable[0];
		OutlineItem x2 = childrenUnmodifable[1];
		
		assertEquals(mockedToken1.getOffset(),x1.getOffset());
		assertEquals(mockedToken3.getOffset(),x2.getOffset());
		assertTrue(x2.hasChildren());
		
		OutlineItem[] x2Children = x2.getChildren();
		assertEquals(1,x2Children.length);
		assertEquals(mockedToken4.getOffset(),x2Children[0].getOffset());
	}
	
	

}
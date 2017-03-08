package de.jcup.egradle.integration.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.DescriptionFinder;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.integration.IntegrationTestComponents;

public class DescriptionFinderIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();
	private DescriptionFinder finderToTest;

	@Before
	public void before() {
		finderToTest = new DescriptionFinder();
	}
	
	@Test
	public void copy_task_find_inherited_description__for_from_Object__method() throws Exception {
		/* prepare + check preconditions */
		Type copy = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		assertNotNull(copy);
		
		Set<Method> methods = copy.getMethods();
		Method methodFrom = null;
		for (Method method: methods){
			if (MethodUtils.hasSignature(method, "from", new String[]{"Object"}, true)){
				methodFrom=method;
				break;
			}
		}
		// check description would not be found by normal way:
		assertNotNull("from(Object ..) not found?!?!?" , methodFrom);
		String description = methodFrom.getDescription();
		assertTrue(description.indexOf("@inheritDoc")!=-1);
		
		/* execute */
		String foundDescription = finderToTest.findDescription(methodFrom);
		
		/* test */
		assertNotNull(foundDescription);
		if (foundDescription.indexOf("Specifies source files or directories")==-1){
			fail("Did not found expected description but:\n"+foundDescription);
		}
	}
	
	@Test
	public void copy_task_find_inherited_description__for_getIncludes__method() throws Exception {
		/* prepare + check preconditions */
		Type copy = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		assertNotNull(copy);
		
		Set<Method> methods = copy.getMethods();
		Method getIncludes = null;
		for (Method method: methods){
			if (MethodUtils.hasSignature(method, "getIncludes", new String[]{}, true)){
				getIncludes=method;
				break;
			}
		}
		// check description would not be found by normal way:
		assertNotNull("getIncludes() not found?!?!?" , getIncludes);
		String description = getIncludes.getDescription();
		assertTrue(description.indexOf("@inheritDoc")!=-1);
		
		/* execute */
		String foundDescription = finderToTest.findDescription(getIncludes);
		
		/* test */
		assertNotNull(foundDescription);
		if (foundDescription.indexOf("Returns the set of include patterns")==-1){
			fail("Did not found expected description but:\n"+foundDescription);
		}
	}
	
	@Test
	public void copy_task_find_inherited_description__for_includes_property() throws Exception {
		/* prepare + check preconditions */
		Type copy = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		assertNotNull(copy);
		
		Set<Property> properties = copy.getProperties();
		Property includes = null;
		for (Property property: properties){
			if (property.getName().equals("includes")){
				includes=property;
				break;
			}
		}
		// check description would not be found by normal way:
		assertNotNull("inncludes property not found?!?!?" , includes);
		String description = includes.getDescription();
		assertTrue(description.indexOf("@inheritDoc")!=-1);
		
		/* execute */
		String foundDescription = finderToTest.findDescription(includes);
		
		/* test */
		assertNotNull(foundDescription);
		if (foundDescription.indexOf("Returns the set of include patterns")==-1){
			fail("Did not found expected description but:\n"+foundDescription);
		}
	}
}


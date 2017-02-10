package de.jcup.egradle.test.integregation;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;

/**
 * Does some spot check to data
 * @author Albert Tregnaghi
 *
 */
public class SpotCheckIntegrationTest {


	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	
	@Test
	public void jar_has_zip_as_super_class__this_information_must_be_available_in_data() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");
		
		/* test */
		assertNotNull("Jar type not found?!?!?", jarType);
		String superTypeAsString = jarType.getSuperTypeAsString();
		assertNotNull("Super type not set as string in loaded type!",superTypeAsString);
		assertEquals("Super type implementation fails on spot check!","org.gradle.api.tasks.bundling.Zip", 
				superTypeAsString);
	}
	
	@Test
	public void jar_has_manifest_method_itself__and_also_inherited_method_createCopyAction() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");
		
		/* test */
		assertNotNull("Jar type not found?!?!?", jarType);
		String superTypeAsString = jarType.getSuperTypeAsString();
		assertNotNull("Super type not set as string in loaded type!",superTypeAsString);
		boolean foundCreateCopyAction=false;
		boolean foundManifest=false;
		for (Method m: jarType.getMethods()){
			if (m.getName().equals("manifest")){
				foundManifest=true;
			}

			if (m.getName().equals("createCopyAction")){
				foundCreateCopyAction=true;
			}
		}
		assertTrue("did not found manifest method", foundManifest);
		assertTrue("did not found inherited zip method", foundCreateCopyAction);
	}
	@Test
	public void jar_has_inherited_property_zip64() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");
		
		/* test */
		assertNotNull("Jar type not found?!?!?", jarType);
		String superTypeAsString = jarType.getSuperTypeAsString();
		assertNotNull("Super type not set as string in loaded type!",superTypeAsString);
		boolean foundZip64=false;
		for (Property p : jarType.getProperties()){
			if (p.getName().equals("zip64")){
				foundZip64=true;
				break;
			}
		}
		assertTrue("did not found inherited zip method", foundZip64);
	}
}

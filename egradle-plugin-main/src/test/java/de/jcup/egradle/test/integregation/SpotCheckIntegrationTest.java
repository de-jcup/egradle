package de.jcup.egradle.test.integregation;

import static de.jcup.egradle.test.integregation.TypeAssert.*;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;

/**
 * Does some spot check to data
 * 
 * @author Albert Tregnaghi
 *
 */
public class SpotCheckIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	@Test
	public void jar_has_interface_copy_spec(){
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.api.tasks.bundling.Jar");

		/* test */
		assertType(jarType).hasInterface("org.gradle.api.file.CopySpec");
	}
	
	@Test
	public void sourceset_fullname_has_mixin_parts_from_scala() {
		/* execute */
		Type sourceSetType = components.getGradleDslProvider().getType("org.gradle.api.tasks.SourceSet");

		/* test */
		assertType(sourceSetType).hasMethod("scala", "groovy.lang.Closure");
	}

	@Test
	public void sourceset_shortname_has_mixin_parts_from_scala() {
		/* execute */
		Type sourceSetType = components.getGradleDslProvider().getType("SourceSet");

		/* test */
		assertType(sourceSetType).hasName("org.gradle.api.tasks.SourceSet").hasMethod("scala", "groovy.lang.Closure");
	}

	@Test
	public void jar_has_zip_as_super_class__this_information_must_be_available_in_data() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasSuperType("org.gradle.api.tasks.bundling.Zip");
	}

	@Test
	public void bundling_jar_has_jar_as_super_class__this_information_must_be_available_in_data() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.api.tasks.bundling.Jar");

		/* test */
		assertType(jarType).hasSuperType("org.gradle.jvm.tasks.Jar");
	}

	@Test
	public void jar_has_manifest_method_itself__and_also_inherited_method_createCopyAction() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasMethod("manifest", "groovy.lang.Closure").hasMethod("createCopyAction");
	}

	@Test
	public void jar_has_inherited_property_zip64() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasSuperType().hasProperty("zip64");

	}
}

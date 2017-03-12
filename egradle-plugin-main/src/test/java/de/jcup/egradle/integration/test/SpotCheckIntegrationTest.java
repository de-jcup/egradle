package de.jcup.egradle.integration.test;

import static de.jcup.egradle.integration.TypeAssert.*;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.integration.IntegrationTestComponents;

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
		assertType(sourceSetType).hasName("org.gradle.api.tasks.SourceSet").hasMethod("scala", "groovy.lang.Closure");
	}

	/* FIXME ATR, 10.03.2017: fix link output :This is equivalent to calling &lt;a href='type://org.gradle.api.Project#configure(Object,'&gt;groovy.lang.Closure)&lt;/a&gt; for each of th */
	
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
	public void jar_1_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory_did_not_fail_alone_but_when_all() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		/* @formatter:off */
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			/* FIXME ATR, 09.03.2017: fails in gradle, or when all tests run in plugin-main in eclipse too, 
			 * but not when standalone execute - must be fixed .
			 * 
			 * Idea: maybe the problem comes up with ordering.
			 * getType(type1)
			 * getType(type2)
			 * ->1,2
			 * getType(type2)
			 * getType(type1)
			 * ->,1
			 * ??
			 * */
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}
	
	/**
	 * Special test case which did produce a loop inheratance problem. An example
	 * <pre>
	 * 	Class A                             Class B
	 *  
	 *    methodA:ClassB					  methodB: ClassA
	 *    
	 *  -> extends Class C					-> extends Class C
	 *  
	 *  Class C
	 *  
	 *    methodC: String
	 * <pre>
	 * 
	 * Now it depends which of the classes will be first initialized:
	 * 
	 * 1. Class A: will resolve Class B which will resolve
	 * 
	 */
	@Test
	public void jar_2_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory__failed_always_alone_and_also_when_all() {
		/* execute */
		Type copyType = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		// jar type seems to be already loaded by former call */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		/* @formatter:off */
		assertType(copyType).
			hasMethod("getTemporaryDirFactory");
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}
	

	@Test
	public void jar_3_has_manifest_method_itself__and_also_inherited_method_getTemporaryDirFactory_did_not_fail_alone_but_when_all() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		Type copyType = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");


		/* test */
		/* @formatter:off */
		assertType(jarType).
			hasMethod("manifest", "groovy.lang.Closure").
			hasMethod("getTemporaryDirFactory");
		assertType(copyType).
			hasMethod("getTemporaryDirFactory");
		/* @formatter:on */
	}

	@Test
	public void jar_has_inherited_property_zip64() {
		/* execute */
		Type jarType = components.getGradleDslProvider().getType("org.gradle.jvm.tasks.Jar");

		/* test */
		assertType(jarType).hasSuperType().hasProperty("zip64");

	}
}

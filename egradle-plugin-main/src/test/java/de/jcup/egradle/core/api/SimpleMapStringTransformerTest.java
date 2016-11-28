package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SimpleMapStringTransformerTest {
	
	@Test
	public void transform_rootproject_dir__is_transformed() {
		/* prepare*/
		Map<String, String> map = new HashMap<String, String>();
		map.put(GradleStringTransformer.ROOTPROJECT_PROJECTDIR, "iAmRoot");
		
		/* execute */
		SimpleMapStringTransformer transformerToTest = new SimpleMapStringTransformer(map);
		String result = transformerToTest.transform("${rootProject.projectDir}/libraries.gradle");
		
		/* test */
		assertEquals("iAmRoot/libraries.gradle",result);
	}

}

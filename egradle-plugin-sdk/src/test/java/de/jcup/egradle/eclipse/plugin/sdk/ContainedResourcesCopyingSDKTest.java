package de.jcup.egradle.eclipse.plugin.sdk;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.eclipse.plugin.sdk.internal.ContainedResourcesCopyingSDK;

public class ContainedResourcesCopyingSDKTest {

	private SDK sdkToTest;

	@Before
	public void before() {
		sdkToTest = new ContainedResourcesCopyingSDK("1.0.0",new File("test"));
	}
	
	@Test
	public void get_version_returns_set_version() {
		assertEquals("1.0.0",sdkToTest.getVersion());
	}
	
	@Test
	public void get_version_returns_unknown_for_manager_called_with_null() {
		sdkToTest = new ContainedResourcesCopyingSDK(null,new File("test"));
		assertEquals("unknown",sdkToTest.getVersion());
	}
	
	@Test
	public void get_version_returns_unknown_for_manager_called_with_blank_string() {
		sdkToTest = new ContainedResourcesCopyingSDK(" ",new File("test"));
		assertEquals("unknown",sdkToTest.getVersion());
	}

}

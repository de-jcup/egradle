package de.jcup.egradle.sdk.internal;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.sdk.SDK;

public class ContainedResourcesCopyingSDKTest {

	private SDK sdkToTest;
	
	private RootFolderProvider createRootFolderProvider(){
		return new RootFolderProvider() {
			
			@Override
			public File getRootFolder() {
				return new File("test");
			}
		};
	}

	@Before
	public void before() {
		sdkToTest = new ContainedResourcesCopyingSDK("1.0.0", createRootFolderProvider(),null);
	}
	
	@Test
	public void get_version_returns_set_version() {
		assertEquals("1.0.0",sdkToTest.getVersion());
	}
	
	@Test
	public void get_version_returns_unknown_for_manager_called_with_null() {
		sdkToTest = new ContainedResourcesCopyingSDK(null,createRootFolderProvider(),null);
		assertEquals("unknown",sdkToTest.getVersion());
	}
	
	@Test
	public void get_version_returns_unknown_for_manager_called_with_blank_string() {
		sdkToTest = new ContainedResourcesCopyingSDK(" ",createRootFolderProvider(),null);
		assertEquals("unknown",sdkToTest.getVersion());
	}
	
}

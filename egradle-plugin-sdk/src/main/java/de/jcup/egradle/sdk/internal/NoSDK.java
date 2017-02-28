package de.jcup.egradle.sdk.internal;

import java.io.File;

public class NoSDK extends AbstractSDK{
		
		public static NoSDK INSTANCE = new NoSDK();
		
		private NoSDK() {
			super("0.0.0");
		}

		@Override
		public boolean isInstalled() {
			return false;
		}

		@Override
		public void install() {
			
		}

		@Override
		public File getSDKInstallationFolder() {
			return null;
		}
		
	}
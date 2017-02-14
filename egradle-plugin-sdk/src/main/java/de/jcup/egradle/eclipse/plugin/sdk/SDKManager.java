package de.jcup.egradle.eclipse.plugin.sdk;

import de.jcup.egradle.eclipse.plugin.sdk.internal.NoSDK;

/**
 * SDK manager - currently the manager supports onlyone sdk per time.
 * But in future it could be a central point for downloading and managing
 * other SDKs (egradle bintray parts - special sdk package) - download via proxyadapter so usable in eclipse too.
 * @author Albert Tregnaghi
 *
 */
public class SDKManager {

	private static SDKManager INSTANCE = new SDKManager();
	private SDK currentSDK = NoSDK.INSTANCE;

	SDKManager() {
	}
	public static final SDKManager get(){
		return INSTANCE;
	}
	
	public void setCurrentSDK(SDK currentSDK) {
		this.currentSDK = currentSDK;
	}

	public SDK getCurrentSDK() {
		if (currentSDK==null){
			currentSDK=NoSDK.INSTANCE;
		}
		return currentSDK;
	}
	
	
}

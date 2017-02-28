package de.jcup.egradle.sdk;

import de.jcup.egradle.sdk.internal.NoSDK;

/**
 * SDK manager - currently the manager supports onlyone SDK per time.
 * But in future it could be a central point for downloading and managing
 * other SDKs (egradle bintray parts - special sdk package) - download via proxyadapter so usable in eclipse too.<br><br>
 * <b>Important:</b> do not move this class to another plugin location. Reason: OSGI ensures activator start is done before
 * any class inside this plugi is used. This mechanism ensures SDKManager has correctly set the SDK from this plugin when
 * used from another one - e.g. egradle editor.
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

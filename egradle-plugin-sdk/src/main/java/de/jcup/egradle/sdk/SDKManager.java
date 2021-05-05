/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.sdk;

import de.jcup.egradle.sdk.internal.NoSDK;

/**
 * SDK manager - currently the manager supports onlyone SDK per time. But in
 * future it could be a central point for downloading and managing other SDKs
 * (egradle bintray parts - special sdk package) - download via proxyadapter so
 * usable in eclipse too.<br>
 * <br>
 * <b>Important:</b> do not move this class to another plugin location. Reason:
 * OSGI ensures activator start is done before any class inside this plugi is
 * used. This mechanism ensures SDKManager has correctly set the SDK from this
 * plugin when used from another one - e.g. egradle editor.
 * 
 * @author Albert Tregnaghi
 *
 */
public class SDKManager {

    private static SDKManager INSTANCE = new SDKManager();
    private SDK currentSDK = NoSDK.INSTANCE;

    SDKManager() {
    }

    public static final SDKManager get() {
        return INSTANCE;
    }

    public void setCurrentSDK(SDK currentSDK) {
        this.currentSDK = currentSDK;
    }

    public SDK getCurrentSDK() {
        if (currentSDK == null) {
            currentSDK = NoSDK.INSTANCE;
        }
        return currentSDK;
    }

}

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
package de.jcup.egradle.eclipse.util;

/**
 * Provides states of debug features to enable. Only interesting for egradle
 * development itself.
 * 
 * @author Albert Tregnaghi
 *
 */
public interface EclipseDevelopmentSettings {
    /**
     * Feature toggle - shows more information on some points
     */
    public static final boolean DEBUG_ADD_SPECIAL_TEXTS = Boolean.parseBoolean(System.getProperty("egradle.debug.texts"));
    /**
     * Feature toggle - adds some extra menues, buttons, commands etc. E.g. allows
     * outline reloading as full antlr parts also. interesting for debug purpose
     * only. For normal usage uninteresting
     */
    public static final boolean DEBUG_ADD_SPECIAL_MENUS = Boolean.parseBoolean(System.getProperty("egradle.debug.menus"));

    /**
     * Feature toggle - enables special logging information
     */
    public static final boolean DEBUG_ADD_SPECIAL_LOGGING = Boolean.parseBoolean(System.getProperty("egradle.debug.logging"));

}

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
package de.jcup.egradle.sdk.builder.util;

import de.jcup.egradle.codeassist.dsl.Method;

/**
 * A special visitor which will estimate/calculte/use delegation target of a
 * method
 * 
 * @author Albert Tregnaghi
 *
 */
public interface DelegationTargetMethodVisitor {

    /**
     * Visits method
     * 
     * @param method
     * @return delegation target for this method or <code>null</code> if not
     *         resolveable
     */
    public String getDelegationTargetAsString(Method method);
}

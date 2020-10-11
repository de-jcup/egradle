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
package de.jcup.egradle.core.model;

/**
 * A model for outline views.
 * 
 * @author Albert Tregnaghi
 *
 */
public interface Model {

    /**
     * Finds item, starting at given offset. In contrast to
     * {@link #getItemOnlyAt(int)} when no item found at given start offset
     * algorithm tries to resolve next possible items. If no item found
     * <code>null</code> is returned
     * 
     * @param offset
     * @return item or <code>null</code>
     */
    Item getItemAt(int offset);

    /**
     * Finds item, starting at given offset. No additional fallback strategy as in
     * {@link #getItemAt(int)}. If no item found <code>null</code> is returned
     * 
     * @param offset
     * @return item or <code>null</code>
     */
    Item getItemOnlyAt(int offset);

    /**
     * Finds parent item, starting at given offset.
     * 
     * @param offset
     * @return parent item, never <code>null</code>
     */
    Item getParentItemOf(int offset);

    /**
     * Returns the root item
     * 
     * @return root item, never <code>null</code>
     */
    Item getRoot();

}
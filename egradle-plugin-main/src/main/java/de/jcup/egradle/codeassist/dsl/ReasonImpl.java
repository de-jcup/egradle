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
package de.jcup.egradle.codeassist.dsl;

public class ReasonImpl implements Reason {

    private Plugin plugin;
    private Type superType;
    private String mixinId;

    public ReasonImpl() {
    }

    public ReasonImpl setPlugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReasonImpl other = (ReasonImpl) obj;
        if (plugin == null) {
            if (other.plugin != null)
                return false;
        } else if (!plugin.equals(other.plugin))
            return false;
        return true;
    }

    @Override
    public Type getSuperType() {
        return superType;
    }

    public void setSuperType(Type superType) {
        this.superType = superType;
    }

    public void setMixinId(String id) {
        this.mixinId = id;
    }

    public String getMixinId() {
        return mixinId;
    }

}

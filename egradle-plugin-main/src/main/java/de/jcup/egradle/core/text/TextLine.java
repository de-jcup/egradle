/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.core.text;

public class TextLine {
    private int offset;
    private String content;

    /**
     * Create a text line
     * 
     * @param offset
     * @param content
     */
    public TextLine(int offset, String content) {
        this.offset = offset;
        this.content = content;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * 
     * @return content, never <code>null</code>. If conent is not set an empty
     *         string will be returned
     */
    public String getContent() {
        if (content == null) {
            content = "";
        }
        return content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TextLine: offset=");
        sb.append(offset);
        sb.append(",content='");
        sb.append(content);
        sb.append("'");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + offset;
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
        TextLine other = (TextLine) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (offset != other.offset)
            return false;
        return true;
    }

    public int getLength() {
        return getContent().length();
    }

}

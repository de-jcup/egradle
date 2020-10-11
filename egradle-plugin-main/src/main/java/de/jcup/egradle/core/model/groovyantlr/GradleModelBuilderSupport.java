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
package de.jcup.egradle.core.model.groovyantlr;

import static org.codehaus.groovy.antlr.parser.GroovyTokenTypes.*;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import antlr.collections.AST;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;

class GradleModelBuilderSupport extends AbstractGroovyModelBuilderSupport {

    public Item handleDependencyAndReturnItem(AST methodCall, Item item) {
        AST configuration = methodCall.getFirstChild();
        AST configurationParameter = null;

        if (configuration != null) {
            configurationParameter = configuration.getNextSibling();
        }
        if (configurationParameter == null) {
            return item;
        }
        String depencyName = resolveAsSimpleString(configurationParameter);
        String methodName = resolveAsSimpleString(methodCall, true);
        item.setConfiguration(configuration.getText());
        item.setName(depencyName);
        item.setIdentifier(methodName);

        return item;
    }

    public void handleApplyType(Item item, AST nextAST) {
        if (nextAST == null) {
            return;
        }
        if (GroovyTokenTypes.ELIST != nextAST.getType()) {
            return;
        }
        /* parameter -e.g. apply from/plugin 'bla' */
        AST elist = nextAST;

        AST applyKind = elist.getFirstChild();

        if (applyKind == null) {
            return;
        }
        if (GroovyTokenTypes.LABELED_ARG != applyKind.getType()) {
            return;
        }
        AST applyLabel = applyKind.getFirstChild();
        if (applyLabel == null) {
            return;
        }
        String typeStr = applyLabel.getText();
        if ("plugin".equals(typeStr)) {
            item.setItemType(ItemType.APPLY_PLUGIN);
            item.setName("apply plugin");
        } else if ("from".equals(typeStr)) {
            item.setItemType(ItemType.APPLY_FROM);
            item.setName("apply from");
        }
        AST applyTarget = applyLabel.getNextSibling();
        if (applyTarget == null) {
            return;
        }
        String target = resolveAsSimpleString(applyTarget);
        item.setTarget(target);
    }

    public AST handleTasksWithTypeClosure(String enameString, Item item, AST nextAST) {
        AST newLastAst = nextAST;
        if (enameString.startsWith("tasks.withType")) {
            String type = StringUtils.substringAfterLast(enameString, " ");
            item.setType(type);

            if (nextAST == null) {
                return null;
            }
            if (nextAST.getType() == ELIST) {
                AST elist = nextAST;
                AST methodCall2 = elist.getFirstChild();
                if (methodCall2 != null) {
                    if (GroovyTokenTypes.SL == methodCall2.getType()) {
                        /* << */
                        methodCall2 = methodCall2.getFirstChild();
                    }
                    AST taskType = methodCall2.getFirstChild();
                    item.setName("tasks.withType(" + taskType + ")");
                    AST other = elist.getNextSibling();
                    newLastAst = other;
                }
            }
            return newLastAst;

        }
        return newLastAst;
    }

    /**
     * @param enameString
     * @param item
     * @param nextAST
     * @return next AST to inspect for further details. If the next hierarchy part
     *         is a closure the closure element (CLOSABLE_BLOCK=50) must be
     *         returned!
     */
    public AST handleTaskClosure(String enameString, Item item, AST nextAST) {
        if (nextAST == null) {
            return null;
        }
        ASTResultInfo nextASTData = handleTaskNameResolving(enameString, item, nextAST);
        if (nextASTData == null) {
            return null;
        }
        if (nextASTData.terminated) {
            return nextASTData.nextAST;
        }
        nextAST = nextASTData.nextAST;
        nextAST = handleTaskTypeResolving(item, nextAST);
        return nextAST;
    }

}

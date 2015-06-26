/*
 * Copyright 2014 Attila Szegedi, Daniel Dekany, Jonathan Revusky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ctnitchie.doclet.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.Doc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

/**
 * Resolves the inline tags for a comment. Looks for a template called
 * <code>tagHandler_<i>tagName</i>.ftl</code> in the same directory as the root
 * Freemarker template, falling back to <code>tagHandler.ftl</code>. The template
 * will then be passed:
 * <dl>
 * <dt>tag</dt><dd>The Tag instance itself.</dd>
 * <dt>root</dt><dd>The RootDoc instance.</dd>
 * <dt>model</dt><dd>The Doc object whose comment is being rendered.</dd>
 * </dl>
 * @author chris.nitchie
 */
@SuppressWarnings({
        "rawtypes", "unchecked"
})
public class ResolveCommentDirective implements TemplateDirectiveModel {

    private RootDoc root;

    public ResolveCommentDirective(RootDoc root) {
        this.root = root;
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
        TemplateModel modelObj = (TemplateModel) params.get("model");
        if (modelObj == null) {
            return;
        }
        Doc model = (Doc) DeepUnwrap.unwrap(modelObj);

        for (Tag tag : model.inlineTags()) {
            Template tagTemplate = null;
            String macroName = "tagHandler_" + tag.name().substring(1) + ".ftl";
            if ("Text".equals(tag.kind())) {
                env.getOut().write(tag.text());
                continue;

            } else {
                tagTemplate = env.getTemplateForInclusion(macroName, null,
                        true, true);
                if (tagTemplate == null) {
                    tagTemplate = env.getTemplateForInclusion("tagHandler.ftl",
                            null, true, true);
                }
            }

            if (tagTemplate == null) {
                System.out.println("No template found for tag \"" + tag.name()
                        + "\"; expected " + macroName
                        + ".ftl or tagHandler.ftl");
                env.getOut().write(tag.toString());
                continue;
            }

            Map tagParams = new HashMap();
            tagParams.put("tag", tag);
            tagParams.put("root", root);
            tagParams.put("model", model);
            tagTemplate.process(tagParams, env.getOut());
        }
    }

}

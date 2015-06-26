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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Allows the chunking of Freemarker output into multiple output files. Takes
 * the following attributes:
 * <dl>
 * <dt>location</dt><dd>Required. The relative path to the output file, relative to the
 * directory containing the primary output file specified to the doclet.</dd>
 * <dt>encoding</dt><dd>The encoding to use for the output file. If not specified,
 * the same encoding used for the main output file will be used.</dd>
 * </dl>
 * <h3>Example</h3>
 * <pre>
 * <#list root.classes() as class&gt;
 *   <@file location=class.name() + ".txt"&gt;
 * Documentation for ${class.name()}!
 *   </@file&gt;
 * </#list>
 * </pre>
 */
@SuppressWarnings("rawtypes")
public class FileDirective implements TemplateDirectiveModel {

    private File basedir;

    private String dfltEncoding = "UTF-8";

    public FileDirective(File basedir, String dfltEncoding) {
        this.basedir = basedir;
        this.dfltEncoding = dfltEncoding == null ? "UTF-8" : dfltEncoding;
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {

        Object outParam = params.get("location");
        if (outParam == null) {
            throw new TemplateException("No 'location' property", env);
        }
        Object encodingProp = params.get("encoding");
        if (encodingProp == null) {
            encodingProp = this.dfltEncoding;
        }
        File outFile = new File(basedir, outParam.toString());
        outFile.getParentFile().mkdirs();
        try (FileOutputStream fout = new FileOutputStream(outFile);
                Writer out = new OutputStreamWriter(fout,
                        encodingProp.toString())) {
            if (body != null) {
                body.render(out);
            }
        }
    }

}

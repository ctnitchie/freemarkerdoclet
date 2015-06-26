/*
 * Copyright 2015 Chris Nitchie
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
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * The main Doclet implementation.
 * 
 * @author chris.nitchie
 */
public class FreemarkerDoclet {
    private static final String ROOT_TEMPLATE = "template";
    private static final String OUTPUT_FILE = "outputFile";
    private static final String OUTPUT_ENCODING = "outputEncoding";
    private static final String TEMPLATE_ENCODING = "templateEncoding";
    private static final String[] OPTIONS = new String[] {
            ROOT_TEMPLATE, OUTPUT_FILE, OUTPUT_ENCODING, TEMPLATE_ENCODING
    };

    public static boolean start(RootDoc root) throws Exception {
        Configuration config = new Configuration(Configuration.VERSION_2_3_22);

        String rootTemplate = getOption(root, ROOT_TEMPLATE, null);
        if (rootTemplate == null) {
            throw new Exception("-" + ROOT_TEMPLATE + " not specified.");
        }
        File rootFile = new File(rootTemplate);
        if (!rootFile.isFile()) {
            throw new Exception("No such file: " + rootTemplate);
        }
        config.setDirectoryForTemplateLoading(rootFile.getParentFile());
        String outEncoding = getOption(root, OUTPUT_ENCODING, "UTF-8");
        config.setOutputEncoding(outEncoding);
        config.setDefaultEncoding(getOption(root, TEMPLATE_ENCODING, "UTF-8"));

        String outputFile = getOption(root, OUTPUT_FILE, null);
        Writer out;
        if (outputFile != null) {
            out = new FileWriter(outputFile);
        } else {
            throw new Exception("-" + OUTPUT_FILE + " not specified.");
        }

        try {
            config.setSharedVariable("file", new FileDirective(new File(
                    outputFile).getParentFile(), outEncoding));
            config.setSharedVariable("wellFormed", new WellFormedDirective());
            config.setSharedVariable("resolveComment",
                    new ResolveCommentDirective(root));
            config.setSharedVariable("echo", new EchoDirective());
            
            Template template = config.getTemplate(rootFile.getName());
            Map<String, Object> model = new HashMap<>();
            model.put("root", root);
            template.process(model, out);

        } finally {
            out.close();
        }

        return true;
    }

    private static String getOption(RootDoc root, String name, String dflt) {
        return getOption(root.options(), name, dflt);
    }

    private static String getOption(String[][] options, String name, String dflt) {
        name = "-" + name;
        for (int i = 0; i < options.length; i++) {
            if (options[i][0].equals(name)) {
                if (options[i].length > 1) {
                    return options[i][1];
                } else {
                    return dflt;
                }
            }
        }
        return dflt;
    }

    private static boolean isOption(String option) {
        for (String opt : OPTIONS) {
            if (option.equals(opt)) {
                return true;
            }
        }
        return false;
    }

    public static int optionLength(String option) {
        return isOption(option.substring(1)) ? 2 : 0;
    }

    public static boolean validOptions(String[][] options,
            DocErrorReporter reporter) {
        boolean ok = true;
        if (getOption(options, ROOT_TEMPLATE, null) == null) {
            reporter.printError("No -" + ROOT_TEMPLATE + " specified.");
            ok = false;
        }
        if (getOption(options, OUTPUT_FILE, null) == null) {
            reporter.printError("No -" + OUTPUT_FILE + " specified.");
            ok = false;
        }
        return ok;
    }
}

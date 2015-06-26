Freemarker Doclet
=================

Usage
-----

Custom Javadoc [Doclets](https://docs.oracle.com/javase/6/docs/technotes/guides/javadoc/doclet/overview.html) are hard. [Freemarker](http://freemarker.org/) is easy. This simple wrapper doclet allows you to express a Javadoc doclet as a set of Freemarker templates. No Java coding required. It simply passes the  [`RootDoc`](https://docs.oracle.com/javase/8/docs/jdk/api/javadoc/doclet/com/sun/javadoc/RootDoc.html) instance to a Freemarker template.

Execute the doclet in the normal way:

    javadoc -doclet org.ctnitchie.doclet.freemarker.FreemarkerDoclet \
            -docletpath freemarkerdoclet.jar \
            -template path/to/template.ftl \
            -outputFile path/to/outputFile.html \
            [other arguments]

Within the template, build the output documentation.

    <!DOCTYPE html>
    <html>
      <head>
        <title>API Documentation</title>
      </head>
      <body>
        <h1>API Documentation</h1>
        <ul>
        <#list root.classes() as class>
          <li><a href="${class.name()?html}.html">${class.name()?html}</a>
          <@file location=class.name() + ".html">
           <#-- Generate file for this class -->
          </@file>
        </#list>
        </ul>
      </body>
    </html>

You're free to use the full range of Freemarker template features, including `<#include>`, `<#import>`, builtins, macros, etc. See the Freemarker [Template Language Reference](http://freemarker.org/docs/ref.html) for details.

Helper Directives
-----------------

The doclet provides a number of helper directives to facilitate output generation.

The `@echo` directive allows you to output messages to the console during output generation.

    <@echo message="Hey there!"/>

The `@file` directive wraps content in your template to be written to an alternative location. Specify the location of the output file, relative to the primary output file passed via `-outputFile`, using the `location` attribute. You can also specify the `encoding` of the output file.

    <#list root.classes() as class>
      <@file locatoin=class.name() + ".html" encoding="UTF-8">
        <h1>${class.name()?html} Documentation!!!!</h1>
        <ul>
        <#list class.methods() as method>
          <li>${method.signature()?html}
        </#list>
        </ul>
      </@file>
    </#list>

The `@wellFormed` directive ensures that documentation content, often authored as non-well-formed HTML, can be treated as well-formed XML using [JSoup](http://jsoup.org/).

    <?xml version="1.0" encoding="UTF-8"?>
    <methods>
      <#list class.methods() as method>
        <method>
          <name>${method.name()?xml}</name>
          <documentation>
            <@wellFormed>
              ${method.commentText()}
            </@wellFormed>
          </documentation>
        </method>
      </#list>
    </methods>

The `@resolveComment` directive will convert inline Javadoc tags from an element's comment into content appropriate for the output being generated using helper templates.

    <@resolveComment model=class/>

For details, see...

Resolving Inline Javadoc Tags
-----------------------------

There are two ways to add comment text to the output.

Directly:

    ${class.commentText()}

Or using `@resolveComment`:

    <@resolveComment model=class/>

If there are no inline tags in the comment, both will do exactly the same thing. However, if there *are* inline tags, then the former will present them as-is, whereas the latter will pass each inline tag to a helper template in the same directory as the main template, called `tagHandler_[tagname].ftl` (e.g. `tagHandler_link.ftl` for `@{link}` tags). If it doesn't find such a template, it will look for `tagHandler.ftl`, which can be used to handle arbitrary tag types as a fallback handler. If neither template file exists, the tag will be output as-is and a warning will be generated.

The helper template will be passed `tag` (the [`Tag`](https://docs.oracle.com/javase/8/docs/jdk/api/javadoc/doclet/com/sun/javadoc/Tag.html) instance), `root` (the `RootDoc` instance), and the `model` passed to `@resolveComment`.

Encodings
---------
By default the doclet assumes UTF-8 encoding for both input and output. If you need to use another encoding, pass `-templateEncoding [name]` for the template's encoding, and `-outputEncoding [name]` for the output.

Building
--------

This doclet is built using [Apache Maven](https://maven.apache.org/) and requires Java 7 or later. To build it, install Maven, then execute the `package` goal.

    mvn package

The resulting jar file `target/freemarkerdoclet.jar` embeds all of the classes from all dependencies, so you'll only need to pass it and nothing more to the `-docletpath` command-line parameter.

Samples
-------

The example files in the `samples` directory are fairly anemic at the moment, but they do include a template to generate some very basic [DITA](http://dita.xml.org/) XML content.

License
-------

This code is licensed under the Apache 2.0 license. See LICENSE.txt for details.

<#include "macros.ftl">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">
<map>
  <title>API Documentation</title>
<#list root.specifiedPackages() as package>
  <topicref navtitle="${package.name()?xml}" keys="${package.name()?xml}" href="topics/${package.name()?url_path}.dita">
    <@file location="topics/" + package.name() + ".dita">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE concept PUBLIC "-//OASIS//DTD DITA 1.2 Concept//EN" "concept.dtd">
<concept id="${package.name()?xml}">
  <title>${package.name()?xml}</title>
  <conbody>
    <section>
      <@wellFormed>
        <@resolveComment model=package/>
      </@wellFormed>
    </section>
  </conbody>
</concept>
    </@file>
    <#list package.allClasses(true) as class>
      <topicref href="topics/${(class.name() + '.dita')?url_path}" navtitle="${class.name()?xml}" keys="${class.qualifiedName()?xml}"/>
      <@echo message="Generating " + class.name() + ".dita"/>
      <@file location="topics/" + class.name() + ".dita">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE concept PUBLIC "-//OASIS//DTD DITA 1.2 Concept//EN" "concept.dtd">
<concept id="${class.qualifiedName()?xml}">
  <title>${class.name()?xml}</title>
  <conbody>
    <section>
      <@wellFormed>
        <@resolveComment model=class/>
      </@wellFormed>
    </section>
    <section id="fields">
      <title>Fields</title>
      <dl>
        <#list class.fields() as field>
          <dlentry>
            <dt>
              <codeph><#t>
                <@typeXref field.type()/> ${field.name()?xml}<#t>
              </codeph><#t>
            </dt>
            <dd>
              <@wellFormed>
                <@resolveComment model=field/>
              </@wellFormed>
            </dd>
          </dlentry>
        </#list>
      </dl>
    </section>
    <section id="methods">
      <title>Methods</title>
      <dl>
        <#list class.methods() as method>
          <dlentry>
            <dt>
                <codeph><#t>
                  <@typeXref method.returnType()/> ${method.name()?xml}(<#list method.parameters() as param><@typeXref param.type()/> ${param.name()?xml}<#if param_has_next>, </#if></#list>)<#t>
                </codeph><#t>
            </dt>
            <dd><@resolveComment model=method/></dd>
          </dlentry>
        </#list>
      </dl>
    </section>
  </conbody>
</concept>
      </@file>
    </#list>
  </topicref>
</#list>
</map>

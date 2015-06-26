<#macro typeXref type fragment="" prefix="" suffix="" text="">
<#local qname=type.qualifiedTypeName()/>
<#local lname=type.typeName()/>
<#if root.classNamed(qname?trim)?? && root.classNamed(qname?trim).included>
<xref keyref="${qname?xml}<#if fragment!=''>/${fragment}</#if>" href="${lname?url_path?xml}.dita<#if fragment!=''>#${fragment}</#if>">${prefix}<#if text=''>${lname?xml}<#else>${text}</#if>${suffix}</xref>
<#else>
${prefix}<#if text=''>${lname?xml}<#else>${text}</#if>${suffix}
</#if>
</#macro>

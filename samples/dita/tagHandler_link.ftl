<#include "macros.ftl">
<#assign mbr=(tag.referencedMemberName()!"")?url_path/>
<@typeXref type=tag.referencedClass() fragment=mbr?trim text=tag.label() suffix=("."+tag.referencedMemberName()!"")?xml/>

#!/bin/bash
DIR="$(dirname "$(readlink "$0")")"
javadoc -docletpath $DIR/../target/freemarkerdoclet.jar \
        -doclet org.ctnitchie.doclet.freemarker.FreemarkerDoclet "$@"

#!/bin/sh

version=1.0.0
classPath=build/intermediates/classes/release/com/fpliu/newton/ui/

./gradlew aR && jar cvf CustomToast-${version}.jar ${classPath}/CustomToast.class ${classPath}/CustomToast$OnDismissListener.class ${classPath}/CustomToast$1.class

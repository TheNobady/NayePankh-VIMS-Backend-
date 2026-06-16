#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
#   JAVA_HOME - location of a JDK home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
  CYGWIN* | MINGW*)
    [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
    native_path() { cygpath --path --windows "$1"; }
    ;;
esac

# set JAVA_CMD
# https://github.com/actions/setup-java/issues/501#issuecomment-1612398058
if [ -n "${JAVA_HOME-}" ] ; then
  if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
    JAVA_CMD="$JAVA_HOME/jre/sh/java"
  elif [ -x "$JAVA_HOME/bin/java" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
  else
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
  fi
else
  JAVA_CMD="$(\unset -f command 2>/dev/null; \command -v java)" || :
  if [ ! -x "${JAVA_CMD-}" ] ; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
  fi
fi

# hash://
distributionUrl="${MVNW_REPOURL-https://repo.maven.apache.org/maven2}/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
jarUrl="${MVNW_REPOURL-https://repo.maven.apache.org/maven2}/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
wrapperJar=".mvn/wrapper/maven-wrapper.jar"
wrapperMainClass="org.apache.maven.wrapper.MavenWrapperMain"

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd -- "$(dirname -- "$0")" && pwd -P)}"

if [ -r "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" ]; then
  distributionUrl="$(sed -n 's/^distributionUrl=//p' "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" | tr -d '\r')"
  jarUrl="$(sed -n 's/^wrapperUrl=//p' "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" | tr -d '\r')"
fi

TMP_DOWNLOAD_DIR="${TMPDIR-/tmp}"

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  printf %s\\n "${1}" | tr -d '[:space:]'
}

# Downloading maven-wrapper.jar if it doesn't exist
if [ ! -r "$MAVEN_PROJECTBASEDIR/$wrapperJar" ]; then
  TMP_DOWNLOAD_DIR_HOLDER="$TMP_DOWNLOAD_DIR"
  TMP_DOWNLOAD_DIR="$(mktemp -d -- "${TMP_DOWNLOAD_DIR_HOLDER}/maven-wrapper-XXXXXX")"
  jarUrl="$(trim "$jarUrl")"
  if command -v wget > /dev/null; then
    verbose "Found wget ... using wget"
    wget -nv -O "$TMP_DOWNLOAD_DIR/maven-wrapper.jar" "$jarUrl" || rm -f "$TMP_DOWNLOAD_DIR/maven-wrapper.jar"
  elif command -v curl > /dev/null; then
    verbose "Found curl ... using curl"
    curl -fsSL -o "$TMP_DOWNLOAD_DIR/maven-wrapper.jar" "$jarUrl" || rm -f "$TMP_DOWNLOAD_DIR/maven-wrapper.jar"
  else
    die "Could not find wget or curl to download maven-wrapper.jar"
  fi
  if [ -r "$TMP_DOWNLOAD_DIR/maven-wrapper.jar" ]; then
    mkdir -p "$(dirname -- "$MAVEN_PROJECTBASEDIR/$wrapperJar")"
    mv -- "$TMP_DOWNLOAD_DIR/maven-wrapper.jar" "$MAVEN_PROJECTBASEDIR/$wrapperJar"
  fi
  rm -rf -- "$TMP_DOWNLOAD_DIR"
fi
# End of extension

exec "$JAVA_CMD" \
  ${MAVEN_OPTS-} \
  ${MAVEN_DEBUG_OPTS-} \
  -classpath "$(native_path "$MAVEN_PROJECTBASEDIR/$wrapperJar")" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  $wrapperMainClass \
  ${MAVEN_CONFIG-} \
  "$@"

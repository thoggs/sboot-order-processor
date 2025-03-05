#FROM oraclelinux:9 AS base
#
#RUN set -eux; \
#    dnf install -y tar git wget unzip
#
#ARG JAVA_VERSION=21
#ENV JAVA_HOME=/usr/java/jdk-$JAVA_VERSION
#ENV PATH=$JAVA_HOME/bin:$PATH
#
#RUN set -eux; \
#    ARCH="$(uname -m)" && \
#    if [ "$ARCH" = "x86_64" ]; then ARCH="x64"; fi && \
#    JAVA_PKG="https://download.oracle.com/java/${JAVA_VERSION}/latest/jdk-${JAVA_VERSION}_linux-${ARCH}_bin.tar.gz"; \
#    JAVA_SHA256=$(curl -sSL "$JAVA_PKG.sha256") && \
#    curl -o /tmp/jdk.tgz -sSL "$JAVA_PKG" && \
#    echo "$JAVA_SHA256  /tmp/jdk.tgz" | sha256sum -c - && \
#    mkdir -p "$JAVA_HOME" && \
#    tar --extract --file /tmp/jdk.tgz --directory "$JAVA_HOME" --strip-components 1
#
#ENV LANG=en_US.UTF-8
#
#FROM base
#ARG JAR_FILE=build/libs/app.jar
#COPY ${JAR_FILE} /app/app.jar
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]


FROM oraclelinux:9 AS base

ARG JAVA_VERSION=21
ENV JAVA_HOME=/usr/java/jdk-$JAVA_VERSION
ENV PATH=$JAVA_HOME/bin:$PATH
ENV LANG=en_US.UTF-8

# Combine tudo em um único RUN para menos camadas
RUN set -eux; \
    dnf update -y && \
    dnf install -y tar wget unzip && \
    ARCH="$(uname -m)" && \
    if [ "$ARCH" = "x86_64" ]; then ARCH="x64"; fi && \
    JAVA_PKG="https://download.oracle.com/java/${JAVA_VERSION}/latest/jdk-${JAVA_VERSION}_linux-${ARCH}_bin.tar.gz"; \
    JAVA_SHA256=$(curl -sSL "$JAVA_PKG.sha256"); \
    curl -o /tmp/jdk.tgz -sSL "$JAVA_PKG"; \
    echo "$JAVA_SHA256  /tmp/jdk.tgz" | sha256sum -c -; \
    mkdir -p "$JAVA_HOME"; \
    tar --extract --file /tmp/jdk.tgz --directory "$JAVA_HOME" --strip-components 1; \
    rm /tmp/jdk.tgz; \
    # Remove pacotes desnecessários em runtime
    dnf remove -y tar wget unzip && \
    dnf clean all

FROM base
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
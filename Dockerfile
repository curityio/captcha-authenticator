FROM maven:3.9.0-eclipse-temurin-11 AS maven

FROM azul/zulu-openjdk:17.0.6

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "/root/.m2"

COPY --from=maven ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

COPY src/ ./plugin/src/
COPY pom.xml ./plugin/pom.xml

WORKDIR /plugin/

CMD ["mvn", "clean", "package"]

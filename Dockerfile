FROM azul/zulu-openjdk-alpine:21-jre-headless
RUN mkdir /apps
COPY build/libs/vs-currency-converter-*.jar /apps/currency-converter.jar
ENTRYPOINT ["java", "-jar", "/apps/currency-converter.jar"]
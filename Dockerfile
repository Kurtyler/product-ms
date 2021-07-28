FROM openjdk:8-jdk-alpine
MAINTAINER sarmientojohnkurt@gmail.com
COPY target/product-0.0.1-SNAPSHOT.jar product-ms.jar
ENTRYPOINT ["java","-jar","/product-ms.jar"]
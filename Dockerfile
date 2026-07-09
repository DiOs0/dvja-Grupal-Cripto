FROM eclipse-temurin:8-jdk
MAINTAINER Abhisek Datta <abhisek@appsecco.com>

RUN apt-get update
RUN apt-get install -y default-mysql-client
RUN apt-get install -y maven

WORKDIR /app
COPY pom.xml pom.xml
RUN mvn dependency:resolve

COPY . .
RUN mvn clean package
RUN sed -i 's/\r$//' /app/scripts/start.sh
RUN chmod +x /app/scripts/start.sh

EXPOSE 8080
CMD ["sh", "/app/scripts/start.sh"]

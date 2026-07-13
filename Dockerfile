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
# 1. Crear un grupo y un usuario del sistema que no sea root
#RUN groupadd -r appgroup && useradd -r -g appgroup appuser
RUN groupadd -r appgroup && useradd -m -r -g appgroup appuser
# 2. Darle propiedad a ese usuario SOBRE la carpeta donde corre el servidor
# Si el proyecto corre desde otra carpeta como /app:
RUN chown -R appuser:appgroup /app

# 3. Cambiar al usuario recién creado
USER appuser
CMD ["sh", "/app/scripts/start.sh"]

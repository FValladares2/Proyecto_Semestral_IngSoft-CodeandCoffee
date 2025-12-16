#usamos java 21
FROM eclipse-temurin:21-jdk-alpine

# intalamos las dependencias necesarias
RUN apk update && apk add --no-cache mariadb-client

WORKDIR /app

# copiar el JAR compilado
COPY build/libs/*.jar app.jar

#documentamos el puerto que usara la app
EXPOSE 8085


# ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]
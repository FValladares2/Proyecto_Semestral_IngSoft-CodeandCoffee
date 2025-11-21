# imagen base de java 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app


# se copia el archivo jar generado en la carpeta build/libs del proyecto al contenedor y se renombra como app.jar
COPY build/libs/*.jar app.jar

# usamos el puerto 8085 para el contenedor
EXPOSE 8085

# comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]

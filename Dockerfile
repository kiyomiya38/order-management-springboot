FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/attendance-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

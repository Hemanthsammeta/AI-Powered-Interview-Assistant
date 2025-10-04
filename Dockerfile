# ---- build stage ----
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app

# copy only what is needed for Maven to download dependencies faster
COPY pom.xml .
# if you have a multi-module project or parent poms, copy them too
# COPY settings.xml .  (if you use a custom settings.xml)

RUN mvn -B -q dependency:go-offline

# copy source and package
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:22-jre
WORKDIR /app

# copy the executable jar produced in build stage
COPY --from=build /app/target/*.jar app.jar

# expose the port your Spring Boot app uses
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

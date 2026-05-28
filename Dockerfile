# ---- Stage 1: build the fat jar with Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src

# Cache dependencies first for faster rebuilds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Stage 2: minimal runtime image ----
# Using JRE 17 (debian-slim) instead of alpine: musl libc + Log4j2's StackWalker
# can fail to identify the caller class for LogManager.getLogger() inside shaded jars.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the shaded fat jar produced by maven-shade-plugin
COPY --from=build /src/target/BattleshipGamePlayer-2.0.jar /app/app.jar

# SQLite DBs the game creates/uses at runtime live in /app
# (DatabaseManager creates them in the CWD if missing)

# pom's shade Main-Class is wrong (pt.iscte-iul.istar.battleship.Main),
# so we run with -cp + the real package: battleship.Main
ENTRYPOINT ["java","-cp","/app/app.jar","battleship.Main"]

############################################################
# Stage 1: Build WAR на Maven + Eclipse Temurin 21 (Alpine) #
############################################################
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# 1) Копируем pom.xml и все исходники
COPY pom.xml .
COPY src ./src

# 2) Собираем проект в WAR, полностью пропуская тесты
#    -Dmaven.test.skip=true отключает фазы компиляции/запуска тестов
RUN mvn clean package -Dmaven.test.skip=true

############################################################
# Stage 2: Runtime – Tomcat с JDK 21 (Temurin)             #
############################################################
FROM tomcat:jdk21-temurin

# Удаляем дефолтные webapps, чтобы развернуть только наш WAR
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем собранный *.war из builder-стадии в Tomcat как ROOT.war
COPY --from=builder /workspace/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Запуск Tomcat
CMD ["catalina.sh", "run"]

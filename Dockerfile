FROM gradle:jdk17-alpine AS build
WORKDIR /app
COPY settings.gradle settings.gradle
COPY shared shared
COPY tradingEngineService tradingEngineService
COPY webService webService
RUN gradle bootJar --no-daemon

FROM openjdk:17-alpine as tradingEngineService
WORKDIR /
COPY --from=build /app/tradingEngineService/build/libs/tradingEngineService.jar /app/app.jar
ENTRYPOINT ["java", "-jar","/app/app.jar"]

FROM openjdk:17-alpine as webService
EXPOSE 80
COPY --from=build /app/webService/build/libs/webService.jar /app/app.jar
ENTRYPOINT ["java", "-jar","/app/app.jar"]
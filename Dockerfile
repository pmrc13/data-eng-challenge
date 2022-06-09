FROM gradle:6.2.1-jdk11 as base

WORKDIR /app

COPY build.gradle gradle.properties settings.gradle /app/

RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY ./ /app/

FROM base as builder

RUN gradle clean build --no-daemon

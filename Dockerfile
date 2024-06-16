FROM maven:3-eclipse-temurin-21 as jbuilder

WORKDIR /madoi-core
COPY ./madoi-core ./
RUN --mount=type=cache,target=/root/.m2 \
  mvn install -DskipTests=true
WORKDIR /madoi-volatileserver
COPY ./madoi-volatileserver ./
RUN --mount=type=cache,target=/root/.m2 \
  mvn package -DskipTests=true


FROM node:22 as nbuilder

WORKDIR /madoi-client-ts-js
COPY ./madoi-client-ts-js ./
RUN \
  --mount=type=cache,target=/root/.npm \
  npm ci && npm run build


FROM eclipse-temurin:21-jre
WORKDIR /work
COPY --from=jbuilder /madoi-volatileserver/target/madoi-volatileserver-0.0.1-SNAPSHOT.jar ./madoi-volatileserver.jar 
COPY --from=jbuilder /madoi-volatileserver/webapp ./webapp
COPY --from=nbuilder /madoi-client-ts-js/dist ./webapp/js

CMD java -jar madoi-volatileserver.jar

#!/bin/bash

pushd ../madoi-core
mvn clean
mvn install -DskipTests=true
popd

pushd ../madoi-client-ts-js
npm ci
npm run build
popd
cp ../madoi-client-ts-js/dist/madoi.js ./webapp/js/
cp ../madoi-client-ts-js/dist/madoi.d.ts ./webapp/js/

mvn clean package -DskipTests=true

#!/bin/bash

pushd ../madoi-core
mvn clean
mvn install -DskipTests=true
popd

pushd ../../madoi-client-ts-js
npm i
npm run build
popd
cp ../../madoi-client-ts-js/dist/madoi.umd.js ./webapp/js/madoi.js
cp ../../madoi-client-ts-js/types/madoi.d.ts ./webapp/js/

mvn clean package -DskipTests=true

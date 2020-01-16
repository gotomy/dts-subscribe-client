#!/bin/bash
exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar

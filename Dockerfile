FROM openjdk:11
VOLUME [ "/tmp" ]
COPY target/app.jar /app.jar
COPY start.sh /start.sh
EXPOSE 8000
CMD /start.sh

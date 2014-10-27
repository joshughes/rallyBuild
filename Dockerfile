FROM java:7

WORKDIR /usr/src/myapp

RUN apt-get update
RUN apt-get install -y maven

CMD ["mvn", "clean", "install"]
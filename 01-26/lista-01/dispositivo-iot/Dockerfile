FROM gradle:8.10-jdk21

WORKDIR /app

COPY . .

RUN gradle installDist --no-daemon

# ENTRYPOINT ["./build/install/dispositivo-iot/bin/dispositivo-iot"]
ENTRYPOINT ["./app/build/install/app/bin/app"]
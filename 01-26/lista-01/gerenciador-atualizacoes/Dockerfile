FROM gradle:8.10-jdk21

WORKDIR /app

COPY . .

RUN gradle installDist --no-daemon

#ENTRYPOINT ["./build/install/gerenciador-atualizacoes/bin/gerenciador-atualizacoes"]
ENTRYPOINT ["./app/build/install/app/bin/app"]
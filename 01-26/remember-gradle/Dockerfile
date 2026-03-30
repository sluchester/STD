# Imagem para compilar aplicação java /gradle
FROM gradle:9.4.0-jdk25-alpine AS build

WORKDIR /codigo

COPY aplicacao /codigo/

RUN gradle installDist

# Imagem com a minha aplicação Java para ser executada
FROM eclipse-temurin:25-jre-alpine

WORKDIR /aplicacao

COPY --from=build /codigo/app/build/install/app /aplicacao/

CMD [ "./bin/app" ]
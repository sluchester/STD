FROM ubuntu:24.04

# instalar o aplicativo figlet
# RUN apt-get update && apt-get install -y figlet

RUN apt-get update && apt-get install -y figlet && rm -rf /var/lib/apt/lists/*

# definindo diretório padrão
WORKDIR /app

# copiar o arquivo mensagem.txt para dentro da imagem
COPY mensagem.txt /app/
COPY entrypoint.sh /app/

RUN chmod +x entrypoint.sh

# qualquer alteração executar: > docker build -t minha-imagem .
# executar (um shell script) para a aplicação
# CMD ["figlet","<","mensagem.txt"]
CMD ["/app/entrypoint.sh"]
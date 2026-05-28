# Prática com contêineres e sockets TCP/IP em Java

## Primeiro Teste TCP

Primeiramente foi testado com uma conexão simples, em terminais locais, para verificar o funcionamento do tcp.

Descrever mais

AQUI VAI A IMAGEM DO PRIMEIRO TESTE TCP

## Segundo Teste - Multicast / TCP

Logo após verificar a que o dispositivo e o gerenciador estavam se comunicando via TCP, foi acrescentada uma seção para a comunicação UDP multicast, conforme requisitado na descrição do projeto.

Falar mais sobre...

AQUI VAI A IMAGEM DO PRIMEIRO TESTE MULTICAST TCP

### Diferenciação por versão obsoleta recebida

Como foi mencionado em sala, e debatido entre os alunos e o professor, caso o dispositivo receba uma atualização de versão com um número anterior, ele não deveria ser atualizado. Não faz sentido. Logo, foi feito. TAL TAL TAL TAL TAL TAL....

AQUI VAI A IMAGEM DO PRIMEIRO TESTE MULTICAST TCP COM DIFERENCIAÇÃO DE VERSÃO


### Comandos utilizados
docker compose build
docker compose build --no-cache
docker compose up -d dispositivo1 dispositivo2 dispositivo3
docker compose run --rm --service-ports gerenciador
docker compose logs -f dispositivo1 dispositivo2 dispositivo3 (em outro terminal, para pegar os logs dos dispositivos)

docker compose stop dispositivo2 (para parar um só dispositivo)
docker compose stop (para parar )
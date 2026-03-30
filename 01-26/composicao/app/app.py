import redis
from flask import Flask

# padrão é esse. Sempre usado como referência
app = Flask(__name__)

# é colocado como padrão o IP que está no container database, é pego de 1
# a porta é a padrão do regis
db = redis.Redis(host='database', port=6379) 

def incrementar_contador():
    try:
        return db.incr('contador') # se já existir, acrescentar. Se não, ele cria
    except redis.exceptions.ConnectionError:
        return -1

@app.route('/') # quando alguém acessar essa pasta, ela irá executar essa função
def inicial():
    contador = incrementar_contador()
    return f'Valor do contador: {contador}. \n'

# acessar o diretório da pasta composicao e digitar "docker compose up -d"
# docker ps -> para listar os containers existentes
# docker compose stop -> para parar o processo do container

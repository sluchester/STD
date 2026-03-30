FROM python:3.14-slim

WORKDIR /app

RUN pip install --no-cache-dir flask redis

ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0

CMD ["flask","run"]
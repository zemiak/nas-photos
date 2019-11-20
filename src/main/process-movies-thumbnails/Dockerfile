FROM alpine

RUN apk add --no-cache curl

VOLUME /data

RUN curl -L https://github.com/zemiak/nasphotos/releases/download/mtn-static/mtn.bz2 >/usr/local/bin/mtn.bz2 && \
    bunzip2 /usr/local/bin/mtn.bz2

COPY process.sh /usr/local/bin/
COPY Roboto-Regular.ttf /opt/

RUN chmod +x /usr/local/bin/*

CMD "/usr/local/bin/process.sh"

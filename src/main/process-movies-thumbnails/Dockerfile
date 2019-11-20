FROM alpine

RUN apk add --no-cache curl

VOLUME /data

RUN curl -L https://github.com/zemiak/nasphotos/releases/download/mtn-static/mtn.bz2 >/usr/local/bin/mtn.bz2 && \
    bunzip2 /usr/local/bin/mtn.bz2 && \
    chmod +x /usr/local/bin/*

COPY process.sh /usr/local/bin/

CMD "sh /usr/local/bin/process.sh"

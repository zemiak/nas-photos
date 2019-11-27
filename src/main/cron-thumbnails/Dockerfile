FROM nasphotos

RUN apk add --no-cache curl

VOLUME /data

COPY process.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/*

ENTRYPOINT [ "sh", "-c", "/usr/local/bin/process.sh" ]

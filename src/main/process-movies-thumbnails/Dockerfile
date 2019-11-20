FROM alpine

RUN apk add --no-cache curl unrar && \
    cd /tmp && \
    curl -L https://github.com/Revan654/movie-thumbnailer-mtn/releases/download/2.0.1/mtn-Linux.rar >mtn-Linux.rar && \
    unrar x mtn-Linux.rar && \
    mkdir -p /usr/local/bin/ && \
    mv mtn /usr/local/bin/ && \
    chmod +x /usr/local/bin/mtn

VOLUME /data
COPY process.sh /usr/local/bin/

CMD "sh /usr/local/bin/process.sh"

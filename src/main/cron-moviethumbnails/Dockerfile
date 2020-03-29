FROM balenalib/rpi-alpine

RUN apk add --no-cache curl

VOLUME /data

RUN curl -L https://github.com/zemiak/nasphotos/releases/download/mtn-static/ffmpeg-rpi.bz2 | bunzip2 -c >/usr/local/bin/ffmpeg
COPY process.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/*

CMD "/usr/local/bin/process.sh"

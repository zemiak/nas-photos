FROM alpine

VOLUME /data
COPY process.sh mtn.bz2 /usr/local/bin/
RUN bunzip2 /usr/local/bin/mtn.bz2 && chmod +x /usr/local/bin/*

CMD "sh /usr/local/bin/process.sh"

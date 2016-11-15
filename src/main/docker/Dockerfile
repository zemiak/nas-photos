FROM zemiak/payara
MAINTAINER zemiak@gmail.com

RUN echo deb http://www.deb-multimedia.org jessie main non-free >>/etc/apt/sources.list
RUN apt-get update && apt-get -yq --force-yes install ffmpegthumbnailer mediainfo imagemagick

RUN mkdir -p /pictures
RUN mkdir -p /cache
RUN mkdir -p /opt/watermarks

VOLUME ["/pictures", "/cache"]

COPY *.png /opt/watermarks/
COPY nasphotos.war $DEPLOYMENT_DIR
COPY start.sh /usr/local/bin/

CMD sh /usr/local/bin/start.sh

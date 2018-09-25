FROM payara/server-full:5.183
MAINTAINER zemiak@gmail.com

USER root
RUN echo deb http://www.deb-multimedia.org jessie main non-free >>/etc/apt/sources.list && \
    apt-get update && apt-get -yq --force-yes install ffmpegthumbnailer mediainfo imagemagick && \
    mkdir -p /pictures /cache /opt/watermarks

VOLUME ["/pictures", "/cache"]

COPY lib/* /opt/payara5/glassfish/domains/domain1/lib/
COPY *.png /opt/watermarks/
COPY nasphotos.war $DEPLOY_DIR

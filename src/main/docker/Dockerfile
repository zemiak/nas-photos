FROM payara/server-full:5.183
MAINTAINER zemiak@gmail.com

USER root
RUN mkdir -p /pictures /cache /opt/watermarks

VOLUME ["/pictures", "/cache"]

COPY lib/* /opt/payara5/glassfish/domains/domain1/lib/
COPY *.png /opt/watermarks/
COPY nasphotos.war $DEPLOY_DIR

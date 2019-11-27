#!/bin/sh

svc=nasphotos

which systemctl
if [ $? -eq 0 ]
then
    cp ./${svc}.service /etc/systemd/system/ || exit 10
    systemctl daemon-reload || exit 20
    systemctl enable ${svc}.service || exit 30
    systemctl stop ${svc}.service
    systemctl start ${svc}.service || exit 40
else
    cp ./${svc}.init /etc/init.d/${svc} || exit 10
    chmod +x /etc/init.d/${svc}
    /sbin/chkconfig --add ${svc} || exit 20
    /sbin/service ${svc} stop
    /sbin/service ${svc} start || exit 30
fi

cp ../cron-moviethumbnails/nasphotos-moviethumbnails /etc/cron.daily/ || exit 40
chmod +x /etc/cron.daily/nasphotos-moviethumbnails

cp ../cron-thumbnails/nasphotos-thumbnails /etc/cron.daily/ || exit 40
chmod +x /etc/cron.daily/nasphotos-thumbnails

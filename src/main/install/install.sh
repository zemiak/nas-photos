#!/bin/sh

which systemctl
if [ $? -eq 0 ]
then
    svc=nasphotos.service
    cp ./${svc} /etc/systemd/system/ || exit 10
    systemctl daemon-reload || exit 20
    systemctl enable ${svc} || exit 30
    systemctl stop ${svc}
    systemctl start ${svc} || exit 40
else
    svc=nasphotos
    cp ./${svc}.init /etc/init.d/${svc} || exit 10
    chmod +x /etc/init.d/${svc}
    /sbin/chkconfig --add ${svc} || exit 20
    /sbin/service ${svc} stop
    /sbin/service ${svc} start || exit 30
fi

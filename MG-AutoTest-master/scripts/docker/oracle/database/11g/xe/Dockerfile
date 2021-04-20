FROM ubuntu:18.04

COPY scripts/docker/oracle/database/11g/xe/assets /assets
RUN chmod +x /assets/setup.sh
RUN /assets/setup.sh

COPY scripts/docker/oracle/database/11g/xe/tnsnames.ora /u01/app/oracle/product/11.2.0/xe/network/admin/
RUN mkdir -p /var/oracle_backupfolder

EXPOSE 22
EXPOSE 1521
EXPOSE 8080

CMD /usr/sbin/startup.sh && tail -f /dev/null
FROM ubuntu:20.10

#para responder automanticamete a las preguntas de Y/N de la instalacion de nodejs
ENV DEBIAN_FRONTEND noninteractive

#exposicion del puerto de posrgres
EXPOSE 5432

#install node-js
RUN apt update
RUN apt install -y nodejs
RUN apt install -y npm

#verifying node
RUN node -v
RUN npm -v

#install postgres
RUN apt update
RUN apt install -y postgresql postgresql-contrib

#cambiarse al usuario
USER postgres

#config postgres server
RUN    /etc/init.d/postgresql start &&\
    psql --command "ALTER USER postgres PASSWORD 'postgres';" 
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/12/main/postgresql.conf

#comando a ejecutar cuando se corre la imagen como un contenedor
CMD ["/usr/lib/postgresql/12/bin/postgres", "-D", "/var/lib/postgresql/12/main", "-c", "config_file=/etc/postgresql/12/main/postgresql.conf"]
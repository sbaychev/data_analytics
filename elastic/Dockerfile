#vesrion  0.0.1

FROM docker.elastic.co/elasticsearch/elasticsearch:5.6.6

MAINTAINER "Stefan Baychev - sbaychev@yahoo.com"

COPY --chown=elasticsearch:elasticsearch elasticsearch.yml /usr/share/elasticsearch/config/
COPY --chown=elasticsearch:elasticsearch logging.yml /usr/share/elasticsearch/config/

EXPOSE 9200 9300

docker stop es
docker rm es
docker rmi sb/elasticsearch

docker build -f Dockerfile -t sb/elasticsearch .
docker volume create --name esdata

docker create --name es -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" --log-opt max-size=50m --log-opt max-file=10 -v esdata:/usr/share/elasticsearch/data -v esdata:/usr/share/elasticsearch/logs sb/elasticsearch

docker start es

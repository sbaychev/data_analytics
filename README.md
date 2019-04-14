# Data Analytics #

What does this do for you?
-
1. Runs via a .bat `windows_start_es` or .sh `unix_start_es` script file an ElasticSearch version 5.6.6 instance via Docker using a Dockerfile and cmd arguments.
The instance runs as single node "cluster", disabled X-Pack security,`esdata` named volume that has the data and logs in pre-defined well known locations

2. The java defined SparkDriver then uses the ongoing ElasticSearch instance.
The SparkDriver defines the needed configuration settings to run itself, communicate to ElasticSearch and to handle
 the data processing of the `XXX.xxxx`.
Once the XXXX file gets processed, we send it to the well known ElasticSearch instance.

What else is there?
-

--- Create Index Pattern in Kibana

- createXXXXXIndex.sh    - does what it says, creates the stations index, with its schema - no data
- deleteXXXXXIndex.sh    - does what it says, deletes the stations index, with its schema and data - similar to SQL DROP Table
- elasticsearch.yml         - the elastic search configuration file
- logging.yml               - the elastic search logging configuration file
- Dockerfile                - the Docker image file descriptor

Good to know?
-
- the SparkDriver has some code documentation and depending on it certain workings can be tweaked
- curl usage is needed, if not having it, you need to! Thank me later, it is a must have tool https://curl.haxx.se/h2c/
- [TODO] Maven Wrapper used, so no need to setup any Maven, paths and what not. Maven gets wrapped within the project.
- Java
- ElasticSearch
- Spark | Hadoop
- Docker

Sample Walk Through?
-

docker-compose up
login to grafana (admin:admin)
setup data source: http://<elastic-container-ip>:9200
have to use elasticsearch container IP, as the hostname won't work
find IP with docker inspect grafanaelasticsearch_elasticsearch_1 | grep IPAddress

1. Execute `windows_start_es.bat` or `unix_start_es.sh`
2. [TODO] Execute script file xyz or wtp and the SparkDriver gets to run.. 
!! For now via the IDE run the SparkDriver and all the magic would happen !!

Et Voaila!

Use the below given and would see some nice json command line readable `XXXXX` !!

`curl http://localhost:9200/xxxxx/_search | json_pp`

# Data Analytics #

What does this do for you?
-
1. Runs via a .bat `windows_start_es_kibana` or .sh `unix_start_es_kibana` script file an ElasticSearch version 6.6.2
instance via
Docker using a Dockerfile and cmd arguments.
The instance runs as single node "cluster", disabled X-Pack security,`esdata` named volume that has the data and logs in pre-defined well known locations

2. The java defined SparkDriver then uses the ongoing ElasticSearch instance.
The SparkDriver defines the needed configuration settings to run itself, communicate to ElasticSearch and to handle
 the data processing of the `XXX.xxxx`.
Once the XXXX file gets processed, we send it to the well known ElasticSearch instance.

What else is there?
-

npm install http-server -g * Future Development


--- Create Index Pattern in Kibana

- createXXXXXIndex.sh                   - does what it says, creates the stations index, with its schema - no data
- deleteXXXXXIndex.sh                   - does what it says, deletes the stations index, with its schema and data - similar to SQL DROP Table
- elasticsearch.yml                     - the elastic search configuration file
- logging.yml                           - the elastic search logging configuration file
- Dockerfile | docker-compose.yml       - the Docker image file descriptor

Good to know?
-

- the SparkDriver has some code documentation and depending on it certain workings can be tweaked
- curl usage is needed, if not having it, you need to! Thank me later, it is a must have tool https://curl.haxx.se/h2c/
- [Future Development] Maven Wrapper used, so no need to setup any Maven, paths and what not. Maven gets wrapped within
the project.
- Java
- ElasticSearch
- Spark | Hadoop
- Docker

Sample Walk Through?
-

1. Execute the Docker based script files unix_start_es_kibana or windows_start_es_kibana
    ~ perform visual inspection if correctly running from within their well known application localhost:9300 and localhost:5360 addresses. If all good, proceed, else investigate and correct.
2. Execute the mapping | scalability script createStationsIndex
    ~ perform visual inspection if correctly executed via the http://localhost:9200/_cat/indices/ or/ and from the Kibana GUI. If all good, proceed, else investigate and correct either via the deleteStationsIndex script or other means.
3. Execute the Data Ingestion and Processing via the pre-build data-analytics-1.0.0-SNAPSHOT-jar-with-dependencies
.jar or via the IDE
    ~ perform inspection of end result running, first via Data Store http://localhost:9200/_cat/indices/ or/ and from the Kibana GUI. If all good, proceed, else investigate and correct. If need be corrections to be performed, first execute the deleteStationsIndex script and proceed to the beginning of step 2.
4. Use the Visual Analytics Platform - Kibana

Et Voaila!

Use the below given and would see some nice json command line readable `XXXXX` | replace with index!!

`curl http://localhost:9200/xxxxx/_search | json_pp`

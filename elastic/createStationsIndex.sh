#!/bin/bash

npm install http-server -g

curl -X PUT localhost:9200/stations/ -d '{"settings" : {"index" : {"number_of_shards" : 3, "number_of_replicas" : 2 }}, "mappings" : {"day-readings" : {"properties" : {"time" : {"type" : "date"}, "geohash" : {"type" : "geo_point"}, "title" : {"type" : "text"}, "geohash" : {"type" : "geo_point"}, "P1" : {"type" : "integer"}, "P2" : {"type" : "integer"}, "temperature" : {"type" : "integer"}, "humidity" : {"type" : "integer"}, "pressure" : {"type" : "integer"} } } } }' > result.json
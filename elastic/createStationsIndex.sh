#!/bin/bash

curl -X PUT "localhost:9200/stations-year-reading/" -H 'Content-Type: application/json' -d '{"settings":{"index":{"number_of_shards":3,"number_of_replicas":2}},"mappings":{"year-readings":{"properties":{"time":{"type":"date"},"geopoint":{"type":"geo_point"},"humidity":{"type":"long"},"p1":{"type":"long"},"p2":{"type":"long"},"pressure":{"type":"long"},"temperature":{"type":"long"}}}}}' > resultPutYearIndex.json

#curl -X PUT "localhost:9200/stations-day-reading/" -H 'Content-Type: application/json' -d
# '{"settings":{"index":{"number_of_shards":3,"number_of_replicas":2}},"mappings":{"day-readings":{"properties":{"time":{"type":"date"},"geopoint":{"type":"geo_point"},"humidity":{"type":"long"},"p1":{"type":"long"},"p2":{"type":"long"},"pressure":{"type":"long"},"temperature":{"type":"long"}}}}}' > resultPutDayIndex.json

#!/bin/bash

docker volume rm elastic_esdata
curl -XDELETE 'localhost:9200/stations' > result.json

#!/bin/bash

#docker volume rm elastic_esdata
#curl -X DELETE 'localhost:9200/stations-day-reading' > resultDeleteDayReading.json
curl -X DELETE 'localhost:9200/stations-year-reading' > resultDeleteYearReading.json
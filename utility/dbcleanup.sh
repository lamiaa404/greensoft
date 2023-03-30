#!/bin/bash
date

if [ -z "$1" ]; then
  #DEL_OLDER_THAN="$(date --date 'today 00:00' +%s)"
  DEL_OLDER_THAN="$(date --date "-2 weeks 00:00" +%s)"
else
  DEL_OLDER_THAN="$1"
fi
# läuft an tag X um 00:30 uhr und löscht einträge aus mqtt_data zu die alt sind
psql postgresql://swtp:K7NLXpqokHaNATgW@localhost:5432/greensoft -c \
  "DELETE FROM mqtt_data WHERE timestamp < $DEL_OLDER_THAN;"

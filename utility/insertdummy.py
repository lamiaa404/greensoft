# usage: python3 insertdummy.py <pfad zur generierten json datei>
# auf dem server laufen lassen, sonst dauert das zu lang !
import psycopg2 # postgres db driver for python
import json
import sys

conn = psycopg2.connect(
    host="192.168.34.158",
    database="greensoft",
    user="swtp",
    password="K7NLXpqokHaNATgW"
)

cur = conn.cursor()

counter = 0
with open(sys.argv[1]) as file:
    j = json.load(file)
    for entry in j:
        cur.execute("""INSERT INTO dummydata (timestamp, id, name, power, standort, users, tags)
        VALUES (%s, %s, %s, %s, %s, %s, %s);
        """, (entry["timestamp"], entry["id"], entry["name"], entry["power"], entry["standort"], entry["users"], entry["tags"]))
        if counter % 50 == 0:
            # alle 50 INSERTs commiten, also speichern
            conn.commit()

# das script dauert ein wenig, da es ja 700k einträge INSERTet
# row anzahl checken: SELECT count(*) AS exact_count FROM dummydata;
conn.commit()
conn.close()

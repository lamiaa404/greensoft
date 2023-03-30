# usage: python3 gendummy.py <pfad zur data.csv>
# in der data.csv muss man noch eine zeile davorsetzen:
# "topic,data" (ohne ")
# nimmt die data.csv aus dem moodle und generiert daraus ein json wie in issue 26 beschrieben:
# https://git.informatik.uni-leipzig.de/SWS/lehre/ws-2022-2023/swt-p/swtp-2022-11/-/issues/26
#{
#id: zahl,
#name: string,
#power: zahl,
#standort: string,
#users: zahl,
#tags: array
#}
import csv
import json
import sys
import re
import random

counter = 0
l = list()
id_attr = dict()

with open(sys.argv[1]) as file:
    reader = csv.DictReader(file)
    for row in reader:
        m_id = re.search("edison/(.*?)/active_power_calculated", row["topic"]).group(1)
        if not m_id in id_attr:
            id_attr[m_id] = dict()
            id_attr[m_id]["name"] = "computer"+str(counter)
            id_attr[m_id]["standort"] = "standort"+str(counter)
            id_attr[m_id]["users"] = random.randint(1, 25)
            id_attr[m_id]["tags"] = ["tag"+str(counter), "tag"+str(counter+100)]
            counter += 1

        out = dict()
        out["timestamp"] = round(json.loads(row["data"])["t"])
        out["id"] = m_id
        out["name"] = id_attr[m_id]["name"]
        out["power"] = round(json.loads(row["data"])["val"], 2)
        out["standort"] = id_attr[m_id]["standort"]
        out["users"] = id_attr[m_id]["users"]
        out["tags"] = id_attr[m_id]["tags"]

        l.append(out)

print(json.dumps(l))

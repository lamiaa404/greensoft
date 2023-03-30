#!/usr/bin/env python3
l = [11,11,11,1,3,17,13,14,15,11,24,7,2,23,23,23,0,1,55,66,23,24,24,22,0,10,0,10]

threshold = 10
duration = 3

seq = list()

def add_to_db(lastseq):
    # das halt nur noch auf die timestamps ummünzen
    print("start", lastseq[0], "end", lastseq[-1])

for index, wert in enumerate(l):
    if wert < threshold or index+1 >= len(l):
        if index+1 >= len(l):
            # letzen wert catchen falls wir auf einem hoch enden
            seq.append(wert)
        if len(seq) >= duration:
            add_to_db(seq)
        seq.clear()
        continue
    seq.append(wert)


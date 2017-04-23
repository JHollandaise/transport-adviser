#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import csv
from convertbng.util import convert_lonlat

input = open('location_data_st_ti.csv', 'r')

convert_dict = {}
n_e_dict = {}

for row in csv.reader(input):
    convert_dict[row[1]]=int(row[0])

input.close()

input = open('RailReferences.csv','r')

for row in csv.reader(input):
    if row[0] in convert_dict:
        n_e_dict[convert_dict[row[0]]] = [int(row[1]),int(row[2])]

input.close()

check_stanox = 78215

print(n_e_dict[check_stanox])
print(convert_lonlat(float(n_e_dict[check_stanox][0]),float(n_e_dict[check_stanox][1])))

lat_lon_dict = {}

for i in n_e_dict:
    lat_lon_dict[i] = (convert_lonlat(float(n_e_dict[i][0]),float(n_e_dict[i][1]))[1][0],\
                        convert_lonlat(float(n_e_dict[i][0]),float(n_e_dict[i][1]))[0][0])


print(lat_lon_dict[check_stanox])

with open('stanox_to_lonlat.json','w') as fp:
    json.dump(lat_lon_dict, fp)

import csv
import fnmatch

input = open('location_data_st_ti.csv', 'rb')
output = open('clean.csv', 'wb')

writer = csv.writer(output)
for row in csv.reader(input):
    if row[0]!=' ' and row[1]!=' ':
        writer.writerow(row)


input.close()
output.close()

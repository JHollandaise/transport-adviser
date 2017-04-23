from bottle import route, run, template
import json

with open('stanox_to_lonlat.json', 'r') as fp:
    data = json.load(fp)



# @route('/<latmin>/<latmax>/<longmin>/<longmax>')
# def index(latmin, latmax, longmin, longmax):
@route('/')
def index():
    return template(json.dumps(data))

run(host='192.168.0.4', port=8080)

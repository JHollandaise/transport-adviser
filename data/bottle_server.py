from bottle import route, run, template
import json

import logging

import stomp

import signal
import sys

NETWORK_RAIL_AUTH = ('jh2044@cam.ac.uk', 'Gandy68!')

class Listener(object):

    def __init__(self, mq):
        self._mq = mq

    def on_error(self, headers, message):
        pass

    def on_heartbeat_timeout(self):
        print "heartbeat failure!!!"
        Make_Conn()

    def on_message(self, headers, message):
        global train_positions

        json_message = json.loads(message)

        for msg in json_message:
            try:
                if msg["header"]["msg_type"]=='0003':
                    train_positions[msg["body"]["train_id"]]=stanox_to_lonlat[msg["body"]["loc_stanox"]]
                    print train_positions[msg["body"]["train_id"]]
                else:
                     print "message type unexpected"
            except:
                print "invalid JSON"

        self._mq.ack(id=headers['message-id'], subscription=headers['subscription'])

def Make_Conn():
    mq = stomp.Connection(host_and_ports=[('datafeeds.networkrail.co.uk', 61618)],
                          keepalive=True,
                          vhost='datafeeds.networkrail.co.uk',
                          heartbeats=(10000, 5000))

    lst = Listener(mq)
    mq.set_listener('', lst)

    mq.start()
    mq.connect(username=NETWORK_RAIL_AUTH[0],
               passcode=NETWORK_RAIL_AUTH[1],
               wait=True)

    mq.subscribe('/topic/TRAIN_MVT_ALL_TOC', 'test-vstp', ack='client-individual')

def getTrains(latmin, latmax, longmin, longmax):
    trains_to_return = {}

    if longmax == "all":
        for train in train_positions:
            trains_to_return[train] = train_positions[train]

        return trains_to_return

    else:

        latmin = float(latmin)
        latmax = float(latmax)
        longmin = float(longmin)
        longmax = float(longmax)

        for train in  train_positions:
            if train_positions[train][0] > latmin and train_positions[train][0] < latmax\
                and train_positions[train][1] > longmin and train_positions[train][1] < longmax:

                trains_to_return[train] = train_positions[train]

        return trains_to_return

train_positions = {}

with open('trains_cache.json', 'r') as fp:
    train_positions = json.load(fp)

Make_Conn()



with open('stanox_to_lonlat.json', 'r') as fp:
    stanox_to_lonlat = json.load(fp)

@route('/<latmin>/<latmax>/<longmin>/<longmax>')
def index(latmin, latmax, longmin, longmax):

    trains_to_return = json.dumps(getTrains(latmin, latmax, longmin, longmax))

    return template(trains_to_return)

run(host='192.168.0.4', port=8080)

def signal_handler(signal, frame):

        with open('trains_cache.json', 'w') as fp:
            json.dump(train_positions, fp)

        sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)
signal.pause()

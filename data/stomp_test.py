#!/usr/bin/env python

import logging
from time import sleep

import stomp

NETWORK_RAIL_AUTH = ('jh2044@cam.ac.uk', 'Gandy68!')

class Listener(object):

    def __init__(self, mq):
        self._mq = mq

    def on_message(self, headers, message):
        print headers
        print message
        self._mq.ack(id=headers['message-id'], subscription=headers['subscription'])

mq = stomp.Connection(host_and_ports=[('datafeeds.networkrail.co.uk', 61618)],
                      keepalive=True,
                      vhost='datafeeds.networkrail.co.uk',
                      heartbeats=(10000, 5000))

mq.set_listener('', Listener(mq))

mq.start()
mq.connect(username=NETWORK_RAIL_AUTH[0],
           passcode=NETWORK_RAIL_AUTH[1],
           wait=True)

mq.subscribe('/topic/TRAIN_MVT_HL_TOC', 'test-vstp', ack='client-individual')

while mq.is_connected():
    sleep(1)

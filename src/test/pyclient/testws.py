#!/usr/bin/python

from websocket import create_connection

if __name__ == '__main__':
    ws = create_connection("ws://localhost:8081/webcat")
    request = {'path': '/test/hello', 'mid': 1, 'params': {'name': 'demo'}}
    ws.send(str(request))
    print 'Sending ...'
    print 'Reeiving...'
    result = ws.recv()
    print "Received '%s'" % result
    ws.close()

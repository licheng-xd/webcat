#!/usr/bin/python

import requests

if __name__ == '__main__':
    resp = requests.get('http://localhost:8080/test/hello')
    print resp.status_code, resp.content

#!/usr/bin/python

import sys, getopt, time, json, uuid
from cassandra.cluster import Cluster
from urllib import urlopen

def store_to_cassandra(current, entries, session):
    for entry in entries:
        print entry
        session.execute(
            """
            INSERT INTO raw_page_visits (id, timestamp, page_title, page_url, visits, links, read, num_refs, search, social)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """,
            (uuid.uuid1(), current, entry['page_title'], entry['page_url'], entry['visits'], entry['links'], entry['read'], entry['num_refs'], entry['search'], entry['social'])
        )

def poll_chartbeat_api(apikey, host):
    url = urlopen('https://dashapi.chartbeat.com/live/toppages/v3/?all_platforms=1&apikey=' + apikey + '&host=' + host + '&loyalty=1&now_on=1&types=1').read()
    result = json.loads(url)
    entries = []
    for page in result['pages']:
        page_visit_entry = {}
        page_visit_entry['page_title'] = page['title']
        page_visit_entry['page_url'] = page['path']
        page_visit_entry['visits'] = int(page['stats']['visits'])
        page_visit_entry['links'] = int(page['stats']['links'])
        page_visit_entry['read'] = int(page['stats']['read'])
        page_visit_entry['num_refs'] = int(page['stats']['num_refs'])
        page_visit_entry['search'] = int(page['stats']['search'])
        page_visit_entry['social'] = int(page['stats']['social'])
        entries.append(page_visit_entry)
    
    return entries

def start(host, apikey, delay):
    cluster = Cluster()
    session = cluster.connect('chartbeat')
    
    # We need to wait at least 3 seconds before polling again
    if delay < 3:
        delay = 3
    
    create_schema(session)
    
    while True:
        current = int(time.time())*1000
        entries = poll_chartbeat_api(apikey, host)
        store_to_cassandra(current, entries, session)
        time.sleep(float(delay))

def create_schema(session):
    schema = open("chartbeat.cql").read().split(';')
    print "Creating schema..."
    for statement in schema:
        if statement.strip() != '':
            session.execute(statement + ";")
    print "Schema up-to-date."

def main(argv):
    host = ''
    apikey = ''
    delay = 3

    try:
       opts, args = getopt.getopt(argv, "h:a:d:",["host=","apikey=","delay="])
    except getopt.GetoptError:
       print 'chartbeat_poll_api.py -h gizmodo.com -a 317a25eccba186e0f6b558f45214c0e7 -d 30'
       sys.exit(2)

    for opt, arg in opts:
        if opt == '--help':
            print 'test.py -h <host name> -a <api key> -d <delay>'
            sys.exit()
        elif opt in ("-h", "--host"):
            host = arg
        elif opt in ("-a", "--apikey"):
            apikey = arg
        elif opt in ("-d", "--delay"):
            delay = arg

    start(host, apikey, delay)

if __name__ == "__main__":
    main(sys.argv[1:])

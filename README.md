# Basic Chartbeat Metrics with Cassandra and Play

There are two parts to the finished application - part one is the Python script that pulls data from the Chartbeat API into Cassandra for storage. The second part is the web application that queries Cassandra and displays a sorted list of the most popular pages for a given time frame.

### (0) Install Cassandra

Since the application depends on Cassandra as the data store, it will need to be installed first. Instructions here: https://wiki.apache.org/cassandra/GettingStarted 

### (1) Python script

Start by running the Python script, located under ```scripts/chartbeat_poll_api.py```, which will create the schema in Cassandra and populate it with data from Chartbeat: 
```
cd scripts; python chartbeat_poll_api.py -h gizmodo.com -a 317a25eccba186e0f6b558f45214c0e7 -d 30
```
Use -h to specify the host, ```-a``` to specify the API key and ```-d``` to specify the delay. Note that the delay cannot be shorter than 3 seconds because the Chartbeat API doesn't update more often than that. 

### (2) Play application

I chose to use Play/Scala as my framework/language of choice. The only big of configuration necessary should be the Cassandra node, specified in the ```application.conf``` file:
```
cassandra.node="127.0.0.1"
```

I have included the typesafe activator, so running the application should only require:
```
./activator run 9000
```
This will start the application at ```localhost:9000```

To clean & compile:
```
./activator clean compile
```

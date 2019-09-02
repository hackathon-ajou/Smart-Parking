import RPi.GPIO as gpio
import time
import sys
import signal
from firebase import firebase
import urllib2, urllib, httplib
import json
import os
from functools import partial

firebase = firebase.FirebaseApplication('https://smart-parking-master.firebaseio.com/',None)

gpio.setmode(gpio.BCM)

trig1=14
echo1=15

trig2=17
echo2=27

trig3=23
echo3=24

trig4=5
echo4=6

trig5=16
echo5=20

trig6=21
echo6=26

print('start!')

gpio.setwarnings(False)
 
def sensor(number, echo, trig) :
	gpio.setup(trig, gpio.OUT)
	gpio.setup(echo, gpio.IN)

	print('Setting...')

	gpio.output(trig, False)
	time.sleep(0.5)

	gpio.output(trig, True)
	time.sleep(0.00001)
	gpio.output(trig, False)

	while gpio.input(echo) == 0 :
		pulse_start = time.time()

	while gpio.input(echo) == 1 :
		pulse_end = time.time()

	pulse_duration = pulse_end - pulse_start
	distance = pulse_duration * 17000
	distance = round(distance, 2)

	print "US", number, " Distance : ", distance, "cm"
	return distance

try :
	while True :
		dist1 = sensor(1, echo1, trig1)
		dist2 = sensor(2, echo2, trig2)
		dist3 = sensor(3, echo3, trig3)
		dist4 = sensor(4, echo4, trig4)
		dist5 = sensor(5, echo5, trig5)
		dist6 = sensor(6, echo6, trig6)

		data = {'dist1':[1,dist1],
			'dist2':[2,dist2],
			'dist3':[3,dist3],
			'dist4':[4,dist4],
			'dist5':[5,dist5],
			'dist6':[6,dist6]
		}

		#senddata = json.dumps(data)
		firebase.patch('/parking3/',data)
except :
	gpio.cleanup()

import csv
import time
import operator
from collections import defaultdict, namedtuple
from operator import itemgetter
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict
from datetime import datetime
import time

class PlotSpi:
	def __init__(self, logfile):
		self.logfilepath = "logs/" + logfile
	def readCSV(self):
		csv_file = open(self.logfilepath, mode='r')
		data = csv.DictReader(csv_file)
		return data
	def parselog(self, logtype):
		logdata = self.readCSV()
		num_log = len(logtype)
		log_dict = {}
		val_array = []
		for x in range(num_log):
			val_array.append([])
			log_dict[logtype[x]]=x
		flag=1
		for row in logdata:
			if flag:
				initial_time = time.mktime(datetime.strptime(row["timestamp"], "%Y-%m-%d %H:%M:%S").timetuple())
				flag=0
			val_array[log_dict[row["logid"]]].append((time.mktime(datetime.strptime(row["timestamp"], "%Y-%m-%d %H:%M:%S").timetuple()) - initial_time, row["timetaken"]))
		for x in range(num_log):
			plt.figure(x)
			x_val = [p[0] for p in val_array[x]]
			y_val = [float(p[1]) for p in val_array[x]]
			plt.plot(x_val, y_val)
			plt.title(logtype[x])
			plt.xlabel("simulation time")
			plt.ylabel(logtype[x]+"in milliseconds")
			plt.savefig(logtype[x]+"performance")

			plt.figure(x+num_log)
			plt.boxplot(x_val,y_val)
			plt.xlabel("Number of samples")	
			plt.ylabel(logtype[x]+"in milliseconds")
			plt.savefig(logtype[x]+"performance_box", bbox_inches='tight')


def main():
	logfile = "./log.csv"
	inst = PlotSpi(logfile)
	inst_data = inst.parselog(["REGISTER", "SIGN_GS", "SIGN_HY", "VERIFYGM"])

main()

import os

file1=open('movieTree.txt','r')
file=open('Results.txt', 'r')
start=0
op=""
for line in file.readlines():
	if '<synsets' in line:
		start=1;
		op="";
	elif '</synsets>' in line:
		print file1.readline().strip()+op
		start=0;
	elif (start==1):
		line = line.strip();
		word = line.split('.');
		op=op+', '+(word[0].strip())
		

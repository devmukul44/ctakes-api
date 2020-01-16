import requests
import json
import csv


with open('Updated_clinical_notes.csv') as csvfile:
	readCSV = csv.reader(csvfile, delimiter=';')
	file = open('outputNotesData.csv', 'w')
	for row in readCSV:
		try:
			url = 'http://34.203.148.220/api/testing/ctakes'
			headers = {'content-type': 'application/json'}
			payload = {'text': row[0]}
			response = requests.post(url, data=json.dumps(payload), headers=headers)
			# file.write(row[0] + '\n \n \n')
			content = json.loads(response.content)
			print content.keys()
			file.write('Subject;Entity Type;Entity;Standardized Text;Polarity;Value;ICD9CM;ICD10CM;CPT;HCPCS;LOINC;RXNORM')
			file.write('\n')
			for i in content['data']:
				for j in content['schema']:
					if j['name'] in i:
						file.write(i[j['name']]+';')
					else:
						file.write(';')
				file.write('\n')
			file.write('\n')
		except Exception:
			pass
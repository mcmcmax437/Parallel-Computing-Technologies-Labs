import requests

response = requests.post('http://localhost:5000/multiply')
print(response.json())

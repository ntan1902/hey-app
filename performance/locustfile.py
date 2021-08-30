import time
from locust import HttpUser, task, between,TaskSet
import json

import csv


USER_CREDENTIALS = [
    ("an21", "123"),
]


with open('Data_1000.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    line_count = 0
    for row in csv_reader:
        if line_count == 0:
            line_count += 1
        USER_CREDENTIALS.append((row['username'], row['password']))
        line_count += 1



class UserBehaviour(TaskSet):
    def on_start(self):
        if len(USER_CREDENTIALS) > 0:
            user, passw = USER_CREDENTIALS.pop()
            response = self.client.post("/auth/api/v1/users/login", json={"username":user, "password":passw})
            self.client.headers.update({'Authorization': 'Bearer ' + json.loads(response._content)['payload']['accessToken']})

    @task
    def some_task(self):
        self.client.post("/payment/api/v1/me/topup",json={"amount":"1000", "bankId": "e8984aa8-b1a5-4c65-8c5e-036851ec783c"})


class User(HttpUser):
    tasks = [UserBehaviour]
    wait_time = between(1, 2)

import subprocess
import time
import uuid
import numpy as np
import matplotlib.pyplot as plt

#URL = "http://localhost:4444/accounts/signup"
URL = "https://api.lockboxes.ru:8445/accounts/signup"
REQUESTS = 500

total_times = []
dns_times = []
connect_times = []
start_transfer_times = []

def make_request():
    unique_email = f"test_{uuid.uuid4().hex[:8]}_{int(time.time_ns())}@aboba.com"
    cmd = [
        "curl", "-X", "POST", "-H", "Content-Type: application/json", "-d",
        f'{{"password": "aboba123", "password_repeat": "aboba123", "mail": "{unique_email}"}}',
        "-o", "/dev/null", "-s", "-w",
        "%{time_total} %{time_namelookup} %{time_connect} %{time_starttransfer}\n",
        URL
    ]
    result = subprocess.run(cmd, capture_output=True, text=True)
    return map(float, result.stdout.strip().split())

for i in range(REQUESTS):
    total, dns, connect, start_transfer = make_request()
    total_times.append(total)
    dns_times.append(dns)
    connect_times.append(connect)
    start_transfer_times.append(start_transfer)
    print(f"Request #{i+1}: Total Time: {total}s")

# Plot Total Response Time
plt.figure(figsize=(10, 5))
plt.plot(range(1, REQUESTS + 1), total_times, marker='o', linestyle='-', label="Total Time")
plt.xlabel("Request Number")
plt.ylabel("Time (s)")
plt.title("Total Response Time per Request")
plt.legend()
plt.grid(True)
plt.show()

# Plot Breakdown of Timing Stats
plt.figure(figsize=(10, 5))
plt.plot(range(1, REQUESTS + 1), dns_times, marker='o', linestyle='-', label="DNS Time")
plt.plot(range(1, REQUESTS + 1), connect_times, marker='o', linestyle='-', label="Connect Time")
plt.plot(range(1, REQUESTS + 1), start_transfer_times, marker='o', linestyle='-', label="Start Transfer Time")
plt.xlabel("Request Number")
plt.ylabel("Time (s)")
plt.title("Detailed Timing Stats per Request")
plt.legend()
plt.grid(True)
plt.show()

# Compute Averages
categories = ["Total Time", "DNS Time", "Connect Time", "Start Transfer Time"]
averages = [np.mean(total_times), np.mean(dns_times), np.mean(connect_times), np.mean(start_transfer_times)]

# Plot Average Timings
plt.figure(figsize=(8, 5))
plt.bar(categories, averages, color=['blue', 'orange', 'green', 'red'])
plt.xlabel("Metric")
plt.ylabel("Average Time (s)")
plt.title("Average Response Time Metrics")
plt.grid(axis='y')
plt.show()


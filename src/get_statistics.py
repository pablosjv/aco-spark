"""Script to get the statistics needed."""
import json

experiment_model = {
    'iterations': [{
        "iteration_best_score": 0,
        "iteration_best_resources": 0,
        "current_best_score": 0,
        "current_resources_used": 0,
        "iteration_time": 0,
    }
    ],
    'experiment_time': 0,
    'resources_used': 0,
    'iteration_of_best_resources': 0
}

FILES_DIR = 'main/scala/resultsFiles/experimentsResults/'
FILE = FILES_DIR + 'result_parallel_case1.json'

with open(FILE, 'r') as f:
    data = json.load(f)

# mean of resources in each iteration
resources_iteration = []
for i in range(100):
    resources_iteration.append(0)
    for element in data:
        resources_iteration[i] += int(element['iterations'][i]['current_resources_used'])
    resources_iteration[i] = resources_iteration[i]/50
    print(int(resources_iteration[i]))

# get all the best iteration number
for element in data:
    print(element['iteration_of_best_resources'])


# Get the general statistics
mean_number_resources = 0
mean_best_iteration_number = 0
mean_iteration_time = 0

for element in data:
    mean_number_resources += float(element['resources_used'])
    mean_best_iteration_number += float(element['iteration_of_best_resources'])
    for iteration in element['iterations']:
        mean_iteration_time += float(iteration['iteration_time'])

print(mean_iteration_time/5000)
print(mean_number_resources/50)
print(mean_best_iteration_number/50)


# Mean time for executions
time_acumulator = 0

for element in data:
    time_acumulator += int(element['experiment_time'])

print(time_acumulator/50)


# get the general results
min_resources = data[0]
min_resources_cont = 0
max_resources = data[0]
max_resources_cont = 0
min_time = 190000000

for element in data:
    if int(min_resources['resources_used']) > int(element['resources_used']):
        min_resources = element
        cont = 1
    elif int(min_resources['resources_used']) == int(element['resources_used']):
        min_resources_cont += 1
    if int(max_resources['resources_used']) < int(element['resources_used']):
        max_resources = element
        max_resources_cont = 1
    elif int(max_resources['resources_used']) == int(element['resources_used']):
        max_resources_cont += 1
    if min_time > int(element['experiment_time']):
        min_time = int(element['experiment_time'])

print(
    min_resources['experiment_time'] + ' ' + min_resources['resources_used']
    + ' ' + str(min_resources_cont)
)
print(
    max_resources['experiment_time'] + ' ' + max_resources['resources_used']
    + ' ' + str(max_resources_cont)
)
print(min_time)

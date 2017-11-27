"""Scritpt to extract the general stadistics from the results file."""

import json

FILES_DIR = 'main/scala/resultsFiles/'
FILE = FILES_DIR + 'result_seq_3.txt'
FILE_JSON = FILES_DIR + 'experimentsResults/result_sequential_case3.json'

experiment_separator_init = '********'
experiment_separator_end = '********************************'
# iteration_separator = '------------ Iteration number'
# best_solution_separator = '------->>> Best global solution:'
iteration_separator = '------------'
best_solution_separator = '------->>>'
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


experiment_count = 0
iteration_count = 0
in_iteration = False
in_best = False
best_iteration = 500
data = []

with open(FILE, 'r') as f:

    for line in f:
        line_data = line.split()
        if line_data[0] == experiment_separator_init:
            data.append({
                'iterations': [],
                'experiment_time': 0,
                'resources_used': 0,
                'iteration_of_best_resources': 0
            })
            iteration_count = 0
            in_best = False

        elif line_data[0] == iteration_separator:
            data[experiment_count]['iterations'].append({
                "iteration_best_score": 0,
                "iteration_best_resources": 0,
                "current_best_score": 0,
                "current_resources_used": 0,
                "iteration_time": 0,
            })
            in_iteration = True

        elif line_data[0] == 'Iteration':
            # Guardar el iteration score
            data[experiment_count]['iterations'][iteration_count]['iteration_best_score'] = line_data[3]

        elif line_data[0] == 'Current':
            # guardar la mejor puntuacion de la iteracion
            data[experiment_count]['iterations'][iteration_count][
                'current_best_score'] = line_data[3]
            in_iteration = False

        elif line_data[0] == 'RESOURCES':
            # print(str(experiment_count) + ' ' + str(iteration_count))
            if in_best:
                # guardar el numero de recursos globales usados
                data[experiment_count]['resources_used'] = line_data[2]
                # print(str(experiment_count) + ' ' + str(iteration_count))
            elif in_iteration:
                # guardar el numero de recursos usados en la iteracion
                # print(str(experiment_count) + line_data[2])
                data[experiment_count]['iterations'][iteration_count]['iteration_best_resources'] = line_data[2]
            else:
                # print(str(experiment_count) + str(in_best) + line_data[2])
                data[experiment_count]['iterations'][iteration_count]['current_resources_used'] = line_data[2]
                # guardar el mejor numero hasta el momento

        elif line_data[0] == '------->>>':
            in_iteration = False
            in_best = True

        elif line_data[0] == '--':
            # guardar tiempo de la iteracion
            data[experiment_count]['iterations'][iteration_count]['iteration_time'] = line_data[7]
            iteration_count += 1

        elif line_data[0] == 'Time':
            # guardar el tiempo del experimento
            data[experiment_count]['experiment_time'] = line_data[6]

        elif line_data[0] == '>>>':
            # guardar la iteracion del mejor resultado
            data[experiment_count]['iteration_of_best_resources'] = iteration_count

        elif line_data[0] == experiment_separator_end:
            experiment_count += 1


with open(FILE_JSON, 'w') as f:
    json.dump(data, f, indent=4)


# data2 = data.split(experiment_separator_end)
# data = []
# for element in data2:
#     data.append(element.split(best_solution_separator))

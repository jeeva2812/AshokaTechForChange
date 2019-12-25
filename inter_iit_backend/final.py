#!./osmnx/bin/python3


"""Capacited Vehicles Routing Problem (CVRP)."""

from ortools.constraint_solver import routing_enums_pb2
from ortools.constraint_solver import pywrapcp
import numpy as np
import sys
import osmnx as ox
import networkx as nx
import json

num_waste_types = 4
price = [10, 10, 10, 10]
cost = 0.01
num_vehicles = 1
depots = 0
prop_const = 100 # have to tweak

# G = ox.graph_from_address('IIT Madras, India')

def print_solution(G, wt_lat_long_amount, num_vehicles, distance_matrix, manager, routing, assignment):
    """Prints assignment on console."""
    # Display dropped nodes.
    dropped_nodes = 'Dropped nodes:'
    for node in range(routing.Size()):
        if routing.IsStart(node) or routing.IsEnd(node):
            continue
        if assignment.Value(routing.NextVar(node)) == node:
            dropped_nodes += ' {}'.format(manager.IndexToNode(node))
    # print(dropped_nodes)
    # Display routes
    total_distance = 0
    total_load = 0
    total_income = 0
    for vehicle_id in range(num_vehicles):
        index = routing.Start(vehicle_id)
        # plan_output = 'Route for vehicle {}:\n'.format(vehicle_id)
        route_distance = 0
        route_load = 0
        route_income = 0
        path = []
        lat_lng_path = []

        node_index = 0
        while not routing.IsEnd(index):
            node_index = manager.IndexToNode(index)
            curr_load = 0; curr_inc = 0;
            for i in range(num_waste_types):
                curr_load += wt_lat_long_amount[node_index][i]
                curr_inc += wt_lat_long_amount[node_index][i] * price[i]
            route_income += curr_inc
            route_load += curr_load
            # plan_output += ' {0} Load({1}) -> '.format(node_index, route_load)
            previous_index = index
            index = assignment.Value(routing.NextVar(index))
            route_distance += routing.GetArcCostForVehicle(
                previous_index, index, vehicle_id)
            # print('prev_index: {0} index: {1} d: {2}'.format(previous_index, index, routing.GetArcCostForVehicle(previous_index, index, vehicle_id)))
            # print('index', manager.IndexToNode(index))
            path.append(distance_matrix[node_index][manager.IndexToNode(index)])
            lat_lng_path.append({
                'a0': wt_lat_long_amount[node_index][0],
                'a1': wt_lat_long_amount[node_index][1],
                'a2': wt_lat_long_amount[node_index][2],
                'a3': wt_lat_long_amount[node_index][3],
                'lat': wt_lat_long_amount[node_index][4],
                'lng': wt_lat_long_amount[node_index][5],
            })
        # plan_output += ' {0} Load({1})\n'.format(manager.IndexToNode(index),route_load)
        # plan_output += 'Distance of the route: {}m\n'.format(route_distance)
        # plan_output += 'Load of the route: {}m\n'.format(route_load)
        # plan_output += 'Profit of the route: {}\n'.format(route_income - cost*route_distance) # need to chan

        # path.append(nx.shortest_path(G,vertex[node_index],vertex[manager.IndexToNode(index)]))
        # ox.plot_graph_routes(G, path)

        # print(plan_output)
        total_distance += route_distance
        total_load += route_load
        total_income += route_income
    result = {
        'route': lat_lng_path,
        'total_distance': total_distance,
        'est_profit': total_income - cost*route_distance
    }
    print(json.dumps(result))
    # print('Total Distance of all vehicle routes: {}m'.format(total_distance))
    # print('Total Load of all vehicle routes: {}'.format(total_load))


def main():

    vehicle_cap = float(sys.argv[1])
    query_distance = int(sys.argv[2])
    csv = np.genfromtxt('temp.csv', delimiter=',')
    # print(csv)
    
    r, c = csv.shape
    # print(r,c)
    num_nodes = r-1
    wt_lat_long_amount = csv[1:] # is a dict
    # print(wt_lat_long_amount)
    lat_index = 4; long_index = 5

    # print(query_distance)
    G = ox.graph_from_point((wt_lat_long_amount[0][lat_index], wt_lat_long_amount[0][long_index]), distance=query_distance)
    distance_matrix = -1 * np.ones((num_nodes,num_nodes))

    """Solve the CVRP problem."""

    # Create the routing index manager.
    manager = pywrapcp.RoutingIndexManager(num_nodes, #one less than the number of rows
                                           num_vehicles, depots)

    # Create Routing Model.
    routing = pywrapcp.RoutingModel(manager)


    # Create and register a transit callback.
    def distance_callback(from_index, to_index):
        """Returns the distance between the two nodes."""
        # Convert from routing variable Index to distance matrix NodeIndex.
        from_node = manager.IndexToNode(from_index)
        to_node = manager.IndexToNode(to_index)

        if distance_matrix[from_node][to_node] == -1:
            node1 = ox.get_nearest_node(G, (wt_lat_long_amount[from_node][lat_index], wt_lat_long_amount[from_node][long_index]))
            node2 = ox.get_nearest_node(G, (wt_lat_long_amount[to_node][lat_index], wt_lat_long_amount[to_node][long_index]))
            distance_matrix[from_node][to_node] = nx.shortest_path_length(G, node1, node2, weight='length')
            # print(from_node,'->',to_node, ': ', distance_matrix[from_node][to_node])

        total_price = 0
        for i in range(num_waste_types):
            total_price += wt_lat_long_amount[to_node][i] * price[i]
        print

        # need to minimise the loss 
        # print('Cost: ',(cost*distance_matrix[from_node][to_node] - total_price) )
        return prop_const*(cost*distance_matrix[from_node][to_node]) # multiplying by 10 because everything is int 

    transit_callback_index = routing.RegisterTransitCallback(distance_callback)

    # Define cost of each arc.
    routing.SetArcCostEvaluatorOfAllVehicles(transit_callback_index)


    # Add Capacity constraint.
    def demand_callback(from_index):
        """Returns the demand of the node."""
        # Convert from routing variable Index to demands NodeIndex.
        from_node = manager.IndexToNode(from_index)
        amount = 0
        for i in range(num_waste_types):
            amount += wt_lat_long_amount[from_index][i]
        return amount

    demand_callback_index = routing.RegisterUnaryTransitCallback(demand_callback)

    routing.AddDimensionWithVehicleCapacity(
        demand_callback_index,
        0,  # null capacity slack
        [vehicle_cap],  # vehicle maximum capacities
        True,  # start cumul to zero
        'Capacity')
    
    # Allow to drop nodes.
     # for total_price
    for node in range(1, num_nodes):
        total_price = 0
        for i in range(num_waste_types):
            total_price += price[i] * wt_lat_long_amount[node][i]
        routing.AddDisjunction([manager.NodeToIndex(node)], int(prop_const*total_price))

    # Setting first solution heuristic.
    search_parameters = pywrapcp.DefaultRoutingSearchParameters()
    search_parameters.first_solution_strategy = (routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC)

    # Solve the problem.
    assignment = routing.SolveWithParameters(search_parameters)

    # Print solution on console.
    if assignment:
        print_solution(G, wt_lat_long_amount, num_vehicles, distance_matrix, manager, routing, assignment)


if __name__ == '__main__':
    main()




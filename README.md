Dijkstra Map Pathfinder
An optimized implementation of Dijkstra’s Algorithm for computing the shortest path on large geographic maps.


 Project Overview
This program loads real-world map data, such as `usa.txt`, which contains over 87,000 intersections and 121,000 roads, and efficiently computes the shortest path between any two cities using Dijkstra’s algorithm.
It also visualizes the cities and roads using JavaFX and highlights the computed path.


 Features
1.Efficient Dijkstra implementation using a priority queue (min-heap)
2.Fast repeated shortest path queries with optimizations
3.Graph visualization with nodes and paths
4.Real map data support (e.g., `usa.txt`)
5.Customizable UI (colors, click-to-select cities)


Input Format
The input map file format:
1. Number of vertices and edges
2. Vertex list: index, x-coordinate, y-coordinate
3. Edge list: pairs of vertex indices
4. Source and target vertex indices

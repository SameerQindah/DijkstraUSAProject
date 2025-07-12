package application;

import java.util.*;

public class Graph {
    public int V;
    public List<double[]> coordinates;
    public Map<Integer, List<Node>> adj;

    public Graph(int V) {
        this.V = V;
        coordinates = new ArrayList<>(Collections.nCopies(V, null));
        adj = new HashMap<>();
    }

    public void addVertex(int id, double x, double y) {
        coordinates.set(id, new double[]{x, y});
        adj.put(id, new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        double[] a = coordinates.get(u);
        double[] b = coordinates.get(v);
        double weight = Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
        adj.get(u).add(new Node(v, weight));
        adj.get(v).add(new Node(u, weight));
    }
}

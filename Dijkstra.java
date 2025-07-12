package application;

import java.util.*;

public class Dijkstra {
    public static double[] dist;
    public static int[] prev;

    public static void compute(Graph g, int source, int target) {
        int V = g.V;
        dist = new double[V];
        prev = new int[V];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0));

        boolean[] visited = new boolean[V];

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;

            if (visited[u]) continue;
            visited[u] = true;

            if (u == target) break;

            for (Node neighbor : g.adj.get(u)) {
                int v = neighbor.id;
                double weight = neighbor.dist;
                double alt = dist[u] + weight;

                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                    pq.add(new Node(v, alt));
                }
            }
        }
    }

    public static List<Integer> getPath(int target) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}
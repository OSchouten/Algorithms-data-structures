package graphs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectedGraph<V extends Identifiable, E> {

    private final Map<String, V> vertices = new HashMap<>();
    private final Map<V, Map<V, E>> edges = new HashMap<>();

    /**
     * representation invariants:
     * 1.  the vertices map stores all vertices by their identifying id (which prevents duplicates)
     * 2.  the edges map stores all directed outgoing edges by their from-vertex and then in the nested map by their to-vertex
     * 3.  there can only be two directed edges between any two given vertices v1 and v2:
     * one from v1 to v2 in edges.get(v1).get(v2)
     * one from v2 to v1 in edges.get(v2).get(v1)
     * 4.  every vertex instance in the key-sets of edges shall also occur in the vertices map and visa versa
     **/

    public DirectedGraph() {
    }

    public Collection<V> getVertices() {
        return vertices.values();
    }

    /**
     * finds the vertex in the graph identified by the given id
     *
     * @param id
     * @return the vertex that matches the given id
     * null if none of the vertices matches the id
     */
    public V getVertexById(String id) {
        return vertices.get(id);
    }

    /**
     * retrieves the collection of neighbour vertices that can be reached directly
     * via an out-going directed edge from 'fromVertex'
     *
     * @param fromVertex
     * @return null if fromVertex cannot be found in the graph
     * an empty collection if fromVertex has no neighbours
     */
    public Collection<V> getNeighbours(V fromVertex) {
        if (fromVertex == null) return null;
        return edges.get(fromVertex).keySet();
    }

    public Collection<V> getNeighbours(String fromVertexId) {
        return this.getNeighbours(this.getVertexById(fromVertexId));
    }

    /**
     * retrieves the collection of edges
     * which connects the 'fromVertex' with its neighbours
     * (only the out-going edges directed from 'fromVertex' towards a neighbour shall be included
     *
     * @param fromVertex
     * @return null if fromVertex cannot be found in the graph
     * an empty collection if fromVertex has no out-going edges
     */
    public Collection<E> getEdges(V fromVertex) {
        if (fromVertex == null) return null;
        return edges.get(fromVertex).values();
    }

    public Collection<E> getEdges(String fromId) {
        return this.getEdges(this.getVertexById(fromId));
    }

    /**
     * Adds newVertex to the graph, if not yet present and in a way that maintains the representation invariants.
     * If a duplicate of newVertex (with the same id) already exists in the graph,
     * nothing will be added, and the existing duplicate will be kept and returned.
     *
     * @param newVertex
     * @return the duplicate of newVertex with the same id that already exists in the graph,
     * or newVertex itself if it has been added.
     */
    public V addOrGetVertex(V newVertex) {
        if (vertices.containsKey(newVertex.getId()))
            return vertices.get(newVertex.getId());

        vertices.putIfAbsent(newVertex.getId(), newVertex);
        return newVertex;
    }


    /**
     * Adds a new, directed edge 'newEdge'
     * from vertex 'fromVertex' to vertex 'toVertex'
     * No change shall be made if a directed edge already exists between these vertices
     *
     * @param fromVertex the start vertex of the directed edge
     * @param toVertex   the target vertex of the directed edge
     * @param newEdge    the instance with edge information
     * @return whether the edge has been added successfully
     */
    public boolean addEdge(V fromVertex, V toVertex, E newEdge) {
        if (fromVertex != null)
            addOrGetVertex(fromVertex);
        if (toVertex != null)
            addOrGetVertex(toVertex);
        if (fromVertex == null || toVertex == null || newEdge == null)
            return false;

        if (!this.edges.containsKey(fromVertex)) {
            Map<V, E> map = new HashMap<>();
            map.put(toVertex, newEdge);
            edges.put(fromVertex, map);
            return true;
        } else if (!this.edges.get(fromVertex).containsKey(toVertex)) {
            edges.get(fromVertex).put(toVertex, newEdge);
            return true;
        }
        return false;
    }

    /**
     * Adds a new, directed edge 'newEdge'
     * from vertex with id=fromId to vertex with id=toId
     * No change shall be made if a directed edge already exists between these vertices
     *
     * @param fromId  the id of the start vertex of the outgoing edge
     * @param toId    the id of the target vertex of the directed edge
     * @param newEdge the instance with edge information
     * @return whether the edge has been added successfully
     */
    public boolean addEdge(String fromId, String toId, E newEdge) {
        if (this.getVertexById(fromId) == null || this.getVertexById(toId) == null) return false;
        return addEdge(getVertexById(fromId), getVertexById(toId), newEdge);
    }

    /**
     * Adds two directed edges: one from v1 to v2 and one from v2 to v1
     * both with the same edge information
     *
     * @param v1
     * @param v2
     * @param newEdge
     * @return whether both edges have been added
     */
    public boolean addConnection(V v1, V v2, E newEdge) {
        return this.addEdge(v1, v2, newEdge) && this.addEdge(v2, v1, newEdge);
    }

    /**
     * Adds two directed edges: one from id1 to id2 and one from id2 to id1
     * both with the same edge information
     *
     * @param id1
     * @param id2
     * @param newEdge
     * @return whether both edges have been added
     */
    public boolean addConnection(String id1, String id2, E newEdge) {
        return this.addEdge(id1, id2, newEdge) && this.addEdge(id2, id1, newEdge);
    }

    /**
     * retrieves the directed edge between 'fromVertex' and 'toVertex' from the graph, if any
     *
     * @param fromVertex the start vertex of the designated edge
     * @param toVertex   the end vertex of the designated edge
     * @return the designated directed edge that has been registered in the graph
     * returns null if no connection has been set up between these vertices in the specified direction
     */
    public E getEdge(V fromVertex, V toVertex) {
        if (fromVertex == null || toVertex == null) return null;
        return edges.get(fromVertex).get(toVertex);
    }

    public E getEdge(String fromId, String toId) {
        return this.getEdge(this.vertices.get(fromId), this.vertices.get(toId));
    }

    /**
     * @return the total number of vertices in the graph
     */
    public int getNumVertices() {
        return vertices.size();
    }

    /**
     * calculates and returns the total number of directed edges in the graph data structure
     *
     * @return the total number of edges in the graph
     */
    public int getNumEdges() {
        return edges.values().stream().mapToInt(veMap -> veMap.values().size()).sum();
    }

    /**
     * Remove vertices without any connection from the graph
     */
    public void removeUnconnectedVertices() {
        this.edges.entrySet().removeIf(e -> e.getValue().size() == 0);
        this.vertices.entrySet().removeIf(e -> !this.edges.containsKey(e.getValue()));
    }

    /**
     * represents a path of connected vertices and edges in the graph
     */
    public class DGPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private final Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are connected in the graph by a directed edge,
         * i.e. FOR ALL i: 0 < i < vertices.length: this.getEdge(vertices[i-1],vertices[i]) will provide edge information of the connection
         * 2. a path with one vertex has no edges
         * 3. a path without vertices is empty
         * totalWeight is a helper attribute to capture additional info from searches, not a fundamental property of a path
         * visited is a helper set to be able to track visited vertices in searches, not a fundamental property of a path
         **/

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            for (V v : this.vertices) {
                sb.append(separator + v.getId());
                separator = ", ";
            }
            sb.append(")");
            return sb.toString();
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() {
            return this.visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath depthFirstSearch(String startId, String targetId) {
        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();

        if (start.equals(target)) {
            path.vertices.add(target);
            path.visited.add(target);
            return path;
        }

        dfsRecursive(start, target, path);
        if (!path.visited.contains(target))
            return null;

        return path;
    }

    private DGPath dfsRecursive(V current, V target, DGPath path) {
        if (path.getVisited().contains(current)) {
            return null;
        }
        path.visited.add(current);

        if (current.equals(target)) {
            return path;
        }
        for (V neighbour : this.getNeighbours(current.getId())) {
            if (dfsRecursive(neighbour, target, path) != null) {
                path.vertices.addFirst(current);
                return path;
            }
        }
        return null;
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath breadthFirstSearch(String startId, String targetId) {

        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.visited.add(start);

        // easy target
        if (start.equals(target)) {
            path.vertices.add(target);
            return path;
        }
        Queue<V> nextToVisit = new LinkedList<>();
        // map of each nodes and where they are visited from
        Map<V, V> visitedFrom = new HashMap<>();

        nextToVisit.offer(start);
        visitedFrom.put(start, null);

        while (!nextToVisit.isEmpty()) {
            V current = nextToVisit.poll();
            path.visited.add(current);

            if (current.getId().equals(target.getId())) {
                while (current != null) {
                    path.vertices.addFirst(current);
                    current = visitedFrom.get(current);
                }
                return path;
            }

            nextToVisit.addAll(this.getNeighbours(current.getId())
                    .stream()
                    .filter(k -> !path.visited.contains(k) && !nextToVisit.contains(k))
                    .collect(Collectors.toList()));

            for (V neighbour : getNeighbours(current.getId())) {
                if (visitedFrom.get(neighbour) == null && !path.visited.contains(neighbour))
                    visitedFrom.put(neighbour, current);
            }
        }
        // no path found, graph was not connected ???
        return null;
    }

    // helper class to register the state of a vertex in dijkstra shortest path algorithm
// your may change this class or delete it altogether follow a different approach in your implementation
    private class DSPNode implements Comparable<DSPNode> {
        protected V vertex;                // the graph vertex that is concerned with this DSPNode
        protected V fromVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex

        private DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(weightSumTo, dspv.weightSumTo);
        }

        @Override
        public String toString() {
            return "DSPNode{" +
                    "vertex=" + vertex +
                    ", fromVertex=" + fromVertex +
                    ", marked=" + marked +
                    ", weightSumTo=" + weightSumTo +
                    '}';
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     * according to Dijkstra's algorithm of a minimum spanning tree
     *
     * @param startId      id of the start vertex of the search
     * @param targetId     id of the target vertex of the search
     * @param weightMapper provides a function, by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPath(String startId, String targetId,
                                       Function<E, Double> weightMapper) {

        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.visited.add(start);

        // easy target
        if (start.equals(target)) {
            path.vertices.add(start);
            return path;
        }

        // keep track of the DSP status of all visited nodes
        // you may choose a different approach of tracking progress of the algorithm, if you wish

        //Priority queue to know where to go next


        // initialise the progress of the start node
        Map<V, DSPNode> VisitedFrom = new HashMap<>();
        PriorityQueue<DSPNode> pQueue = new PriorityQueue<>();
        DSPNode nextDspNode = new DSPNode(start);
        nextDspNode.weightSumTo = 0.0;
        pQueue.add(nextDspNode);
        VisitedFrom.put(start, nextDspNode);
        while (true) {
            DSPNode currentDSP = pQueue.poll();

            if (currentDSP == null)
                return null;

            currentDSP.marked = true;
            V currentVertex = currentDSP.vertex;

            path.visited.add(currentVertex);

            if (currentVertex.equals(target)) {
                path.vertices.addAll(Order(currentDSP, VisitedFrom));
                path.totalWeight = currentDSP.weightSumTo;
                return path;
            }

            for (V neighbour : getNeighbours(currentVertex.getId())) {
                double distance = 0;
                if (weightMapper != null)
                    distance = weightMapper.apply(getEdge(currentVertex.getId(), neighbour.getId()));

                double vDistance = currentDSP.weightSumTo + distance;
                DSPNode nextDSP = VisitedFrom.get(neighbour);

                if (nextDSP == null) {
                    nextDSP = new DSPNode(neighbour);
                    nextDSP.weightSumTo = vDistance;
                    nextDSP.fromVertex = currentVertex;
                    VisitedFrom.put(neighbour, nextDSP);
                    pQueue.add(nextDSP);
                } else if (vDistance < nextDSP.weightSumTo) {
                    nextDSP.weightSumTo = vDistance;
                    nextDSP.fromVertex = currentDSP.vertex;
                    pQueue.remove(nextDSP);
                    pQueue.add(nextDSP);
                }
            }
        }
    }

    private LinkedList<V> Order(DSPNode dspNode, Map<V, DSPNode> progressData) {
        LinkedList<V> shortestPath = new LinkedList<>();
        while (dspNode != null) {
            shortestPath.add(dspNode.vertex);
            dspNode = progressData.get(dspNode.fromVertex);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.getVertices().stream()
                .map(v -> v.toString() + ": " +
                        this.edges.get(v).entrySet().stream()
                                .map(e -> e.getKey().toString() + "(" + e.getValue().toString() + ")")
                                .collect(Collectors.joining(",", "[", "]"))
                )
                .collect(Collectors.joining(",\n  ", "{ ", "\n}"));
    }
}

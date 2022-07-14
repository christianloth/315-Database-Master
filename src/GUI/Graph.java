package GUI;
import java.util.*;

class Graph {

    //========================= NODE & DATA STRUCTURE INSTANTIATION =========================

    // Final output to be used by in the frontend
    private ArrayList<node> nodal_output;

    // Array  of lists for Adjacency List Representation
    private ArrayList<node> vertex_cluster;

    // Nodes are constructed to the purpose of an adjacency list graph
    // Additional features like aux_data serve to provide additional metadata in the palatable results
    static class node{

        boolean visited;
        Object data;

        ArrayList<Pair<Object, Object>> aux_data;
        LinkedList<node> ll;

        node(Object vertex){
            data = vertex;
            visited = false;
            aux_data = new ArrayList<>();
            ll = new LinkedList<>();
        }

    }

    //========================= GRAPHING =========================

    // Graph initialization
    Graph(){ vertex_cluster = new ArrayList<>(); }

    // Returns the number of the vertices in the graph, not the refined output
    int getGraphSize(){ return vertex_cluster.size(); }

    int getEdgeSize( Object node ){
        try {
            for (Graph.node value : vertex_cluster) {
                if (value.data.equals(node.toString()))
                    return value.ll.size();
            }
        } catch (Exception e){
            System.out.println("getEdgeSize Error :: " + e.toString());
        }
        return -1;
    }

    // Adding vertex to the graph, checks for repetition
    void addVertex( Object vertex ){
        try{
            for (Graph.node node : vertex_cluster)
                if (node.data.equals(vertex.toString()))
                    return;
            vertex_cluster.add(new node(vertex));
        } catch (Exception e){
            System.out.println("addVertex Error :: " + e.toString());
        }
    }

    //Function to add an edge into the graph. Directed edges will be defined with distinct games from the winner to the loser
    //Please pay close attention to how you structure the edges
    void addEdge ( Object vertex_one, Object vertex_two ){
        try{

            //System.out.println("vertex_one :: " + vertex_one);
            //System.out.println("vertex_two :: " + vertex_two);
            int edge_count = 0;
            int vertex_two_ref = -1;
            for(int i = 0; i  < vertex_cluster.size(); i++) {
                if(vertex_cluster.get(i).data.toString().equals(vertex_two.toString())) {
                    vertex_two_ref = i;
                }
            }

            if(vertex_two_ref == -1) {
                throw new NullPointerException(":: Edge is wack!");
                //return;
            }

            for (Graph.node node : vertex_cluster) {
                if (node.data.equals(vertex_one.toString())) {
                    node.ll.add(vertex_cluster.get(vertex_two_ref)); // throws an error if not found...
                    edge_count++;
                }
            }

            if(edge_count == 0)
                throw new NullPointerException(":: Edge is wack!");

        }catch( Exception e ){
            System.out.println("Edge Addition Error :: " + e.toString());
        }
    }

    // Finding the vertex, given the node header data, will return null if not found
    node getVertex( Object node ){
        try {
            for (Graph.node value : vertex_cluster) {
                if (value.data.equals(node.toString())) {
                    return value;
                }
            }
        } catch (Exception e){
            System.out.println("getVertex Error" + e.toString());
        }
        return null;
    }

    // Getting the vertex at the integer index passed into the function, checks for existence of the node
    node getVertexAtIndex( int index ){
        try {
            if(index < vertex_cluster.size() && index > -1) {
                return vertex_cluster.get(index);
            }
        } catch (Exception e){
            System.out.println("getVertex Error" + e.toString());
        }
        return null;
    }

    // Finding desired node "source", with (attr)ibuted data tied to auxiliary "data" link
    void addAuxData( Object source, Object attr, Object data ){
        try {

            for (Graph.node node : vertex_cluster) {
                if (node.data.toString().equals(source.toString())) {
                    node.aux_data.add(new Pair(attr, data));
                }
            }
        } catch (Exception e){
            System.out.println("addAuxData Error :: " + e.toString());
        }
    }

    // Initial DFS method used to check for loops, discovering longest pathway (more bragging rights)
    private ArrayList<node> DFS(Object source, Object destination){
        ArrayList<node> output = new ArrayList<>();
        node top;
        try{

            int start_index = -1;
            for(int i = 0; i < vertex_cluster.size(); i++) {
                vertex_cluster.get(i).visited = false;
                if(vertex_cluster.get(i).data.equals(source.toString()))
                    start_index = i;
            }

            if(start_index == -1)
                throw new NullPointerException(":: The starting index is wack");

            // Create a queue for DFS
            Stack<node> stack = new Stack<>();

            // Mark the current node as visited and enqueue it
            stack.push(vertex_cluster.get(start_index));

            // iterating down the stack node in the list
            while (stack.size() != 0) {

                top = stack.pop();

                if(!top.visited) {
                    top.visited = true;
                    output.add(top);
                }

                if(top.data.equals(destination.toString())){
                    return output;
                }

                Iterator<node> index = top.ll.listIterator();
                while (index.hasNext()) {
                    node n = index.next();
                    if (!n.visited) {
                        stack.push(n);
                    }
                }
            }

        } catch( Exception e ){
            System.out.println("DFS Error :: " + e.toString());
        }
        return output;
    }

    // Iterative BFS for shortest pathway planning
    private ArrayList<node> BFS(Object source, Object destination) {
        ArrayList<node> output = new ArrayList<>();
        node head;
        try{
            // resetting all nodes to unvisited, and finding the starting index
            int start_index = -1;
            for(int i = 0; i < vertex_cluster.size(); i++) {
                vertex_cluster.get(i).visited = false;
                if(vertex_cluster.get(i).data.equals(source.toString()))
                    start_index = i;
            }

            // Ensuring the starting index is found
            if(start_index == -1)
                throw new NullPointerException(":: The starting index is wack");
            // Create a queue for BFS
            LinkedList<node> queue = new LinkedList<>();

            // Mark the current node as visited and enqueue it
            vertex_cluster.get(start_index).visited = true;
            queue.add(vertex_cluster.get(start_index));

            while (queue.size() != 0) {

                head = queue.poll();
                output.add(head);

                if(head.data.equals(destination.toString())){
                    return output;
                }

                // Searching for direct relationship in the nodal adjacency list
                Iterator<node> temp_iter = head.ll.listIterator();
                while (temp_iter.hasNext()) {
                    node n = temp_iter.next();
                    if (n.data.equals(destination.toString())) {
                        output.add(n);
                        return output;
                    }
                }

                // enqueuing new nodes to the queue
                Iterator<node> index = head.ll.listIterator();
                while (index.hasNext()) {
                    node n = index.next();
                    if (!n.visited) {
                        n.visited = true;
                        queue.add(n);
                    }
                }
            }

        } catch( Exception e ){
            System.out.println("BFS Init Error :: " + e.toString());
        }
        return output;
    }

    // Reduces the BFS/DFS output to have direct relationships from nodes A to B only. This removes unconnected branches to the main branch
    private void snipBranches(){
        for(int i = nodal_output.size()-1; i > 0; i--){ // removes branches in the graph
            if(!nodal_output.get(i).aux_data.toString().contains(nodal_output.get(i-1).data.toString())){
                nodal_output.remove(i-1);
                i = nodal_output.size()-1;
            }
        }
    }

    // Outputs a palatable array list for table display
    ArrayList<Object> getResults(){
        ArrayList<Object> rslt = new ArrayList<>();

        for(Graph.node node : nodal_output){
            rslt.add(node.data);
            rslt.add(node.aux_data);
        }
        return rslt;
    }

    //========================= PRETTY PRINTING & FORMATTING =========================

    void prettyPrintGraph(){
        System.out.println("\n<><><><><><><><><><><><><><><><><>");
        System.out.println("<><><> Adjacency List Graph <><><>");
        System.out.println("<><><><><><><><><><><><><><><><><>\n");
        for (Graph.node node : vertex_cluster) {
            System.out.print("[" + node.data + "]");
            for (Graph.node n : node.ll) {
                System.out.print(" -> " + n.data);//+ " " + n.aux_data);
            }
            System.out.println();
        }
    }

    int DFSWrapper( Object source, Object destination ){

        nodal_output = DFS(source, destination);

        if(!nodal_output.get(nodal_output.size() - 1).data.equals(destination.toString())){ // checking for a viable chain
            return -1;
        }

        snipBranches();
        return 0;
    }

    int BFSWrapper( Object source, Object destination ){

        nodal_output = BFS(source, destination);

        if(!nodal_output.get(nodal_output.size() - 1).data.equals(destination.toString())){ // checking for a viable chain
            return -1;
        }

        snipBranches();
        return 0;
    }

    int prettyPrintDFS( Object source, Object destination ){

        DFSWrapper(source, destination);

        for (Graph.node node : nodal_output) {
            System.out.print(node.data.toString() + " ");
            for (int j = 0; j < node.aux_data.size(); j++) {
                System.out.print(node.aux_data.get(j).getLeft().toString() + " ");
                System.out.print(node.aux_data.get(j).getRight().toString() + ", ");
            }
            System.out.print(" -> ");
        }
        System.out.println("X");
        return 0;

    }

    int prettyPrintBFS( Object source, Object destination ){

        BFSWrapper(source, destination);

        for (Graph.node node : nodal_output) {
            System.out.print(node.data.toString() + " ");
            for (int j = 0; j < node.aux_data.size(); j++) {
                System.out.print(node.aux_data.get(j).getLeft().toString() + " ");
                System.out.print(node.aux_data.get(j).getRight().toString() + ", ");
            }
            System.out.print(" -> ");
        }
        System.out.println("X");
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Graph.node node : nodal_output) {
            result.append(node.data.toString()).append(" ").append(node.aux_data.toString()).append(", ");
        }
        return result.toString();
    }

}
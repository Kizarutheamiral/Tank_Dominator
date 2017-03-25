package com.dominator.game.Module;

import com.badlogic.gdx.math.Vector2;
import com.dominator.game.Quadtree.*;


import java.util.*;


/**
 * Created by Choujaa Wassil on 03/03/2017.
 *
 */
public class Pathfinder {

    QuadTree quadTree;
    private float maxSearchDistance = 1000000f;
    private final List<AstarNodes> vectorPool = new ArrayList<AstarNodes>();
    private ArrayList<AstarNodes> closed = new ArrayList<AstarNodes>();
    private SortedList open = new SortedList();
    private ArrayList<AstarNodes> path = new ArrayList<AstarNodes>();

    public Pathfinder(QuadTree tree) {
        this.quadTree = tree;
    }
/*
    /// First Implementation : Flow field
    public Vector2[][] generateFlowFieldFrom(int y, int x) {
        int[][] matrix = null;

        matrix = generateHeatMap(x, y);
        //window.setMatrix(matrix);
        if (matrix == null) {
            return null;
        }
        return generateVectorFromHeat(matrix, x, y);

    }

    private Vector2[][] generateVectorFromHeat(int[][] matrix, int targetX, int targetY) {
        Vector2[][] field = new Vector2[matrix.length][matrix[0].length];
        for (int y = 1; y < matrix.length - 1; y++) {
            for (int x = 1; x < matrix[0].length - 1; x++) {
                if (matrix[y][x] == 0) {
                    field[y][x] = new Vector2(0, 0);
                } else if (matrix[y][x + 1] == 0) {
                    if (matrix[y - 1][x] == 0) {
                        if (matrix[y][x - 1] == 0) {
                            field[y][x] = new Vector2(0, matrix[y][x] - matrix[y + 1][x]);
                        } else {
                            field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x], matrix[y][x] - matrix[y + 1][x]);
                        }
                    } else if (matrix[y + 1][x] == 0) {
                        if (matrix[y][x - 1] == 0) {
                            field[y][x] = new Vector2(0, 0);
                        } else {
                            field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x], 0);
                        }
                    } else {
                        field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x], matrix[y - 1][x] - matrix[y + 1][x]);
                    }
                } else if (matrix[y][x - 1] == 0) {
                    if (matrix[y - 1][x] == 0) {
                        if (matrix[y][x + 1] == 0) {
                            field[y][x] = new Vector2(0, matrix[y][x] - matrix[y + 1][x]);
                        } else {
                            field[y][x] = new Vector2(matrix[y][x] - matrix[y][x + 1], matrix[y][x] - matrix[y + 1][x]);
                        }
                    } else if (matrix[y + 1][x] == 0) {
                        if (matrix[y][x + 1] == 0) {
                            field[y][x] = new Vector2(0, 0);
                        } else {
                            field[y][x] = new Vector2(matrix[y][x] - matrix[y][x + 1], 0);
                        }
                    } else {
                        field[y][x] = new Vector2(matrix[y][x] - matrix[y][x + 1], matrix[y - 1][x] - matrix[y + 1][x]);
                    }
                } else if (matrix[y - 1][x] == 0) {
                    field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x + 1], matrix[y][x] - matrix[y + 1][x]);
                } else if (matrix[y + 1][x] == 0) {
                    field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x + 1], 0);

                }
                /// cas général
                else {
                    field[y][x] = new Vector2(matrix[y][x - 1] - matrix[y][x + 1], matrix[y - 1][x] - matrix[y + 1][x]);
                }
            }
        }
        return field;
    }

    private int[][] generateHeatMap(int x, int y) {

        if (x < 0 || y < 0 || x > Map.Graph.maxX || y > Map.Graph.maxY) {
            return null;
        }

        Integer goal = map.getGraph().findNearestNode(x, y);

        if (goal == -1) {
            return null;
        }

        boolean[] visited = new boolean[Map.Graph.maxX * map.getGraph().numvertices + Map.Graph.maxY];

        int matrix[][] = new int[Map.Graph.maxX][Map.Graph.maxY];
        LinkedList<Integer> queue = new LinkedList<Integer>();
        visited[goal] = true;
        queue.add(goal);

        if (map.getGraph().getNeghbour(goal) == null) {
            System.out.println("probleme with goal");
            return null;
        }

        int current;
        while (!queue.isEmpty()) {
            current = queue.poll();
            Iterator<Integer> i = map.getGraph().getNeghbour(current).listIterator();
            while (i.hasNext()) {
                int n = i.next();
                if (!visited[n]) {
                    if (matrix[map.getGraph().getX(current)][map.getGraph().getY(current)] < 0) {
                        matrix[map.getGraph().getX(n)][map.getGraph().getY(n)] = -matrix[map.getGraph().getX(current)][map.getGraph().getY(current)] + 1;

                    } else {
                        matrix[map.getGraph().getX(n)][map.getGraph().getY(n)] = matrix[map.getGraph().getX(current)][map.getGraph().getY(current)] + 1;
                    }

                    // if line of sight => negative value

                    visited[n] = true;
                    queue.add(n);
                }
            }
        }

        return matrix;
    }*/

    public ArrayList<AstarNodes> AstarPathFrom(float x, float y, float targetX, float targetY) {

        Node beginNode = (Node) quadTree.get(x, y, null);
        Node targetNode = (Node) quadTree.get(targetX, targetY, null);

        if (beginNode == null || targetNode == null) {
            System.out.println("null sorry");
            return null;
        }

        closed.clear();
        open.clear();
        path.clear();

        open.add(getNewVec().set(beginNode, null, targetNode));

        while (open.size() != 0 && open.first().node != targetNode) {

            AstarNodes current = open.first();
            open.remove(current);
            closed.add(current);

            Node[] neighbours = current.getNeighbours();

            // for each neighbour of the current:
            for (int i = 0; i < neighbours.length; i++) {

                Node neighbour = neighbours[i];
                // if neighbour in closed => continue
                boolean found = false;
                for (AstarNodes aClosed : closed) {if (aClosed.node == neighbour) {found = true;}}
                if (found) {continue;}
                // now find his corresponding AstarNode in the open
                AstarNodes astarNeighbour = open.find(neighbour);

                if(astarNeighbour == null){
                    open.add(getNewVec().set(neighbour,current,targetNode));
                }
                else {
                    // comparaison
                    float cost = current.cost + current.dist(astarNeighbour);
                    if(cost<=astarNeighbour.cost){
                        astarNeighbour.set(neighbour,current,targetNode);
                    }
                }
            }
        }


        AstarNodes last = open.first();

        while (last.parent != null){
            path.add(last);
            last = last.parent;
            System.out.println(last.getX()+" "+last.getY());
        }

        Collections.reverse(path);

        return (ArrayList<AstarNodes>) path.clone();
        // contruct path from current;
    }
    /**
     * A simple sorted list
     *
     * @author kevin => Thanks Kevin :D
     */
    private class SortedList {
        /** The list of elements */
        private ArrayList<AstarNodes> list = new ArrayList<AstarNodes>();

        /**
         * Retrieve the first element from the list
         *
         * @return The first element from the list
         */
        public AstarNodes first() {
            return list.get(0);
        }

        /**
         * Empty the list
         */
        public void clear() {
            list.clear();
        }

        /**
         * Add an element to the list - causes sorting
         *
         * @param o The element to add
         */
        public void add(AstarNodes o) {
            list.add(o);
            Collections.sort(list);
        }

        /**
         * Remove an element from the list
         *
         * @param o The element to remove
         */
        public void remove(AstarNodes o) {
            list.remove(o);
        }

        /**
         * Get the number of elements in the list
         *
         * @return The number of element in the list
         */
        public int size() {
            return list.size();
        }

        /**
         * Check if an element is in the list
         *
         * @param o The element to search for
         * @return True if the element is in the list
         */
        public boolean contains(AstarNodes o) {
            return list.contains(o);
        }

        public AstarNodes get(int j) {
            return list.get(j);
        }

        public AstarNodes find(Node search) {
            Iterator<AstarNodes> it = list.iterator();
            while (it.hasNext()){
                AstarNodes node = it.next();
                if (node.node == search){
                    return node;
                }
            }
            return null;
        }
    }

    private AstarNodes getNewVec() {
        return vectorPool.isEmpty() ? new AstarNodes() : vectorPool.remove(0);
    }

    private void free(AstarNodes v) {
        vectorPool.add(v);
    }

    public class AstarNodes implements Comparable {
        private boolean option = true;
        private float cost;
        private float estimated;
        private Node node;
        private AstarNodes parent;

        public AstarNodes() {
            super();
        }

        public AstarNodes set(Node node, AstarNodes parent, Node target) {
            this.node = node;
            this.cost = (parent==null) ? 0 : node.dist(parent.node) + parent.cost; // this cost == current Cost + dist between current and this one
            this.estimated = node.dist(target)*1.1f;
            this.parent = parent;

            if (parent!=null && parent.parent!=null && option){

                boolean los = quadTree.LOS(quadTree.getRootNode(), new Func() { @Override public void call(QuadTree quadTree, Node node) {}}, getX(), getY(), parent.parent.getX(), parent.parent.getY());

                float parentParentCost =  parent.parent.cost + parent.parent.dist(this);

                if (los){
                   // System.out.println(" ligne of sight between:\n"+parent.parent.getX()+" "+parent.parent.getY()+","+getX()+" "+getY());

                    if(parentParentCost<=cost){
                        this.parent = parent.parent;
                        this.cost = parentParentCost;
                    }
                }
            }
            return this;
        }


        @Override
        public int compareTo(Object o) {
            AstarNodes tmp = (AstarNodes) o;
            // compare the total cost for the sort
            if (this.cost+this.estimated < tmp.cost+tmp.estimated) {
                return -1;
            } else if (this.cost+this.estimated > tmp.cost+tmp.estimated) {
                return 1;
            } else {
                return 0;
            }
        }

        public Node[] getNeighbours(){
            return node.getNeighbours();
        }

        public float dist(AstarNodes astarNeighbour) {
            return astarNeighbour.node.dist(node);
        }

        public float getX(){
            return node.getCenterX();
        }

        public float getY(){
            return node.getCenterY();
        }

    }
}

import java.util.*;

/**
 * Created by Choujaa Wassil on 02/02/2017.
 *
 *
 * l'implémentation du graphe choisie est une liste de liste liée
 *
 * chaque noeud de la grille NxN possède une clé x*N+y de manière à accéder aux graphe : graph[x*N+y] de facon unique 
 *
 *
 *
 */

public class FlowFieldGenerator {

    HashMap<Integer, LinkedList<Integer>> graph = new HashMap<Integer, LinkedList<Integer>>();
    int numVertices;
    int verticesProduct;
    View window;
    public FlowFieldGenerator(int[][] matrix, View window) {
        /**         generate Graph from grid (=Tile based map)          **/
        verticesProduct = matrix.length*matrix[0].length;
        numVertices = matrix.length;
        LinkedList<Integer> tmp = new LinkedList<>();
        this.window = window;

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                //// les voisins sont maintenant le carré formé autour du poin
                if(x-1>=0 && matrix[y][x-1]==0){
                    tmp.add((x-1)*numVertices+(y));

                }

                if(x+1<numVertices && matrix[y][x+1]==0) {
                    tmp.add((x+1)*numVertices+(y));

                }

                if(y+1<numVertices && matrix[y+1][x]==0){
                    tmp.add((x)*numVertices+(y+1));
                }

                if(y-1>=0 && matrix[y-1][x]==0){
                    tmp.add((x)*numVertices+(y-1));
                }

                if(x-1>=0 && matrix[y][x-1]==0){
                    if(y-1>=0 && matrix[y-1][x-1]==0){
                        tmp.add((x-1)*numVertices+(y-1));
                    }
                    if(y+1<numVertices && matrix[y+1][x-1]==0){
                        tmp.add((x-1)*numVertices+(y+1));
                    }

                }

                if(x+1<numVertices && matrix[y][x+1]==0){
                    if(y+1<numVertices && matrix[y+1][x+1]==0){
                        tmp.add((x+1)*numVertices+(y+1));
                    }
                    if(y-1>=0 && matrix[y-1][x+1]==0){
                        tmp.add((x+1)*numVertices+(y-1));
                    }

                }

                graph.put((x*numVertices+y), (LinkedList<Integer>) tmp.clone());
                //System.out.println(Arrays.toString(tmp.toArray()));
                tmp.clear();
            }
        }

    }

    public Vector[][] generateFlowFieldFrom(int y, int x){
        double[][] matrix = generateHeatMap(x,y);
        window.setMatrix(matrix);
        return generateVectorFromHeat(matrix);

    }

    private Vector[][] generateVectorFromHeat(double[][] matrix) {
        Vector[][] field = new Vector[matrix.length][matrix[0].length];
        for (int y = 1; y < matrix.length-1; y++) {
            for (int x = 1; x < matrix[0].length-1; x++) {
                if(matrix[y][x]==0){
                    field[y][x] = new Vector(0,0);
                }
                else if(matrix[y][x+1]==0){
                    if(matrix[y-1][x] == 0){
                        if(matrix[y][x-1]==0){
                            field[y][x] = new Vector(0,matrix[y][x]-matrix[y+1][x]);
                        } else {
                            field[y][x] = new Vector(matrix[y][x-1]-matrix[y][x],matrix[y][x]-matrix[y+1][x]);
                        }
                    }
                    else if (matrix[y+1][x] == 0) {
                        if(matrix[y][x-1]==0){
                            field[y][x] = new Vector(0,0);
                        } else {
                            field[y][x] = new Vector(matrix[y][x-1]-matrix[y][x],0);
                        }
                    }
                    else {
                        field[y][x] = new Vector(matrix[y][x-1] - matrix[y][x], matrix[y - 1][x] - matrix[y + 1][x]);
                    }
                }
                else if (matrix[y][x-1]==0){
                    if(matrix[y-1][x] == 0){
                        if (matrix[y][x+1]==0){
                            field[y][x] = new Vector(0,matrix[y][x]-matrix[y+1][x]);
                        } else {
                            field[y][x] = new Vector(matrix[y][x]-matrix[y][x+1],matrix[y][x]-matrix[y+1][x]);
                        }
                    }
                    else if (matrix[y+1][x] == 0) {
                        if (matrix[y][x+1]==0){
                            field[y][x] = new Vector(0,0);
                        } else {
                            field[y][x] = new Vector(matrix[y][x]-matrix[y][x+1],0);
                        }
                    }
                    else {
                        field[y][x] = new Vector(matrix[y][x] - matrix[y][x+1], matrix[y - 1][x] - matrix[y + 1][x]);
                    }
                }
                else if(matrix[y-1][x] == 0){
                    field[y][x] = new Vector(matrix[y][x-1] - matrix[y][x+1], matrix[y][x] - matrix[y + 1][x]);
                }
                else if(matrix[y+1][x] == 0){
                    field[y][x] = new Vector(matrix[y][x-1] - matrix[y][x+1],0);

                }
                /// cas général
                else {
                    field[y][x] = new Vector(matrix[y][x-1]-matrix[y][x+1],matrix[y-1][x]-matrix[y+1][x]);
                }
            }
        }
        return field;
    }

    public double[][] generateHeatMap(int x, int y){

        Integer goal = getNode(x,y);

        boolean[] visited = new boolean[verticesProduct+1];
        double[][] matrix = new double[numVertices][numVertices];
        LinkedList<Integer> queue = new LinkedList<Integer>();
        visited[goal] = true;
        queue.add(goal);

        if(goal==null){
            System.out.println("probleme with goal");
            return null;
        }
        //matrix[x][y]=1;

        Integer right = getNode(x+1,y);
        Integer down = getNode(x,y-1);
        Integer right_down = getNode(x+1,y-1);

/*        if(right!=null){
            visited[right]=true;
            queue.add(right);
            matrix[x+1][y]=1;

        }
        if(down!=null){
            visited[down]=true;
            queue.add(down);
            matrix[x][y-1]=1;

        }
        if(right_down!=null){
            visited[right_down]=true;
            queue.add(right_down);
            matrix[x+1][y-1]=1;

        }*/
        int current;
        while (queue.size() != 0)
        {
            current = queue.poll();
            Iterator<Integer> i = getNeighbour(current).listIterator();
            while (i.hasNext())
            {

                int n = i.next();
                if (!visited[n])
                {
                    if(check_if_Diagonal(current,n)){
                        matrix[(int) Math.floor(n/numVertices)][n%numVertices] = matrix[(int) Math.floor(current/numVertices)][current%numVertices]+1.44;
                    } else {
                        matrix[(int) Math.floor(n/numVertices)][n%numVertices] = matrix[(int) Math.floor(current/numVertices)][current%numVertices]+1;
                    }
                    visited[n]=true;
                    queue.add(n);
                }
            }
        }
        /*for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }*/
        return matrix;
    }

    private boolean check_if_Diagonal(int current, int n) {
        int x = (int) Math.floor(current/numVertices);
        int y = (int) Math.floor(current%numVertices);
        int neighbour_x = (int) Math.floor(n/numVertices);
        int neighbour_y = (int) Math.floor(n%numVertices);
        boolean result = false;



        if(neighbour_x==x+1&&neighbour_y==y+1 || neighbour_x==x+1&&neighbour_y==y-1 ||neighbour_x==x-1&&neighbour_y==y+1||neighbour_x==x-1&&neighbour_y==y-1){
            result=true;
            //System.out.println("truuuuth");
        }
        return result;
    }
/// methode pour le graphe => a mettre dans une clase

    public Integer getNode(int x, int y ){
        return x*numVertices+y;
    }

    public LinkedList<Integer> getNeighbour(int node){
        return graph.get(node);
    }
}

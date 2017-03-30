package com.dominator.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.dominator.game.Quadtree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Choujaa Wassil on 22/02/2017.
 *
 *
 *  The map is generated from the json file map.json
 *  this file is composed by a list of Polygons and Circles wich represent the static objects.
 *  for the sake of simplicity the map is a 1x1 scaled axis multiply by the furthest polygon created in
 *  the box2D world wich means that with a camera scaled to 1 we have MAP_SIZE*100 px
 *  sized map
 *
 */

public class Map {

    public QuadTree quadTree;

    public static final int step = 10;
    public static final int scale = 100;   // 1 m = 100 px

    private JsonToMap loader;


    public Map() {
        loader = new JsonToMap(Gdx.files.internal("map.json"));
    }

    public Map loadMap(World world){

        // 1. Create a BodyDef, as usual.
        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyDef.BodyType.StaticBody;

        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1f;
        fd.friction = 0f;
        fd.restitution = 0.3f;

        // 3. Create a Body, as usual.
        Body map = world.createBody(bd);

        // 4. Create the body fixture automatically by using the loader and return
        // map is 100x100
        JsonToMap.RigidBodyModel model =  loader.attachFixture(map, "Name", fd, scale);

        // 5. Now get all the circles and polygons of the map and generate the graph

        generateQuadtree(model);


        quadTree.traverse(quadTree.getRootNode(), new Func() {
            @Override
            public void call(QuadTree quadTree, final Node node1) {

                final ArrayList<Node> arr = new ArrayList<Node>();

                quadTree.navigate(quadTree.getRootNode(), new Func() {
                    @Override
                    public void call(QuadTree quadTree, Node node2) {
                        if(node2.getNodeType() == NodeType.LEAF_EMPTY && node2!=node1){
                            arr.add(node2);
                        }
                    }
                },node1.getCenterX()-node1.getW()/2-step/2,node1.getCenterY()-node1.getW()/2-step/2,node1.getW()+step,node1.getW()+step);

                node1.setNeighbours(arr.toArray(new Node[arr.size()]));
            }
        });

        return this;

    }

   private void generateQuadtree(JsonToMap.RigidBodyModel model){

        float max = (model.maxX > model.maxY)? model.maxX : model.maxY;

        quadTree = new QuadTree(0,0,max);

        for (JsonToMap.PolygonModel polygon : model.polygons) {
            quadTree.subdivide(polygon,quadTree.getRootNode());
        }

        for (JsonToMap.CircleModel circle:model.circles) {
            quadTree.subdivide(circle,quadTree.getRootNode());
        }
    }
/*
    private HashMap<Integer,LinkedList<Integer>> convertToGraph(JsonToMap.RigidBodyModel model, World world) {

        // The RigidiModel is scaled in our world coordinate system/scale
        HashMap<Integer, LinkedList<Integer>> graphTmp = new HashMap<Integer, LinkedList<Integer>>();

        int maxY = Math.round(model.maxY);
        int maxX = Math.round(model.maxX);



                //System.out.println(maxX+" "+maxY);
        // now we got the dimension of the map, lets generate the pathable node:
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        fd.shape = shape;

        int counter = 0;

        boolean result = true;

        System.out.println(maxX+" "+maxY);

        int[][] nodes = new int[maxX/step][maxY/step];

        for (float x = 0; x < maxX/step; x++) {
            for (float y = 0; y < maxY/step; y++) {
                for (JsonToMap.PolygonModel polygon : model.polygons) {
                    if(polygon.contain(x*step,y*step)){
                        result = false;
                    }
                }
                if (!result){
                    result = true;
                    continue;
                }
                for (JsonToMap.CircleModel circle:model.circles) {
                    if(circle.contain(x*step,y*step)){
                        result = false;
                    }
                }
                if (!result){
                    result = true;
                    continue;
                }
                // Add the node into the graph

                nodes[(int)x][(int)y] = 1;
*//*                BodyDef bd = new BodyDef();
                bd.position.set(x*step,y*step);
                bd.type = BodyDef.BodyType.StaticBody;
                Body node = world.createBody(bd);
                node.createFixture(fd);*//*
                counter++;
            }
        }

        graph.numvertices = counter;

        System.out.println(counter);

        for (int x = 0; x < maxX - step;x+=step) {
            for (int y = 0; y < maxY - step; y+=step) {
                int matx = x/step ;
                int maty = y/step ;
                //System.out.print(nodes[matx][maty]);

                if(nodes[matx][maty]==1){
                    LinkedList<Integer> tmp = new LinkedList<Integer>();
                    if( x + step <maxX  - step&& nodes[matx+1][maty] == 1){
                        tmp.add(graph.getNode(matx+1,maty));
                    }
                     if( x - step >= 0 && nodes[matx-1][maty]==1){
                        tmp.add(graph.getNode(matx-1,maty));
                    }
                     if(y + step < maxY - step && nodes[matx][maty+1]==1){
                        tmp.add(graph.getNode(matx,maty+1));
                    }
                     if(y - step >= 0 && nodes[matx][maty-1]==1){
                        tmp.add(graph.getNode(matx,maty-1));
                    }

                    graphTmp.put(graph.getNode(matx,maty), (LinkedList<Integer>) tmp.clone());

                }
            }
            //System.out.println();
        }

        graph.tile_size = step;

        graph.maxX = maxX/step;
        graph.maxY = maxY/step;

        return graphTmp;

    }

    public static class Graph{

        public HashMap<Integer, LinkedList<Integer>> nodeTable = new HashMap<Integer, LinkedList<Integer>>();
        public int numvertices;
        public int tile_size;
        public static int maxX;
        public static int maxY;

        public LinkedList<Integer> getNeghbour(int x, int y){
            return nodeTable.get(getNode(x,y));
        }
        public LinkedList<Integer> getNeghbour(int node){
            return nodeTable.get(node);
        }

        public int getNode(int x, int y){
            return x*numvertices+y;
        }

        public int getX(int Node){
            return (int) Math.floor(Node/numvertices);
        }

        public int getY(int Node){
            return (int) Math.floor(Node%numvertices);
        }

        public int findNearestNode(int x,int y){
            if(nodeTable.containsKey(getNode(x,y))){
                return getNode(x,y);
            } else {
                int i =0;
                boolean visited[][] = new boolean[step][step];
                while(i<step/2){
                    for (int j = x - i; j <= x + i; j++) {
                        for (int k = y - i; k <= y + i; k++) {
                            if(!visited[j-x+step/2][k-y+step/2]){
                                if(nodeTable.containsKey(getNode(j,k))){
                                    return getNode(j,k);
                                }
                                visited[j-x+step/2][k-y+step/2]=true;
                            }
                        }
                    }
                    i++;
                }
            }
            return -1;
        }

    }*/
}

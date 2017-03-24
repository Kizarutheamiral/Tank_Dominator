package com.dominator.game.Quadtree;

import com.badlogic.gdx.math.Vector2;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class Node implements Comparable {

    private float x;
    private float y;
    private float w;
    private Node parent;
    private NodeType nodetype;
    private Node nw;
    private Node ne;
    private Node sw;
    private Node se;
    private Node[] neighbours;
    /**
     * Constructs a new quad tree node.
     *
     * @param {double} x X-coordiate of node.
     * @param {double} y Y-coordinate of node.
     * @param {double} w Width of node.
     * @param {double} h Height of node.
     * @param {Node}   opt_parent Optional parent node.
     * @constructor
     */
    public Node(float x, float y, float w, Node opt_parent, NodeType type) {
        this.x = x; // centerX
        this.y = y; // cneterY
        this.w = w;
        this.parent = opt_parent;
        this.nodetype = type;
    }



    //// Optimisation : faire le calcul soi meme sans la librairie Geom
    public boolean RectIntersects(float left, float bottom, float width, float height) {
        Rectangle2D rec1 = new Rectangle2D.Float(left,bottom,width,height);
        Rectangle2D rec2 = new Rectangle2D.Float(getCenterX() - getW()/2,getCenterY() - getW()/2, getW(),getW());
        return rec1.intersects(rec2);
    }

    public boolean LigneIntersects(float x1, float y1, float x2, float y2){
        Line2D ligne = new Line2D.Float(x1,y1,x2,y2);
        Rectangle2D rec2 = new Rectangle2D.Float(getCenterX() - getW()/2,getCenterY() - getW()/2, getW(),getW());
        return ligne.intersects(rec2);
    }

    public boolean contain(float x, float y) {
        //return !(x>getCenterX()+getW()/2 || y>getCenterY()+getW()/2 || x<getCenterX()-getW()/2 || y<getCenterY()-getW()/2);
        //System.out.println(x+" "+y);
        if(x>getCenterX()-getW()/2 && x<getCenterX()+getW()/2 && y>getCenterY()-getW()/2 && y<getCenterY()+getW()/2){
            return true;
        }else {
            return false;
        }
    }

    public float getCenterX() {
        return x;
    }

    public void setCenterX(float x) {
        this.x = x;
    }

    public float getCenterY() {
        return y;
    }

    public void setCenterY(float y) {
        this.y = y;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node opt_parent) {
        this.parent = opt_parent;
    }

    public void setNodeType(NodeType nodetype) {
        this.nodetype = nodetype;
    }

    public NodeType getNodeType() {
        return this.nodetype;
    }

    public void setNw(Node nw) {
        this.nw = nw;
    }

    public void setNe(Node ne) {
        this.ne = ne;
    }

    public void setSw(Node sw) {
        this.sw = sw;
    }

    public void setSe(Node se) {
        this.se = se;
    }

    public Node getNe() {
        return ne;
    }

    public Node getNw() {
        return nw;
    }

    public Node getSw() {
        return sw;
    }

    public Node getSe() {
        return se;
    }

    public Node[] getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Node[] neighbours) {
        this.neighbours = neighbours;
    }

    public float dist(Node targetNode) {
        return Math.abs(targetNode.x-x)+ Math.abs(targetNode.y-y);
    }

    @Override
    public int compareTo(Object o) {
        Node tmp = (Node) o;
        if (this.x < tmp.x) {
            return -1;
        } else if (this.x > tmp.x) {
            return 1;
        } else {
            if (this.y < tmp.y) {
                return -1;
            } else if (this.y > tmp.y) {
                return 1;
            }
            return 0;
        }
    }
}

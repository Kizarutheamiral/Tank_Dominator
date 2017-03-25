package com.dominator.game.Quadtree;


import com.dominator.game.Entities.JsonToMap;
import com.dominator.game.Entities.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Datastructure: A point Quad Tree for representing 2D data. Each
 * region has the same ratio as the bounds for the tree.
 * <p/>
 * The implementation currently requires pre-determined bounds for data as it
 * can not rebalance itself to that degree.
 */
public class QuadTree {


    private Node root_;
    private int count_ = 0;

    /**
     * Constructs a new quad tree.
     *
     * @param {double} minX Minimum x-value that can be held in tree.
     * @param {double} minY Minimum y-value that can be held in tree.
     * @param {double} maxX Maximum x-value that can be held in tree.
     * @param {double} maxY Maximum y-value that can be held in tree.
     */
    public QuadTree(float minX, float minY, float width) {
        this.root_ = new Node(minX + width/2, minY + width/2, width, null,NodeType.LEAF_EMPTY);
    }

    /**
     * Returns a reference to the tree's root node.  Callers shouldn't modify nodes,
     * directly.  This is a convenience for visualization and debugging purposes.
     *
     * @return {Node} The root node.
     */
    public Node getRootNode() {
        return this.root_;
    }

    public void subdivide(JsonToMap.ShapeModel obstacle, Node parent){
        // no check if outside the bound of the three
        switch (parent.getNodeType()) {
            case LEAF_EMPTY:
                this.split(obstacle, parent);
                break;
            case POINTER:
                Node[] nodes = getNodeIntersected(obstacle, parent);
                for (int i = 0; i < nodes.length; i++) split(obstacle,nodes[i]);
                break;
            case LEAF_OBSTRUCTED:
                this.split(obstacle, parent);
                break;
            default:
                throw new QuadTreeException("Invalid nodeType in parent");
        }
    }

    /**
     * Return the LEAF_EMPTY nodes Intersected by the obstacles within the parent bound
     */
    private Node[] getNodeIntersected(final JsonToMap.ShapeModel obstacle, Node parent) {
        final List<Node> arr = new ArrayList<Node>();

        this.navigate(parent, new Func() {
            @Override
            public void call(QuadTree quadTree, Node node ) {
                if((node.getNodeType() == NodeType.LEAF_EMPTY || node.getNodeType() == NodeType.LEAF_OBSTRUCTED ) && obstacle.intersect(node)){
                    arr.add(node);
                }
            }
        }, obstacle.getMinX(), obstacle.getMinY(), obstacle.getMaxX(), obstacle.getMaxY());

        return arr.toArray(new Node[arr.size()]);
    }

    /**
     *
     * @param {QuadTree.Node} node The node to split.
     * @param obstacle
     * @private
     */
    private void split(JsonToMap.ShapeModel obstacle, Node parent) {

        float w = parent.getW() / 2;
        float x = parent.getCenterX() - w;
        float y = parent.getCenterY() - w;

        if(w > Map.step){
            parent.setNodeType(NodeType.POINTER);

            parent.setSw(new Node(x+w/2, y+w/2, w, parent, NodeType.LEAF_EMPTY));
            count_++;
            if(obstacle.intersect(parent.getSw())){
                parent.getSw().setNodeType(NodeType.LEAF_OBSTRUCTED);
                subdivide(obstacle,parent.getSw());
            }
            parent.setSe(new Node(x+3*w/2, y+w/2,w, parent, NodeType.LEAF_EMPTY));
            count_++;
            if(obstacle.intersect(parent.getSe())){
                parent.getSe().setNodeType(NodeType.LEAF_OBSTRUCTED);
                subdivide(obstacle,parent.getSe());
            }
            parent.setNw(new Node(x+w/2, y+3*w/2, w , parent, NodeType.LEAF_EMPTY));
            count_++;
            if(obstacle.intersect(parent.getNw())){
                parent.getNw().setNodeType(NodeType.LEAF_OBSTRUCTED);
                subdivide(obstacle,parent.getNw());
            }
            parent.setNe(new Node(x+3*w/2,y+3*w/2, w, parent, NodeType.LEAF_EMPTY));
            count_++;
            if(obstacle.intersect(parent.getNe())){
                parent.getNe().setNodeType(NodeType.LEAF_OBSTRUCTED);
                subdivide(obstacle,parent.getNe());
            }
        }
        else {parent.setNodeType(NodeType.LEAF_OBSTRUCTED);}

    }

    /**
     * Gets the value of the point at (x, y) or null if the point is empty.
     *
     * @param {double} x The x-coordinate.
     * @param {double} y The y-coordinate.
     * @param {Object} opt_default The default value to return if the node doesn't
     *                 exist.
     * @return {*} The value of the node, the default value if the node
     *         doesn't exist, or undefined if the node doesn't exist and no default
     *         has been provided.
     */
    public Object get(float x, float y, Object opt_default) {
        Node node = this.find(this.root_, x, y);
        return node != null ? node: opt_default;
    }

    /**
     * Removes a point from (x, y) if it exists.
     *
     * @param {double} x The x-coordinate.
     * @param {double} y The y-coordinate.
     * @return {Object} The value of the node that was removed, or null if the
     *         node doesn't exist.
     */

    public Node remove(float x, float y) {
        Node node = this.find(this.root_, x, y);
        if (node != null) {
            node.setNodeType(NodeType.EMPTY);
            this.balance(node);
            this.count_--;
            return node;
        } else {
            return null;
        }
    }

    /**
     * Finds the node containing the coordinate x,y
     * null if no point exists.
     * @param {QuadTree.Node} node The node to search in.
     * @param {number} x The x-coordinate of the point to search for.
     * @param {number} y The y-coordinate of the point to search for.
     * @return {QuadTree.Node} The leaf node that matches the target,
     *     or null if it doesn't exist.
     * @private
     */
    public Node find(Node node, float x, float y) {
        Node resposne = null;
        switch (node.getNodeType()) {
            case EMPTY:
                break;
            case LEAF_EMPTY:
                resposne = (node.contain(x,y)) ? node : null;
                break;
            case LEAF_OBSTRUCTED:
                break;
            case POINTER:
                resposne = this.find(this.getQuadrantForPoint(node, x, y), x, y);
                break;

            default:
                throw new QuadTreeException("Invalid nodeType");
        }
        return resposne;
    }


    /**
     * Returns true if the point at (x, y) exists in the tree.
     *
     * @param {double} x The x-coordinate.
     * @param {double} y The y-coordinate.
     * @return {boolean} Whether the tree contains a point at (x, y).
     */
    public boolean contains(float x, float y) {
        return this.get(x, y, null) != null;
    }

    /**
     * @return {boolean} Whether the tree is empty.
     */
    public boolean isEmpty() {
        return this.root_.getNodeType() == NodeType.EMPTY;
    }

    /**
     * @return {number} The number of items in the tree.
     */
    public int getCount() {
        return this.count_;
    }

    /**
     * Removes all items from the tree.
     */
    public void clear() {
        this.root_.setNw(null);
        this.root_.setNe(null);
        this.root_.setSw(null);
        this.root_.setSe(null);
        this.root_.setNodeType(NodeType.EMPTY);
        this.count_ = 0;
    }

    /**
     * Naviguate through the nodes inside of a specific rectangle
     */
    public void navigate(Node node, Func func, float xmin, float ymin, float width, float height) {

        if(node==null){
            return;
        }

        switch (node.getNodeType()) {
            case LEAF_EMPTY :
                func.call(this, node);
                break;
            case LEAF_OBSTRUCTED:
                func.call(this,node);
                break;
            case POINTER:
                if (node.getNe().RectIntersects(xmin, ymin, width, height))
                    this.navigate(node.getNe(), func,xmin, ymin, width, height);
                if (node.getSe().RectIntersects(xmin, ymin, width, height))
                    this.navigate(node.getSe(), func, xmin, ymin, width, height);
                if (node.getSw().RectIntersects(xmin, ymin, width, height))
                    this.navigate(node.getSw(), func, xmin, ymin, width, height);
                if (node.getNw().RectIntersects(xmin, ymin, width, height))
                    this.navigate(node.getNw(), func,xmin, ymin, width, height);
                break;
        }
    }

    /**
     * Return true if a the given ligne founded an intersection with an OBSTRUCTED_LEAF
     * Can be optimised
     */
    public boolean LOS(Node node, Func func, float x1, float y1, float x2, float y2){

        boolean result = true;

        if(node==null){
            return false;
        }

        switch (node.getNodeType()) {
            case LEAF_EMPTY :
                break;
            case LEAF_OBSTRUCTED:
                if(node.LigneIntersects(x1,y1,x2,y2)){
                    func.call(this, node);
                    result = false;
                }
                break;
            case POINTER:

                float width = x1 - x2;
                float height = y1 - y2;
                float x = (width<0)?x1:x2;
                float y = (height<0)?y1:y2;

                if (node.getNe().RectIntersects(x,y,Math.abs(width),Math.abs(height))){result = this.LOS(node.getNe(), func, x1, y1, x2, y2);}
                if(!result){break;}
                if (node.getSe().RectIntersects(x,y,Math.abs(width),Math.abs(height))){result = this.LOS(node.getSe(), func, x1, y1, x2, y2);}
                if(!result){break;}
                if (node.getSw().RectIntersects(x,y,Math.abs(width),Math.abs(height))){result = this.LOS(node.getSw(), func, x1, y1, x2, y2);}
                if(!result){break;}
                if (node.getNw().RectIntersects(x,y,Math.abs(width),Math.abs(height))){result = this.LOS(node.getNw(), func, x1, y1, x2, y2);}
                break;
        }
        return result;
    }

    /**
     * Traverses the tree depth-first, with quadrants being traversed in clockwise
     * order (NE, SE, SW, NW).  The provided function will be called for each
     * leaf node that is encountered.
     * @param {QuadTree.Node} node The current node.
     * @param {function(QuadTree.Node)} fn The function to call
     *     for each leaf node. This function takes the node as an argument, and its
     *     return value is irrelevant.
     * @private
     */
    public void traverse(Node node, Func func) {

        switch (node.getNodeType()) {
            case LEAF_EMPTY:
                func.call(this, node);
                break;
            case POINTER:
                this.traverse(node.getNe(), func);
                this.traverse(node.getSe(), func);
                this.traverse(node.getSw(), func);
                this.traverse(node.getNw(), func);
                break;
        }
    }


    /**
     * Attempts to balance a node. A node will need balancing if all its children
     * are empty or it contains just one leaf.
     * @param {QuadTree.Node} node The node to balance.
     * @private
     */
    private void balance(Node node) {
        switch (node.getNodeType()) {
            case EMPTY:
            case LEAF_EMPTY:
                if (node.getParent() != null) {
                    this.balance(node.getParent());
                }
                break;
            case LEAF_OBSTRUCTED:
                if (node.getParent() != null) {
                    this.balance(node.getParent());
                }
                break;

            case POINTER: {
                Node nw = node.getNw();
                Node ne = node.getNe();
                Node sw = node.getSw();
                Node se = node.getSe();
                Node firstLeaf = null;

                // Look for the first non-empty child, if there is more than one then we
                // break as this node can't be balanced.
                if (nw.getNodeType() != NodeType.EMPTY) {
                    firstLeaf = nw;
                }
                if (ne.getNodeType() != NodeType.EMPTY) {
                    if (firstLeaf != null) {
                        break;
                    }
                    firstLeaf = ne;
                }
                if (sw.getNodeType() != NodeType.EMPTY) {
                    if (firstLeaf != null) {
                        break;
                    }
                    firstLeaf = sw;
                }
                if (se.getNodeType() != NodeType.EMPTY) {
                    if (firstLeaf != null) {
                        break;
                    }
                    firstLeaf = se;
                }

                if (firstLeaf == null) {
                    // All child nodes are empty: so make this node empty.
                    node.setNodeType(NodeType.EMPTY);
                    node.setNw(null);
                    node.setNe(null);
                    node.setSw(null);
                    node.setSe(null);

                } else if (firstLeaf.getNodeType() == NodeType.POINTER) {
                    // Only child was a pointer, therefore we can't rebalance.
                    break;

                } else {
                    // Only child was a leaf: so update node's point and make it a leaf.
                    node.setNodeType(NodeType.LEAF_EMPTY);
                    node.setNw(null);
                    node.setNe(null);
                    node.setSw(null);
                    node.setSe(null);
                }

                // Try and balance the parent as well.
                if (node.getParent() != null) {
                    this.balance(node.getParent());
                }
            }
            break;
        }
    }

    /**
     * Returns the child quadrant within a node that contains the given (x, y)
     * coordinate.
     * @param {QuadTree.Node} parent The node.
     * @param {number} x The x-coordinate to look for.
     * @param {number} y The y-coordinate to look for.
     * @return {QuadTree.Node} The child quadrant that contains the
     *     point.
     * @private
     */
    private Node getQuadrantForPoint(Node parent, double x, double y) {
        if (x < parent.getCenterX()) {
            return y < parent.getCenterY() ? parent.getSw() : parent.getNw();
        } else {
            return y < parent.getCenterY() ? parent.getSe() : parent.getNe();
        }
    }



}

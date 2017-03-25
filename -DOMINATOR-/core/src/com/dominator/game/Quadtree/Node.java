package com.dominator.game.Quadtree;

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

    public boolean CircleIntesects(float CircleX, float CircleY, float CircleRadius){

        float X = getCenterX() - getW()/2;
        float Y = getCenterY() - getW()/2;
        float Width = getW();

        float DeltaX = CircleX - Math.max(X, Math.min(CircleX, X + Width));
        float DeltaY = CircleY - Math.max(Y, Math.min(CircleY, Y + Width));

        return (DeltaX * DeltaX + DeltaY * DeltaY) < (CircleRadius * CircleRadius);

    }

    // Node intersect Rectangle
    public boolean RectIntersects(float x, float y, float width, float height) {

        double x0 = getCenterX() - getW()/2;
        double y0 = getCenterY() - getW()/2;

        return (x + width > x0 &&
                y + height > y0 &&
                x < x0 + getW() &&
                y < y0 + getW());

    }

    // Node intersect Ligne
    public boolean LigneIntersects(float x1, float y1, float x2, float y2){


        float X = getCenterX() - getW()/2;
        float Y = getCenterY() - getW()/2;
        float Width = getW();

        return LineIntersectsLine(x1,y1,x2,y2, X,Y, X + Width, Y) ||
                LineIntersectsLine(x1,y1,x2,y2, X + Width, Y, X + Width, Y + Width) ||
                LineIntersectsLine(x1,y1,x2,y2, X + Width, Y + Width, X, Y + Width) ||
                LineIntersectsLine(x1,y1,x2,y2, X, Y + Width, X,Y) ||
                (contain(x1,y1) && contain(x2,y2));
    }

    private static boolean LineIntersectsLine(float x1, float y1, float x2,float y2,float x3,float y3, float x4, float y4)
    {

        return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                && (relativeCCW(x3, y3, x4, y4, x1, y1) *
                relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    public boolean contain(float x, float y) {
        return !(x>getCenterX()+getW()/2 || y>getCenterY()+getW()/2 || x<getCenterX()-getW()/2 || y<getCenterY()-getW()/2);
    }

    private boolean contain(Point p) {
        return contain(p.x,p.y);
    }

    public static int relativeCCW(double x1, double y1,
                                  double x2, double y2,
                                  double px, double py)
    {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on.  We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,y2
                // x2 and y2 are simply negated.
                // px and py need to have (x2 - x1) or (y2 - y1) subtracted
                //    from them (based on the original values)
                // Since we really want to get a positive answer when the
                //    point is "beyond (x2,y2)", then we want to calculate
                //    the inverse anyway - thus we leave x2 & y2 negated.
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    private boolean onSegment(Point p, Point q, Point r)
    {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;

        return false;
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

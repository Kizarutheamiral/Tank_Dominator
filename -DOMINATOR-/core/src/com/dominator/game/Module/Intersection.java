package com.dominator.game.Module;

import com.dominator.game.Quadtree.Point;

/**
 * Created by Choujaa Wassil on 27/03/2017.
 */
public class Intersection {

    public static boolean RectIntersects(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2) {

        double x0 = x2 - width2;
        double y0 = y2 - height2;


  //      System.out.println(x1+" "+y1+" "+width1+" "+height1+" "+x2+" "+y2+" "+width2+" "+height2+" ");
        return (x1 + width1 > x0 &&
                y1 + height1 > y0 &&
                x1 < x0 + width2 &&
                y1 < y0 + height2);

    }

    // Node intersect Ligne
    public static boolean LigneIntersects(float x1, float y1, float x2, float y2, float X, float Y, float Width , float Height){

        return LineIntersectsLine(x1,y1,x2,y2, X,Y, X + Width, Y) ||
                LineIntersectsLine(x1,y1,x2,y2, X + Width, Y, X + Width, Y + Height) ||
                LineIntersectsLine(x1,y1,x2,y2, X + Width, Y + Width, X, Y + Height) ||
                LineIntersectsLine(x1,y1,x2,y2, X, Y + Width, X,Y) ||
                (contain(x1,y1,X,Y,Width,Height) && contain(x2,y2,X,Y,Width,Height));
    }

    private static boolean LineIntersectsLine(float x1, float y1, float x2,float y2,float x3,float y3, float x4, float y4)
    {

        return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                && (relativeCCW(x3, y3, x4, y4, x1, y1) *
                relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    // Rectangle X, Y contain point x,y ?
    public static boolean contain(float x, float y, float X, float Y, float Width, float Height) {
        return !(x>=X+Width || y>=Y+Height || x<=X || y<=Y);
    }

    private static boolean contain(Point p,float X, float Y, float Width, float Height) {
        return contain(p.x,p.y,X,Y, Width, Height);
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

    private static boolean onSegment(Point p, Point q, Point r)
    {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;

        return false;
    }

}

/**
 * Created by Choujaa Wassil on 03/02/2017.
 */
public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector normalise(){
        double norm = Math.sqrt(x*x+y*y);
        return new Vector((x/norm),(y/norm));
    }

}

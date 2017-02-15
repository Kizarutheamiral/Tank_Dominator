/**
 * Created by Choujaa Wassil on 05/02/2017.
 */
public class Particule {
    int x;
    int y;
    int velocity;
    int aceleration;
    int mass;

    public Particule(int x, int y) {
        this.x = x;
        this.y = y;
        mass=1;
        aceleration=0;
        velocity=0;
    }

    public void move(Vector[][] field){
        int x = (int) this.x/20;
        int y = (int) this.y/20;
        if(field[y][x]==null){
            System.out.println("return");
            return;
        }
        Vector direction = field[y][x];
        this.x+=direction.x*2;
        this.y+=direction.y*2;
        //System.out.println(direction.x+" "+direction.y);
    }


}

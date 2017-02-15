import java.util.Arrays;

/**
 * Created by Choujaa Wassil on 02/02/2017.
 *
 *
 */

public class Game implements Runnable {
    Vector[][] matrix;
    public InputHandler controller;
    private boolean gaming = true;
    Particule p;
    public View window;
    public FlowFieldGenerator generator;


    int x_offset=0;
    int y_offset=0;
    public Game() {
        // les chemins sont des 0 les murs sont des 1
        int[][] matrix = new int[50][50];
        for (int i = 0; i < 20; i++) {
            matrix[i+3][10] = 1;
        }


        for (int i = 0; i < 50; i++) {
            matrix[2][i] = 1;
        }

        for (int i = 20; i < 50; i++) {

            matrix[40][i] = 1;
            matrix[41][i] = 1;
            if(i>40){
                continue;
            }
            matrix[20][i] = 1;
            matrix[21][i] = 1;
            matrix[i][20]=1;
            matrix[i][21]=1;

        }

        p = new Particule(60,50);
        controller = new InputHandler(this);
        window = new View(controller,p,this);
        generator = new FlowFieldGenerator(matrix, window);
        new Thread(this).start();

    }

    @Override
    public void run() {

        while (gaming){
            //System.out.println(gaming);
            window.update();
            if(matrix!=null){
                p.move(matrix);
            }
            try {
                Thread.sleep(28);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setGaming(boolean gaming) {
        System.out.println(gaming);
        this.gaming = gaming;
    }

    public void setMatrix(Vector[][] matrix) {
        this.matrix = matrix;
    }
}

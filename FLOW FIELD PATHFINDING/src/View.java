import javax.swing.*;
import java.awt.*;

/**
 * Created by Choujaa Wassil on 03/02/2017.
 */
public class View extends JFrame{

    Vector[][] field;
    double[][] matrix;
    int[][] heat_Map;
    Game gem;
    Particule p;
    public View(InputHandler controller, Particule p, Game g) {
        addKeyListener(controller);
        addMouseListener(controller);
        setTitle("Square Move Practice");
        setResizable(false);
        setSize(600, 600);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(200,100);
        Panel panel = new Panel();
        add(panel);
        pack();
        setVisible(true);
        this.p = p;
        this.gem = g;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public void setField(Vector[][] field) {
        this.field = field;
    }

    public void update(){
        repaint();
    }

    private class Panel extends JPanel{

        int k = 20;
        @Override
        public void paintComponent(Graphics g ){

            super.paintComponent(g);

            if (matrix!=null){
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        g.setColor(Color.BLACK);
                        String s = Double.toString(matrix[i][j]).substring(0,3);
                        //g.drawString(s,j*k,i*k);
                        g.setColor(new Color((int)(255-2.1*matrix[i][j]),(int)(255-2.1*matrix[i][j]),(int)(255-2*matrix[i][j])));
                        g.fillRect(j*k + gem.x_offset,i*k+gem.y_offset,k,k);
                    }
                }
            }

            if(field!=null){
                for (int y = 0; y < field.length; y++) {
                    for (int x = 0; x < field[0].length; x++) {
                        g.drawRect(x*k + gem.x_offset,y*k  + gem.y_offset,k,k);
                        g.setColor(Color.BLACK);
                        if(field[y][x] != null){
                            Vector v = field[y][x];
                            double norm = Math.sqrt(v.x*v.x+v.y*v.y);
                            //Vector normalize = new Vector((int) (v.x/norm),(int)(v.y/norm));
                            g.drawLine( k*x+k/2 +gem.x_offset, k*y +k/2 + gem.y_offset, (int)(k*x+k/2+v.x*2) + gem.x_offset,(int)( k*y + k/2+v.y*2) + gem.y_offset);
                        }
                        else {
                            g.fillRect(x*k + gem.x_offset,y*k + gem.y_offset,k,k);
                        }
                    }
                }
            }

            if(p!=null){
                g.setColor(Color.orange);
                g.fillOval(p.x + gem.x_offset,p.y+gem.y_offset,10,10);
            }
        }
    }
}

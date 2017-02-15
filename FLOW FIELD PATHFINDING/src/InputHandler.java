import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Choujaa Wassil on 03/02/2017.
 */
public class InputHandler implements MouseListener, KeyListener {
    private Game game;
    public InputHandler(Game game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x =  (e.getX() - game.x_offset)/20;
        int y =  (e.getY() - game.y_offset)/20;
        //System.out.println(x+ " "+y);
        if(x<100 && y<100){
            Vector[][] m =  game.generator.generateFlowFieldFrom(x,y);
            game.window.setField(m);
            game.setMatrix(m);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {


    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            game.setGaming(false);
        } else if(e.getKeyCode() == KeyEvent.VK_Q){
            game.x_offset-=10;
        } else if(e.getKeyCode() == KeyEvent.VK_Z){
            game.y_offset-=10;
        } else if(e.getKeyCode() == KeyEvent.VK_D){
            game.x_offset+=10;
        } else if(e.getKeyCode() == KeyEvent.VK_S){
            game.y_offset+=10;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

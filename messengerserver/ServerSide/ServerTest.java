package messengerserver.ServerSide;
import javax.swing.JFrame;

public class ServerTest {
    public static void main(String[] args){
        Server elena = new Server();
        elena.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        elena.startRunning();
    }    
}

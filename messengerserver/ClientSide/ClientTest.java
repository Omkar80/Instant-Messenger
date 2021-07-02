package messengerserver.ClientSide;

import javax.swing.JFrame;

public class ClientTest {
    public static void main(String[] args){
        Client budd;
        budd = new Client("127.0.0.1");
        budd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        budd.startRunning();
    }
}

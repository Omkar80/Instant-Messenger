package messengerserver.ClientSide;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
    private JTextField user_text;
    private JTextArea chat_window;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host){
        super("Client!");
        serverIP = host;
        user_text = new JTextField();
        user_text.setEditable(false);
        user_text.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    user_text.setText("");
                }
            }
        );
        add(user_text, BorderLayout.NORTH);
        chat_window = new JTextArea();
        add(new JScrollPane(chat_window), BorderLayout.CENTER);
        setSize(400, 200);
        setVisible(true);
    }

    //connect to server
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client terminated connection");
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally{
            closeCrap();
        }
    }

    //connect to 
    private void connectToServer() throws IOException{
        showMessage("Attempting to connect.... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 7867);
        showMessage("Connected to : " + connection.getInetAddress().getHostName());
    }

    //set up streams to send and recieve messages
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n The streams are good to go! \n");
    }

    //while chatting with server
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String)input.readObject();
                showMessage("\n" + message);

            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Invalid \n");
            }
        }while(!message.equals("SERVER - END"));
    }

    //close the streams and sockets
    private void closeCrap(){
        showMessage("Closing the connection....");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //send messages to server
    private void sendMessage(String message){
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\n CLIENT - " + message);
        }catch(IOException ioException){
            chat_window.append("\n Message not sent!");
        }
    }

    //update chat window
    private void showMessage(final String text){    //so that the message does not change
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    chat_window.append(text);       //updating the chat window with new messages
                }
            }
        );
    }

    //permission to type text
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    user_text.setEditable(tof);
                }
            }
        );
    }
}

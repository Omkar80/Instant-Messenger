package messengerserver.ServerSide;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    private JTextField user_text;
    private JTextArea chat_window;
    private ObjectOutputStream output;      //output text package going to other user
    private ObjectInputStream input;        //input text package coming to us
    private ServerSocket server;        //creation of server
    private Socket connection;          //connection between two computers

    //constructor
    public Server(){
        super("Instant Messenger");
        user_text = new JTextField();
        user_text.setEditable(false);   //for the case when there is no user on other side
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
        add(new JScrollPane(chat_window));
        setSize(400, 200);
        setVisible(true);
    }
    
    //set and run server
    public void startRunning(){
        try{
            server = new ServerSocket(7867, 100);    //(port number, number of people allowed to wait on the server)
            while(true){
                try{
                    //connect and have conversationq
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage(" Server ended the connection! ");      //when a user ends the chat connection.
                }finally{
                    closeCrap();                //close
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException{
        showMessage(" Waiting for someone to connect ....:) \n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }

    //get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are setup now... you can now start chatting! :) \n");
    }

    //during the chat conversation
    private void whileChatting() throws IOException{
        String message = "\n You are now connected! \n";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String)input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Invalid Input \n");
            }
        }while(!message.equals("CLIENT - END"));
    }

    //close streams and sockets after chatting
    private void closeCrap(){
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try{
            output.close();     //closing output stream
            input.close();      //closing input stream 
            connection.close();  //closing connection
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //send a message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();     //make sure nothing is left bufferinng in the stream
            showMessage("\nSERVER - " + message);
        }catch(IOException ioException){
            chat_window.append("\n ERROR: Message not sent \n");
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

    //user text
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

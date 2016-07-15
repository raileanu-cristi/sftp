/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 *
 * @author Cip
 */
public class Sftp_client
{   
    static String ok= "[ok] ";
    static String er= "[!] ";
    static String pend= "[...] ";
    static String path= "";
    
    // Returning null if message format is incorrect
    public static Message parseMessage(String msgText)
    {
        final String[] tokens = msgText.split(" ");
        Message msg = new Message();
        
        if (tokens[0].compareTo("disconnect") == 0) { msg.mType = Message.MsgType.MSG_DISCONNECT; }
        else if (tokens[0].compareTo("upload") == 0)
        {
            msg.mType = Message.MsgType.MSG_UPLOAD;
            msg.filename= tokens[1];
        }
        else if (tokens[0].compareTo("setpath")==0)
        {
            path= tokens[1];
            return null;
        }
        return msg;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        String serverAddress= "localhost";
        int port= 9090;
        
        if (args.length >= 2)
        {
            serverAddress= args[0];
            port= Integer.parseInt(args[1]);
        }
        
        System.out.println(pend+"connecting to "+ serverAddress + ":" + port+"...");
        
        try
        {
            Socket clientSocket = new Socket(serverAddress, port);
            
            ObjectOutputStream streamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream streamFromServer = new ObjectInputStream(clientSocket.getInputStream());
            
            System.out.println(ok+"Connected!");
            
            Scanner scan = new Scanner(System.in);
            while(true)
            {
                // Read a message from client
                System.out.print(">> ");
                String msgText = scan.nextLine();
                
                Message msg = parseMessage(msgText);
                if (msg == null)
                {
                    System.out.println(ok+"path changed! ");
                    continue;
                }
                
                if (msg.mType == Message.MsgType.MSG_INVALID)
                {
                    System.out.println(er+"Incorrect message format, try again");
                }
                else
                {                   
                    // Writting message to server
                    
                    System.out.println(pend+"Send request to server");
                    streamToServer.writeObject(msg);
                    
                    System.out.println(pend+"Wait for server response");
                    System.out.println(ok+"[Server]: "+ (String) streamFromServer.readObject() );
                    
                    
                    
                    if (msg.mType == Message.MsgType.MSG_DISCONNECT)    // Normally we should wait for disconnect confirm for server but this is fine too.
                    {
                        System.out.println(ok+"disconnecting...");
                        break;
                    }
                    
                    // Waiting for his answer
                    // NOTICE that we can't get any request from client console before we receive the answer :(
                    // In the next app we'll use threads to fix this !
                    
                    if (msg.mType == Message.MsgType.MSG_UPLOAD)    // Normally we should wait for disconnect confirm for server but this is fine too.
                    {             
                        System.out.println(pend+"Uploading file "+path+ msg.filename);
                        
                        FileInputStream file= new FileInputStream(path+msg.filename);
                        
                        BufferMessage bf= new BufferMessage();
                        
                        int nr= file.read(bf.getBuffer(), 0, bf.max_size-1);
                        
                        while (nr != -1)
                        {
                            
                            bf.setSize(nr);
                            
                            System.out.println(pend+"Sending packet... ");
                            streamToServer.writeObject(bf);
                            
                            System.out.println(ok+"Packet sent! ["+bf.getSize()+" bytes]");
                            
                            System.out.println(ok+"[Server]: "+ (String) streamFromServer.readObject() );
                            
                            nr= file.read(bf.getBuffer(), 0, bf.max_size-1);
                        }
                        streamToServer.writeObject(null);
                        System.out.println(ok+"file uploaded! ");
                        
                        file.close();
                    }
                    
                }
            }
            
                       
            //JOptionPane.showMessageDialog(null, answer);
        }
        catch (Exception e)
        {
            System.out.println(er+"Error! "+ e.getClass());
        }
    }    
}
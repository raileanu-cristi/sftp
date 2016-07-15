/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Cristi
 */
public class Fir extends Thread
{
    Socket mSocket;
    String mPath;
    
    public Fir(Socket s, String path)
    {
        mSocket= s;
        
        if (path != null)
            mPath= path;
        else 
        {
            mPath= "";
            return;
        }
        
        File dir = new File(mPath);
        if (!dir.exists()) 
        {
            if (!dir.mkdir()) 
                System.out.println("Download directory is created!");
            else 
                System.out.println("Failed to create directory!");
            
        }
        
    }
    
    public void run()
    {
        try 
        {
            ObjectOutputStream streamToClient = new ObjectOutputStream(mSocket.getOutputStream());
            ObjectInputStream streamFromClient = new ObjectInputStream(mSocket.getInputStream()); 
            
            System.out.println("S-au creat object i/o! ");
            
            boolean stillConnected = true;
            
            //  While loop
            while(stillConnected)   
            {         
                System.out.println("wait for client requests");
                Message msg = (Message) streamFromClient.readObject();
                
                System.out.println("request received! Sending ack");
                streamToClient.writeObject(new String("ack"));
                
                switch(msg.mType)
                {
                    case MSG_DISCONNECT:
                    {
                        System.out.println("client got disconnected..");                        
                        stillConnected = false;
                        mSocket.close();
                    }
                    break;
                    case MSG_UPLOAD:
                    {                                               
                        System.out.println("client requested upload. Uploading...");
                        
                        
                        
                        FileOutputStream file= new FileOutputStream(mPath + msg.filename);
                        
                        BufferMessage bm= (BufferMessage) streamFromClient.readObject();
                        
                        int packet_nr=0;
                        
                        while (bm!= null)
                        {
                            String ack= "Packet nr "+packet_nr+" uploaded!";
                            
                            streamToClient.writeObject(ack);
                            
                            file.write(bm.getBuffer(), 0, bm.getSize());
                            
                            bm= (BufferMessage) streamFromClient.readObject();
                        }
                        
                        file.close();
                        System.out.println(msg.filename+ " has been uploaded successfully");
                        streamToClient.writeObject(new String("Uploaded!"));
                    } 
                    break;
                   
                }                      
                if (msg.mType != Message.MsgType.MSG_DISCONNECT)
                    streamToClient.writeObject(new String("You are being disconnected!"));
                
            }            
        }
        catch(Exception e)
        {
            System.out.println("Client deconectat! "+ e.getClass());
            this.interrupt();
        }        
    }
}

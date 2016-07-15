/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Cristi
 */
public class Sftp_server 
{

    /**
     * @param args the command line arguments
     */
    
    static int nr_clients= 0;
    static Fir[] client= new Fir[1000];
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
       

        int port=9090;
        String path;
        
        if ( args.length < 1 )
            System.err.println("Port was not given. Using default port 9090");
        else
            port= Integer.parseInt(args[0]);
        
        if ( args.length >= 2 )
            path= args[1];
        else
            path = "SftpDownloads\\";
            
	System.out.println("The default folder is: "+ path);
        
        try 
        {
	    System.out.println("Starting server on port "+port);
            ServerSocket listener = new ServerSocket(port);
            
            while(true)
            {
                System.out.println("Waiting for a client to connect...");
                Socket socket = listener.accept();
                
                System.out.println("client "+socket.getLocalAddress());
                // TODO: notice that we can support only one client at a time...in the next app we'll use threads to solve this
                client[++nr_clients]= new Fir(socket, path);
                client[nr_clients].start();
                
                //while (cons.hasMessages())
                //    parseMessage(cons.pop_front());
            }           
        }
        catch(Exception e)
        {
            System.out.println("Eroare! "+e.getMessage());      
        }
    }
    
}

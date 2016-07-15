/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;

/**
 *
 * @author Cristi
 */
public class Message implements Serializable
{
    public int mN;
    public String filename;
    public MsgType mType; 
    
    public enum MsgType { MSG_INVALID, MSG_DISCONNECT, MSG_UPLOAD };
    
       
    
   Message() { mType = MsgType.MSG_INVALID; mN = 0; }
    
    // TODO: as you can see ideally we should have a base class for message then multiple derived class depending on message type
    
}

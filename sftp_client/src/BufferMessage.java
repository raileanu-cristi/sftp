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
public class BufferMessage implements Serializable
{
    public static int max_size = 4000100;
    
    int mSize;
    byte[] mBuffer;
    
    public BufferMessage()
    {
        mSize= 0;
        mBuffer= new byte[max_size];
    }
    
    public byte[] getBuffer() 
    {
        return mBuffer;
    }
    
    public void setSize(int n)
    {
        mSize= n;
    }
    
    public int getSize()
    {
        return mSize;
    }
}

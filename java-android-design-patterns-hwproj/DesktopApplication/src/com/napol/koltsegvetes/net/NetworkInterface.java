package com.napol.koltsegvetes.net;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class NetworkInterface implements Runnable
{
    @Override
    public void run()
    {
        try
        {
            ServerSocket socket = new ServerSocket(8888);
            socket.setSoTimeout(50);

            boolean ok = false;
            while (!ok)
            {
                Socket s;
                try
                {
                    s = socket.accept();
                }
                catch (SocketTimeoutException e1)
                {
                    continue;
                }

                ObjectInput in = new ObjectInputStream(s.getInputStream());
                ArrayList<?> obj = (ArrayList<?>) in.readObject();

                for (Object e : obj)
                {
                    System.out.println("Element = " + e);
                }

                s.close();
                ok = true;
            }
            
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

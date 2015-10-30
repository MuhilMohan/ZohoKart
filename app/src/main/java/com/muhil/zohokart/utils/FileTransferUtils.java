package com.muhil.zohokart.utils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class FileTransferUtils
{
    public static void copyStream(InputStream inputStream, OutputStream outputStream)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            while (true)
            {
                int count = inputStream.read(bytes, 0, buffer_size);
                if (count == -1)
                {
                    break;
                }
                outputStream.write(bytes, 0, count);
            }
        }
        catch (Exception e)
        {

        }
    }
}

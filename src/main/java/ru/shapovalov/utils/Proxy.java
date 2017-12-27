package ru.shapovalov.utils;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class Proxy {
    private Socket sClient;
    private final String SERVER_URL;
    private final int SERVER_PORT;

    public Proxy(Socket sClient, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        run();
    }

    public void run() {
        try {
            byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final ReadableByteChannel inFromClient = Channels.newChannel(sClient.getInputStream());
            final WritableByteChannel outToClient = Channels.newChannel(sClient.getOutputStream());


            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                outToClient.close();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final ReadableByteChannel inFromServer = Channels.newChannel(server.getInputStream());
            final WritableByteChannel outToServer = Channels.newChannel(server.getOutputStream());
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = inFromClient.read(ByteBuffer.wrap(request))) != -1) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(request, 0, bytes_read);
                            outToServer.write(byteBuffer);
                            //TODO CREATE YOUR LOGIC HERE
                        }
                    } catch (IOException e) {
                    }
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(ByteBuffer.wrap(reply))) != -1) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(reply, 0, bytes_read);
                    outToClient.write(byteBuffer);

                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.electric_water_heaters_controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by RED on 2015/11/6.
 */
public class Server {
    //�������˿�
    private static final int SERVERPORT = 54321;
    //�ͻ�������
    private static List<Socket> mClientList = new ArrayList<Socket>();
    //�̳߳�
    private ExecutorService mExecutorService;
    //ServerSocket����
    private ServerSocket mServerSocket;
    //����������
    public static void main(String[] args)
    {
        new Server();
    }
    public Server()
    {
        try
        {
            //���÷������˿�
            mServerSocket = new ServerSocket(SERVERPORT);
            //����һ���̳߳�
            mExecutorService = Executors.newCachedThreadPool();
            System.out.println("start...");
            //������ʱ����ͻ������ӵ�Socket����
            Socket client = null;
            while (true)
            {
                //���տͻ����Ӳ���ӵ�list��
                client = mServerSocket.accept();
                mClientList.add(client);
                //����һ���ͻ����߳�
                mExecutorService.execute(new ThreadServer(client));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //ÿ���ͻ��˵�������һ���߳�
    static class ThreadServer implements Runnable
    {
        private Socket			mSocket;
        private BufferedReader mBufferedReader;
        private PrintWriter mPrintWriter;
        private String			mStrMSG;

        public ThreadServer(Socket socket) throws IOException
        {
            this.mSocket = socket;
            mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            mStrMSG = "user:"+this.mSocket.getInetAddress()+" come total:" + mClientList.size();
            sendMessage();
        }
        public void run()
        {
            try
            {
                while ((mStrMSG = mBufferedReader.readLine()) != null)
                {
                    if (mStrMSG.trim().equals("exit"))
                    {
                        //��һ���ͻ����˳�ʱ
                        mClientList.remove(mSocket);
                        mBufferedReader.close();
                        mPrintWriter.close();
                        mStrMSG = "user:"+this.mSocket.getInetAddress()+" exit total:" + mClientList.size();
                        mSocket.close();
                        sendMessage();
                        break;
                    }
                    else
                    {
                        mStrMSG = mSocket.getInetAddress() + ":" + mStrMSG.trim();
                        sendMessage();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //������Ϣ�����пͻ���
        private void sendMessage() throws IOException
        {
            System.out.println(mStrMSG);
            for (Socket client : mClientList)
            {
                mPrintWriter = new PrintWriter(client.getOutputStream(), true);
                mPrintWriter.println(mStrMSG);
            }
        }
    }
}

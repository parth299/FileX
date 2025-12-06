package p2p.service;

import p2p.utils.UploadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class FileSharer {
    private HashMap<Integer, String> availableFiles;

    public FileSharer() {
        availableFiles = new HashMap<>();
    }

    public int offerFiles(String filePath) {
        int port;
        while(true) {
            port = UploadUtils.generateCode();

            if(!availableFiles.containsKey(port)) {
                availableFiles.put(port, filePath);
                return port;
            }
        }
    }

    public void startFileServer(int port) {
        String filePath = availableFiles.get(port);

        if(filePath == null) {
            System.out.println("No files associated with the given port: " + port);
            return ;
        }

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serving file " + new File(filePath).getName() + " on port: " + port);
            Socket clientSocket = serverSocket.accept();

            System.out.println("Client connection: " + clientSocket.getInetAddress());
            new Thread(new FileSenderHandler(clientSocket, filePath)).start();
        }
        catch(IOException ex) {
            System.err.println("Error handling file server on port: " + port);
        }
    }

    private static class FileSenderHandler implements Runnable {
        private final Socket clientSocket;
        private final String filePath;

        public FileSenderHandler(Socket clientSocket, String filePath) {
            this.clientSocket = clientSocket;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try(FileInputStream fis = new FileInputStream(filePath)) {
                OutputStream os = clientSocket.getOutputStream();
                String filename = new File(filePath).getName();
                String header = "Filename: " + filename + "\n";
                os.write(header.getBytes());

                byte[] buffer = new byte[4096];
                int byteRead;
                while((byteRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }

                System.out.println("File: " + filename + " sent to " + clientSocket.getInetAddress());
            }
            catch(IOException ex) {
                System.err.println("Error sending file to client " + ex.getMessage());
            }
            finally {
                try {
                    clientSocket.close();
                }
                catch(Exception ex) {
                    System.err.println("error closing the client socket " + ex.getMessage());
                }
            }
        }
    }
}

package de.hamze;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class PortScanner {
    private static final File port = new File("port.txt");
    private static final File commonPort = new File("commonPorts.txt");
    private static final FileWriter fileWriter;

    static {
        try {
            fileWriter = new FileWriter(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        boolean connection;
        String input = "";

        // Re-Type after Connection failed to the targetted IP to avoid useless waiting
        do {
            try {
                System.out.print("Enter Target-IP: ");
                input = new Scanner(System.in).nextLine();
                InetAddress checkConnection = InetAddress.getByName(input);
                connection = checkConnection.isReachable(1000);
            } catch (UnknownHostException exception) {
                connection = false;
            }
        } while (!connection);


        // Calls the 'connectToServer' method to scan all open (common) ports. To be written in the port.txt file
        BufferedReader bufferedReader = new BufferedReader(new FileReader(commonPort));
        String finalInput = input;

        List<Integer> ports = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            ports.add(Integer.parseInt(line));
        }

        ports.forEach(port -> connectToServer(finalInput, port));
        fileWriter.close();
    }

    public static void connectToServer(String ip, int port) {
        if (port > 65536 || port < 0) {
            throw new IllegalArgumentException("Port has to be in range from 0 to 65536");
        }

        Socket socket = new Socket();

        // Creates file if not exists to save open ports
        if (!PortScanner.port.exists()) {
            try {
                PortScanner.port.createNewFile();
                System.out.println("File created: " + PortScanner.port.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            socket.connect(new InetSocketAddress(ip, port), 100);
            fileWriter.write(ip + ":" + socket.getPort() + "\n");
            System.out.print("Connection to " + socket.getInetAddress() + " to port " + socket.getPort() + " was successful!");
            socket.close();
        } catch (IOException e) {

        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * A runnable TCP TFTP client.
 * Supports: read/write requests and error handling.
 */
public class TCPClient implements Runnable {

    private Socket socket;

    private final int serverPort;
    private final InetAddress serverIP;

    /**
     * @throws IOException if an I/O error occurs.
     */
    public TCPClient() throws IOException {
        this(InetAddress.getLoopbackAddress(), 10000);
    }

    /**
     * @param serverIP The server's IP address.
     * @param serverPort The server's port.
     * @throws IOException if an I/O error occurs.
     */
    public TCPClient(InetAddress serverIP, int serverPort) throws IOException {
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        System.out.println("Server: " + serverIP.getHostAddress() + ":" + serverPort);
        newClient();
    }

    /**
     * Simple menu to communicate with the TFTP server.
     */
    @Override
    public void run() {
        String mainMenu = "1. Send File\n2. Retrieve File\n3. Quit";
        System.out.println(mainMenu);
        System.out.println();

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try {
            while (!(input = console.readLine().trim()).equals("3")) {
                if (input.equals("1") || input.equals("2")) {
                    System.out.println("Enter file name.");

                    String fileName = console.readLine().trim();

                    switch (input) {
                        case "1":
                            new WRQOperation(socket, fileName).run();
                            break;
                        case "2":
                            new RRQOperation(socket, fileName).run();
                            break;
                    }

                    System.out.println();
                    newClient();
                } else {
                    System.out.println("\nInvalid Option");
                }

                System.out.println(mainMenu);
                System.out.println();
            }

            System.out.println();
            System.out.println("Client Closed");
        } catch (IOException e) {
            if(e instanceof SocketException) {
                System.out.println("Server Closed.");
            } else {
                System.out.println("An error occurred, client is closing.");
            }
        }
    }

    /**
     * A new client socket for every request to the TFTP Server.
     * @throws IOException if an I/O error occurs.
     */
    public void newClient() throws IOException {
        socket = new Socket(serverIP, serverPort);
        System.out.println("Client: " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + "\n");
    }
}

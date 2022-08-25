import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Stack;

/**
 * A runnable TCP TFTP server.
 * Supports: simultaneous read/write requests and error handling.
 */
public class TFTPServer implements Runnable {

    private final ServerSocket serverSocket;
    private Stack<Socket> clientSockets = new Stack<>();
    private boolean isRunning = true;

    /**
     * Starts the server.
     * Default IP address is the loopback address/127.0.0.1.
     * Default port is 10000
     * @throws IOException if an I/O error occurs.
     */
    public TFTPServer() throws IOException {
        this(10000);
    }

    /**
     * @param port The port the TFTP server will run on.
     * @throws IOException if an I/O error occurs.
     */
    public TFTPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port, 50, InetAddress.getLoopbackAddress());

        System.out.println("Server: " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() + "\n");
        System.out.println("TFTP Server Started.");
        System.out.println("q - quit.\n");

        stopServer();
    }

    /**
     * Starts the TCP TFTP server and blocks until a request is received form the client.
     * A new thread is created to handle the request.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                clientSockets.push(serverSocket.accept());
                System.out.println("Accepted TCP connection from: " + clientSockets.peek().getInetAddress().getHostAddress() + ", " + clientSockets.peek().getPort() + "...");

                new Thread(() -> {
                    clientHandler(clientSockets.peek());
                }).start();
            }
        } catch (IOException e) {
            errorInfo(e);
        }
    }

    private void clientHandler(Socket clientSocket) {
        Packet initialRequest;

        try {
            initialRequest = DataHandler.getPacket(clientSocket.getInputStream());

            if (initialRequest instanceof ReqPacket) {
                ReqPacket reqPacket = ((ReqPacket) initialRequest);

                if (reqPacket.getPacketType().equals(Opcode.RRQ)) {
                    new RRQOperation(clientSocket, (ReqPacket) initialRequest).start();
                } else {
                    new WRQOperation(clientSocket, (ReqPacket) initialRequest).start();
                }
            }
        } catch (IOException e) {
            errorInfo(e);
        }
    }

    /**
     * The server will be closed if q is entered in the stdin.
     */
    private void stopServer() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

                    if ((console.readLine().trim().equals("q"))) {
                        serverSocket.close();

                        while(!clientSockets.empty()) {
                            if(clientSockets.peek().isClosed()) {
                                clientSockets.pop();
                            } else {
                                clientSockets.pop().close();
                            }
                        }

                        System.out.println("Server Closed");
                        isRunning = false;
                    }
                } catch (IOException e) {
                    errorInfo(e);
                }
            }
        }).start();
    }

    public void errorInfo(Exception e) {
        if (e instanceof SocketException) {
            if (!clientSockets.empty()) {
                System.out.println("Client disconnected: " + clientSockets.pop().getInetAddress().getHostAddress() + ", " + clientSockets.peek().getPort());
            }

//                this.run();
        } else {
            isRunning = false;
            System.out.println("An error has occurred server is closing.");
        }
    }
}

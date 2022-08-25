import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class represents the base class of a request.
 * This class is intended to be ran on its own thread.
 */
public class Request extends Thread {
    private final Socket clientSocket;
    private final String fileName;
    private int block = 0;

    /**
     * @param socket The socket that is connected to the client.
     * @param packet The initial request packet.
     */
    public Request(Socket socket, ReqPacket packet) {
        this.clientSocket = socket;
        this.fileName = packet.getFilename();
    }

    /**
     * Blocks until data is written to the server's output stream and then process the data.
     * @return A "packet" instance which represents the data sent to the server/written to output stream.
     * @throws IOException if an I/O error occurs.
     */
    public Packet receive() throws IOException {
        return DataHandler.getPacket(getInputStream());
    }

    /**
     * @param data The packet to be sent to the client.
     * @throws IOException if an I/O error occurs.
     */
    public void send(byte[] data) throws IOException {
//        System.out.println(clientSocket.getRemoteSocketAddress() + " " + clientSocket.getPort());

        getOutputStream().write(data);
    }

    /**
     * @return The current block number.
     */
    public int getBlock() { return block; }

    /**
     * Increments the block number by 1.
     */
    protected void incrementBlock() { block++; }

    /**
     * @return The name of the file which is being sent or retrieved.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        clientSocket.close();
    }

    /**
     * @return Gets the client's input stream.
     * @throws IOException if an I/O error occurs.
     */
    public InputStream getInputStream() throws IOException {
        return clientSocket.getInputStream();
    }

    /**
     * @return Gets the client's output stream.
     * @throws IOException if an I/O error occurs.
     */
    public OutputStream getOutputStream() throws IOException {
        return clientSocket.getOutputStream();
    }
}

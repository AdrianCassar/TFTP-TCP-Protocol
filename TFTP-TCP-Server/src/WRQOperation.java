import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to serve a write request
 * and is intended to be ran on a separate thread.
 */
public class WRQOperation extends Request {
    private DataPacket packet;
    private boolean isRunning;

    /**
     * @param socket The socket that is connected to the client.
     * @param packet The initial WRQ packet.
     * @throws IOException if an I/O error occurs.
     */
    public WRQOperation(Socket socket, ReqPacket packet) throws IOException {
        super(socket, packet);

        if (DataHandler.fileExists(this.getFileName())) {
            Files.delete(Paths.get(this.getFileName()));
        }

        this.incrementBlock();

        isRunning = true;
    }

    /**
     * Runs read request operation.
     */
    @Override
    public void run() {
        try {
            DataHandler.log("Received WRQ");

            do {
//                DataHandler.log("Data Block: " + this.getBlock());
                Packet packet = this.receive();

                if (packet instanceof DataPacket) {
                    this.packet = (DataPacket) packet;
                    DataHandler.saveFile(this.getFileName(), this.packet);

                    this.incrementBlock();
                } else if(packet instanceof ErrorPacket) {
                    System.out.println("Error: " + ((ErrorPacket) packet).getErrMsg());

                    isRunning = false;
                }
            } while (isRunning && !DataHandler.isLastPacket(packet.getLength()));

            if(isRunning) {
                DataHandler.log("Write Request Completed.\n");
            } else {
                DataHandler.log("Write Request Failed.\n");
            }

            try {
                this.close();
            } catch (IOException ioException) {
                System.out.println("Failed to close client.");
            }
        } catch (IOException e) {
            isRunning = false;
            DataHandler.log("Write Request Failed.\n");
        }
    }
}

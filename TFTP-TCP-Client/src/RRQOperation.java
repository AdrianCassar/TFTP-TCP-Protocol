import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to serve a read request.
 */
public class RRQOperation extends Request {
    private DataPacket packet;
    private boolean isRunning;

    /**
     * @param socket The socket that is connected to the server.
     * @param fileName The name of the file to be received from the server.
     * @throws IOException if an I/O error occurs.
     */
    public RRQOperation(Socket socket, String fileName) throws IOException {
        super(socket, fileName);

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
            DataHandler.log("Sent RRQ");
            this.send(DataHandler.req(Opcode.RRQ, this.getFileName()));

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
                DataHandler.log("Read Request Completed.\n");
            } else {
                DataHandler.log("Read Request Failed.\n");
            }

            try {
                this.close();
            } catch (IOException ioException) {
                System.out.println("Failed to close client.\n");
            }
        } catch (IOException e) {
            DataHandler.log("Write Request Failed.\n");
            isRunning = false;
        }
    }
}

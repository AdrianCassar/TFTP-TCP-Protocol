import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a request packet either RRQ or WRQ.
 */
public class ReqPacket extends Packet {

    private final String filename;
    private final String mode;

    /**
     * @param stream The input stream containing the WRQ or RRQ "packet".
     * @throws IOException if an I/O error occurs.
     */
    public ReqPacket(InputStream stream, Opcode packetType) throws IOException {
        super(stream);
        this.setPacketType(packetType);

        filename = readString();
        mode = readString();
    }

    /**
     * @return The name of the file which is being sent or retrieved.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return The mode which the packet was went in.
     */
    public String getMode() {
        return mode;
    }
}

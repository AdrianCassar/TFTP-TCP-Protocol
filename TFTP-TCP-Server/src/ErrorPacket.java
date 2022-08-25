import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an error packet.
 */
public class ErrorPacket extends Packet {
    private final String errMsg;
    private final ErrorCodes errCode;

    /**
     * @param stream The input stream containing the error "packet".
     * @throws IOException if an I/O error occurs.
     */
    public ErrorPacket(InputStream stream) throws IOException {
        super(stream);
        this.setPacketType(Opcode.Error);

        errCode = ErrorCodes.fromInteger(this.getInputStream().readShort());
        errMsg = readString();
    }

    /**
     * @return Get the error message.
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * @return Get the type of error.
     */
    public ErrorCodes getErrCode() {
        return errCode;
    }
}

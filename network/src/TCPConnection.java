import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final TCPConnectionListener listener;
    private final BufferedReader in;
    private final BufferedWriter out;
    private Thread rxThread;

    public TCPConnection(String IPAdress, int port, TCPConnectionListener listener) throws IOException {
        this(new Socket(IPAdress, port), listener);
    }

    public TCPConnection(Socket socket, TCPConnectionListener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(() -> {
            try {
                listener.OnConnection(TCPConnection.this);
                while (!rxThread.isInterrupted())
                    listener.OnRecieveMessage(TCPConnection.this, in.readLine());
            } catch (IOException e) {
                listener.OnException(TCPConnection.this, e);
            } finally {
                listener.OnDisconnection(TCPConnection.this);
            }
        });
        rxThread.start();
    }

    public synchronized void SendMsg(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            listener.OnException(this, e);
        }
    }

    public synchronized void closeConnection() throws IOException {
        rxThread.interrupt();
        socket.close();
    }

    @Override
    public String toString() {
        return "TCPConnection IP " + socket.getInetAddress() + ":" + socket.getPort();
    }
}
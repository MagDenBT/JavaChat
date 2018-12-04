import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private  Socket socket;
    private final TCPConnectionListener listener;
    private  BufferedReader in;
    private  BufferedWriter out;
    private  Thread rxThread;
    private final String IPAdress;
    private final int PORT;


    public TCPConnection(String IPAdress, int port, TCPConnectionListener listener) throws IOException {
        this(new Socket(IPAdress, port), listener);
    }

    public TCPConnection(Socket socket, TCPConnectionListener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;
        IPAdress = socket.getInetAddress().getHostAddress();
        PORT = socket.getPort();

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(() -> {
            try {

                listener.OnConnection(TCPConnection.this);
                rxThread.setName("TCP Potok");
                String msg;
                while (true)
                {
                    msg = in.readLine();
                    if(msg!=null) listener.OnRecieveMessage(TCPConnection.this, msg);
                }

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
        in.close();
        out.close();
        listener.OnDisconnection(this);
    }


    @Override
    public String toString() {
        return "TCPConnection IP " + socket.getInetAddress() + ":" + socket.getPort();
    }

}
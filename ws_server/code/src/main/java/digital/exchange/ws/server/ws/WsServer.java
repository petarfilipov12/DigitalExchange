package digital.exchange.ws.server.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import digital.exchange.ws.server.ws.event.messages.EventMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class WsServer extends WebSocketServer
{
    private final ConcurrentHashMap<WebSocket, MessageFilter> clientFilters = new ConcurrentHashMap<>();
    final ObjectMapper mapper = new ObjectMapper();

    public WsServer(final int port)
    {
        super(new InetSocketAddress(port));

        //mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public void sendEventMessage(final EventMessage eventMessage)
    {
        System.out.println("Send Msg: " + eventMessage.getJsonString());

        clientFilters.forEach((client, messageFilter) -> {
            if(messageFilter.wants(eventMessage.getMessageType(), eventMessage.getSymbol()))
            {
                client.send(eventMessage.getJsonString());
            }
        });
    }

    @Override
    public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
        //conn.send("Welcome to the server!"); //This method sends a message to the new client
        //broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);

        clientFilters.remove(conn);
    }

    @Override
    public void onMessage(final WebSocket conn, final String message) {
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);

        try{
            final MessageFilter messageFilter = mapper.readValue(message, MessageFilter.class);
            clientFilters.put(conn, messageFilter);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            conn.send("Invalid subscription format");
            conn.close();
        }
    }

    @Override
    public void onError(final WebSocket conn, final Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }
}

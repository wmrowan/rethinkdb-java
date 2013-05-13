package com.rethinkdb;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Connection {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 28015;

    public Connection(String host, int port) throws Exception {
        this(host, port, null);
    }

    public Connection(String host) throws Exception {
        this(host, DEFAULT_PORT);
    }

    public Connection(int port) throws Exception {
        this(DEFAULT_HOST, port);
    }

    public Connection() throws Exception {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Connection(String host, int port, String db) throws Exception {
        use(db);

        socket = new java.net.Socket(host, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();

        // We have to send the version magic to initalize this
        // connection on the server.

        int version_magic = Ql2.VersionDummy.Version.V0_1_VALUE;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(version_magic); // This method puts a 32bit int (or so claims the docs)
        out.write(bb.array());
    }

    public void use(String db) {
        default_db = db;
    }

    public Datum send(Ql2.Query query) throws Exception {
        byte[] serialized = query.toByteArray();

        // First write the length of the serialized message
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(serialized.length);
        out.write(bb.array());

        out.write(serialized);

        // Wait for response
        byte[] responseLengthBuf = new byte[4];
        int bytesLeft = 4;
        do {
            bytesLeft -= in.read(responseLengthBuf, 4 - bytesLeft, bytesLeft);
        } while(bytesLeft > 0);

        ByteBuffer responseLengthBuffer = ByteBuffer.wrap(responseLengthBuf);
        responseLengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        int responseLength = responseLengthBuffer.getInt();

        byte[] responseBuf = new byte[responseLength];
        bytesLeft = responseLength;
        do {
            bytesLeft -= in.read(responseBuf, responseLength - bytesLeft, bytesLeft);
        } while(bytesLeft > 0);

        Ql2.Response response = Ql2.Response.parseFrom(responseBuf);
        switch (response.getType()) {
        case SUCCESS_ATOM:
            return Datum.wrap(response.getResponseList().get(0));
        default:
            throw new Exception("unexpected return type");
        }
    }

    private String default_db;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
}

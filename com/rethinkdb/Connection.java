package com.rethinkdb;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection {
    private AtomicInteger nextToken = new AtomicInteger();
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 28015;

    private String default_db;
    private Socket socket;
    private OutputStream out;
    private InputStream in;

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
        socket.setTcpNoDelay(true);
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

    public void close() throws Exception {
        this.socket.close();
    }

    public <T> T run(Term term) throws Exception {
        Ql2.Query query = Ql2.Query.newBuilder()
            .setType(Ql2.Query.QueryType.START)
            .setToken(nextToken.incrementAndGet())
            .setQuery(term.build()).build();
        send_query(query);
        return get_response();
    }

    public void run_noreply(Term term) throws Exception {
        Ql2.Query query = Ql2.Query.newBuilder()
            .setType(Ql2.Query.QueryType.START)
            .setToken(nextToken.incrementAndGet())
            .setQuery(term.build())
            .addGlobalOptargs(
                Ql2.Query.AssocPair.newBuilder()
                    .setKey("noreply")
                    .setVal(Datum.wrap(true).build())
                    .build()
            ).build();
        send_query(query);
    }

    private void send_query(Ql2.Query query) throws Exception {
        if (default_db != null) {
            query = query.toBuilder().addGlobalOptargs(
                        Ql2.Query.AssocPair.newBuilder()
                            .setKey("db")
                            .setVal(Term.db(default_db).build())
                    ).build();
        }

        byte[] serialized = query.toByteArray();

        // Prefix the serialized message by its length
        ByteBuffer bb = ByteBuffer.allocate(4 + serialized.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(serialized.length);
        bb.put(serialized);
        out.write(bb.array());
    }

    private <T> T get_response() throws Exception {
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
            return Datum.unwrapProto(response.getResponseList().get(0));
        case SUCCESS_PARTIAL:
            return (T)"a";
        case SUCCESS_SEQUENCE:
            return Datum.ArrayDatum.unwrapProto(response.getResponseList());
        case CLIENT_ERROR:
        case COMPILE_ERROR:
        case RUNTIME_ERROR:
            throw new Exception(response.getResponseList().get(0).getRStr());
        default:
            throw new Exception("unexpected return type");
        }
    }
}

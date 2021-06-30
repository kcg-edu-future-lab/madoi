package jp.cnnc.madoi.core;

import java.io.IOException;

public interface Peer {
    String getId();
    void sendText(String string) throws IOException;
}

package jp.cnnc;

import java.io.IOException;

public interface Peer {
    String getId();
    void sendText(String string) throws IOException;
}

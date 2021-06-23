package edu.kcg.futurelab.middleman;

import java.io.IOException;

public interface Peer {
    String getId();
    void sendText(String string) throws IOException;
}

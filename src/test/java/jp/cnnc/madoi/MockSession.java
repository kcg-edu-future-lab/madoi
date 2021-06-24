package jp.cnnc.madoi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.cnnc.Peer;

public class MockSession implements Peer {
    public MockSession(String id){
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void sendText(String text) throws IOException {
        messages.add(text);
    }

    public List<String> getSentMessages() {
        return messages;
    }

    private String id;
    private List<String> messages = new ArrayList<>();
}

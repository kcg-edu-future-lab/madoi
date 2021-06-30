package jp.cnnc.madoi;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.message.Invocation;
import jp.cnnc.room.DefaultRoom;
import jp.cnnc.room.eventlogger.NullRoomEventLogger;

public class ExecAndSendTest {
    @Test
    public void test() throws Throwable{
        var room = new DefaultRoom("room1", new NullRoomEventLogger());
        var session = new MockSession("dummy1");
        room.onPeerArrive(session);
        var mapper = new ObjectMapper();

        var methodConfig = mapper.createObjectNode();
        methodConfig.put("type", "methodConfig");
        var body = mapper.createObjectNode();
        methodConfig.set("body", body);
        body.put("index", 1);
        var option = mapper.createObjectNode();
        body.set("option", option);
        option.put("type", "execAndSend");
        option.put("keep", "log");
        option.put("maxLog", 1000);
        room.onPeerMessage(session.getId(), methodConfig.toString());

        var invocation = mapper.readTree(
            mapper.writeValueAsString(new Invocation(1, 1, new String[]{"arg1"})));
        room.onPeerMessage(session.getId(), invocation.toString());

        for(String m : session.getSentMessages()){
            System.out.println(m);
        }
    }

}

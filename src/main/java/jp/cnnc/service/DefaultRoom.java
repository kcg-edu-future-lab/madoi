/*
 * Copyright 2017 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.cnnc.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jp.cnnc.Peer;
import jp.cnnc.Room;
import jp.go.nict.langrid.commons.io.FileUtil;

public class DefaultRoom implements Room{
	public DefaultRoom(String roomId) {
		this.roomId = roomId;
	}
	
	@Override
	public boolean canRemove() {
		return sessions.size() == 0;
	}

	@Override
	public synchronized void onSessionOpen(String sessionId, Peer session) {
		sessions.put(sessionId, session);
	}

	@Override
	public synchronized long onSessionClose(String sessionId) {
		sessions.remove(sessionId);
		if(sessions.size() == 0) {
			return 0;
		}
		return -1;
	}

	@Override
	public synchronized void onSessionMessage(String sessionId, String message) {
		for(Peer s : sessions.values()){
			try {
				roomLog.printf(",%n{\"time\": %d, \"sender\": \"%s\", \"message\": %s}",
						new Date().getTime(), sessionId, message);
				s.sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onRoomStarted() {
		Date now = new Date();
		String dates = new SimpleDateFormat("yyyyMMdd").format(now);
		String times = new SimpleDateFormat("HHmmss").format(now);
		File dir = new File(new File("logs"), dates);
		dir.mkdirs();
		try {
			File f = FileUtil.createUniqueFile(
					dir,
					getClass().getSimpleName() + "-" + roomId + "-" + times + "-", ".json");
			roomLog = new PrintWriter(Files.newBufferedWriter(f.toPath()));
			roomLog.print("[{}");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void onRoomEnded() {
		roomLog.printf("%n]%n");
		roomLog.close();
	}

	private String roomId;
	private PrintWriter roomLog;

	private Map<String, Peer> sessions = new LinkedHashMap<>();
}

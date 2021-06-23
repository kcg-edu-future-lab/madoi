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
package edu.kcg.futurelab.middleman;

public interface Room {
	void onSessionOpen(String sessionId, Peer session);
	/**
	 * Room削除までの猶予をミリ秒で返す。-1の場合は可能な限り削除されない。
	 * @param session
	 * @return
	 */
	long onSessionClose(String sessionId);
	void onSessionMessage(String sessionId, String message);
	boolean canRemove();
	void onRoomStarted();
	void onRoomEnded();
}

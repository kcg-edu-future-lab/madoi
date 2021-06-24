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
package jp.cnnc;

public interface Room {
	void onPeerArrive(Peer peer);
	/**
	 * Room削除までの猶予をミリ秒で返す。-1の場合は可能な限り削除されない。
	 * @param session
	 * @return
	 */
	long onPeerClose(String peerId);
	void onPeerMessage(String peerId, String message);
	void onPeeerMessage(String peerId, byte[] message);
	void onRoomStarted();
	void onRoomEnded();
	boolean canRemove();
}

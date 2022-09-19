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
package jp.cnnc.madoi.core;

import java.util.Map;

import com.google.common.collect.EvictingQueue;

import jp.cnnc.madoi.core.message.Invocation;

public interface Room {
	void onPeerArrive(Peer peer);
	void onPeerLeave(String peerId);
	void onPeerMessage(String peerId, String message);
	void onPeerMessage(String peerId, byte[] message);
	void onPeerError(String peerId, Throwable cause);
	int getPeerCount();
	String getRoomId();
	Map<Integer, EvictingQueue<Invocation>> getInvocationLogs();
}

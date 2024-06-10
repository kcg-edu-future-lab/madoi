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
package jp.cnnc.madoi.core.room;

import java.util.List;
import java.util.Map;

import com.google.common.collect.EvictingQueue;

import jp.cnnc.madoi.core.message.CastType;
import jp.cnnc.madoi.core.message.InvokeMethod;
import jp.cnnc.madoi.core.message.definition.FunctionDefinition;
import jp.cnnc.madoi.core.message.definition.ObjectDefinition;

public interface Room {
	String getId();
	Map<String, Object> getProfile();

	int getPeerCount();
	Map<Integer, ObjectDefinition> getObjectDefinitions();
	Map<Integer, FunctionDefinition> getFunctionDefinitions();
	Map<Integer, EvictingQueue<InvokeMethod>> getInvocationLogs();

	void onRoomCreated();
	void onPeerArrive(Peer peer);
	void onPeerLeave(Peer peer);
	void onPeerMessage(Peer peer, String message);
	void onPeerMessage(Peer peer, byte[] message);
	void onPeerError(Peer peer, Throwable cause);

	void castMessage(CastType ct, List<String> recipients, String senderPeerId, String messageType, String message);
}
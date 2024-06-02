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

import java.util.List;
import java.util.Map;

import com.google.common.collect.EvictingQueue;

import jp.cnnc.madoi.core.message.InvokeMethod;
import jp.cnnc.madoi.core.message.definition.FunctionDefinition;
import jp.cnnc.madoi.core.message.definition.ObjectDefinition;

public interface Room {
	String getRoomId();
	Map<Integer, ObjectDefinition> getObjectDefinitions();
	Map<Integer, FunctionDefinition> getFunctionDefinitions();
	int getPeerCount();
	Map<Integer, EvictingQueue<InvokeMethod>> getInvocationLogs();

	void onPeerArrive(Peer peer);
	void onPeerLeave(String peerId);
	void onPeerMessage(String peerId, String message);
	void onPeerMessage(String peerId, byte[] message);
	void onPeerError(String peerId, Throwable cause);

	void castMessage(CastType ct, List<String> recipients, String senderPeerId, String messageType, String message);
}

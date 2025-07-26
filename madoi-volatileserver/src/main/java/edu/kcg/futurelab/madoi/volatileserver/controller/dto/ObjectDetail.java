package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.ArrayList;
import java.util.Collection;

import edu.kcg.futurelab.madoi.core.message.definition.ObjectDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDetail {
	private ObjectDefinition definition;
	private Object state;
	// 最後に受け取ったUpdateObjectStateのobjRevision
	private int lastReceivedRevision = -1;
	// 最後に送信したInvokeMethodのserverObjRevision
	private int lastSentRevision = -1;
	private Collection<MethodDetail> methods = new ArrayList<>();
}

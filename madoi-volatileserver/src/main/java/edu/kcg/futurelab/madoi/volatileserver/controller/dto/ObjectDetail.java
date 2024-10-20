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
	private int revision = -1;
	private Collection<MethodDetail> methods = new ArrayList<>();
}

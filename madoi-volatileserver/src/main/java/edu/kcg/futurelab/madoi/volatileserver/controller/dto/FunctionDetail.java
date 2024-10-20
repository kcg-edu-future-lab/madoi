package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.Date;

import edu.kcg.futurelab.madoi.core.message.definition.FunctionDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDetail {
	private FunctionDefinition definition;
	private int invocationCount;
	private Date lastInvocation;
}

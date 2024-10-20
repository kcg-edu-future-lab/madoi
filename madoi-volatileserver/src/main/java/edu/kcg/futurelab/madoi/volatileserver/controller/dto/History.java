package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class History {
	public enum Type{
		InvokeFunction, InvokeMethod, UpdateObjectState, UserMessage
	}
	private Date received;
	private Type type;
	private String sender;
	private String target;
	private String content;
}

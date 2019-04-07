package com.easyapper.eventsbatchms.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter
@ToString
public class Pair <T1, T2> {
	
	T1 first;
	T2 second;
	
}

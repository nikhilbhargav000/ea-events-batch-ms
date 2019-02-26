package com.easyapper.eventsbatchms.model.readevent;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter @Setter
@ToString
public class OrglEventPriceDto {

	private int id;
	private String eid;
	private String name;
	private String currency;
	private double value;
	private double discount_pct;
	private double discount_value;
	private String validity_start;
	private String validity_end;
	private String note;
	private int capacity;
	private int min_quantity ;
	private int is_multi ;
	private int is_individual ;
	private int all_occurrences ;
	private int rank ;
	private int occurrence_id ;
	private String date;
	private String end_date;
	private String time;
	private String end_time;
	private String is_valid;
	private List<OrglEventPriceOccurrenceDto> occurrences;
	private double convenience_fees;
	private double cgst;
	private double sgst;
	
}

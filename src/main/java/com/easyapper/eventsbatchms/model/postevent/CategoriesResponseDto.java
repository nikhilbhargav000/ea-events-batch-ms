package com.easyapper.eventsbatchms.model.postevent;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class CategoriesResponseDto {
	List<CategoryDto> categories;
}

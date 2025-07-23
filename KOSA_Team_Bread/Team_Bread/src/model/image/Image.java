package model.image;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {

	// 이미지 ID
	private Integer imageId;

	// 제품 ID
	private Integer productId;

	// 카테고리 ID
	private Integer categoryId;

	// 이미지명
	private String imageName;

	// 저장 위치
	private String imageLocation;

	// 등록날짜
	private LocalDate imageRegDate;

	// 수정날짜
	private LocalDate imageModDate;

}

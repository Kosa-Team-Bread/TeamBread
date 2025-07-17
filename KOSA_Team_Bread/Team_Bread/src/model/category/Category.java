// Category.java

package model.category;
import lombok.*;

@Data
@Builder
public class Category {

	// 카테고리 ID
	private Integer categoryId;
	
	// 카테고리 부모 ID
	private Integer parentId;
	
	// 카테고리 명
	private String categoryName;
	
	// 카테고리 레벨
	private Integer level;
}

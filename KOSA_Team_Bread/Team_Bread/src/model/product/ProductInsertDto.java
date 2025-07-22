package model.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInsertDto {
	
	String categoryName;
	String productName;
	Integer price;
	Integer cost;
	String adminName;
	Integer productQuantity;
	String stockName;
	String stockLocation;
	String imageLocation;
}

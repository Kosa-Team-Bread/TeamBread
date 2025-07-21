package model.inout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InoutInsertDto {
	  String productName;
      String adminName;
      String categoryName;
      String inoutType;
      int inoutQuantity;
      String content;
}

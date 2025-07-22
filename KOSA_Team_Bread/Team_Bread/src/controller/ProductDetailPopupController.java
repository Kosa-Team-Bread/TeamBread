package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.product.ProductDAO;
import model.product.ProductDetailSelectDto;
import util.AlertUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductDetailPopupController implements Initializable {

    @FXML private ImageView productImageView;
    @FXML private Label     nameLabel;
    @FXML private Label     categoryLabel;
    @FXML private Label     priceLabel;
    @FXML private Label     costLabel;
    @FXML private Label     modifiedDateLabel;
    @FXML private Button    updateButton;
    @FXML private Button    deleteButton;

    private int productId;
    private final ProductDAO productDao = new ProductDAO(/*CategoryDAO, AdminDAO*/);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 팝업 띄울 때 setProductId()에서 loadDetail() 호출됩니다.
    }

    /** StockController에서 호출 */
    public void setProductId(int id) {
        this.productId = id;
        loadDetail();
    }

    /** DB에서 가져온 DTO로 화면 세팅 */
    private void loadDetail() {
        try {
            ProductDetailSelectDto dto = productDao.getProductFromProductId(productId);

            nameLabel.setText(dto.getProductName());
            categoryLabel.setText(dto.getCategoryName());
            priceLabel.setText(dto.getPrice() + "원");
            costLabel .setText(dto.getCost()  + "원");
            modifiedDateLabel.setText(dto.getProductModDate().toString());

            // 이미지가 있을 때만 세팅
            if (dto.getImageLocation() != null && !dto.getImageLocation().isEmpty()) {
                productImageView.setImage(new Image("file:" + dto.getImageLocation()));
            } else {
                productImageView.setImage(null);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            AlertUtil.showError("상세조회 오류", e.getMessage());
        }
    }

    /** 수정하기 버튼 클릭 */
    @FXML
    private void handleUpdate() {
        try {
            // 1) 판매가 입력
            TextInputDialog priceDlg = new TextInputDialog();
            priceDlg.setTitle("판매가 수정");
            priceDlg.setHeaderText("새 판매가를 입력하세요");
            priceDlg.setContentText("판매가:");
            Optional<String> pRes = priceDlg.showAndWait();
            if (pRes == null) return;
            int newPrice = Integer.parseInt(pRes.get().trim());

            // 2) 원가 입력
            TextInputDialog costDlg = new TextInputDialog();
            costDlg.setTitle("원가 수정");
            costDlg.setHeaderText("새 원가를 입력하세요");
            costDlg.setContentText("원가:");
            Optional<String> cRes = costDlg.showAndWait();
            if (cRes == null) return;
            int newCost = Integer.parseInt(cRes.get().trim());

            // 3) DAO 호출 (updateProduct 시그니처: updateProduct(int productId, int newPrice, int newCost))
            ProductDAO.updateProduct(productId, newPrice, newCost);

            // 4) UI 갱신
            loadDetail();
            AlertUtil.showInfo("수정 완료", "가격이 성공적으로 수정되었습니다.");

        } catch (NumberFormatException nfe) {
            AlertUtil.showError("입력 오류", "숫자 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("수정 실패", e.getMessage());
        }
    }

    /** 삭제하기 버튼 클릭 */
    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
            "정말 이 상품을 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO
        );
        confirmation.setTitle("삭제 확인");
        Optional<ButtonType> res = confirmation.showAndWait();
        if (res.orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                productDao.deleteProduct(productId);
                AlertUtil.showInfo("삭제 완료", "상품이 삭제되었습니다.");
                close();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showError("삭제 오류", e.getMessage());
            }
        }
    }

    /** 팝업 닫기 */
    private void close() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}

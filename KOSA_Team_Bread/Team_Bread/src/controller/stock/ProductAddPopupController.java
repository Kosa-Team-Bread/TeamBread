package controller;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;
import model.category.Category;
import model.category.CategoryDAO;
import model.product.ProductDAO;
import model.product.ProductInsertDto;
import util.AlertUtil;

public class ProductAddPopupController implements Initializable {

    // ---------- FXML ----------
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField costField;
    @FXML private TextField quantityField;
    @FXML private TextField locationField;
    @FXML private TextField adminNameField;         // 직접 입력용
    @FXML private ComboBox<String> adminComboBox;   // 콤보박스 선택용
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Label imageFileNameLabel;
    
    // ---------- Local ----------
    private File selectedImageFile;

    // ---------- DAOs ----------
    private final CategoryDAO cateDao = new CategoryDAO();
    private final AdminDAO adminDao     = new AdminDAO();
    // ProductDAO는 static 메서드로 프로시저 호출
    // productDao 인스턴스는 searchProduct 용도로만 사용
    private final ProductDAO productDao = new ProductDAO();

    // ---------- 초기화 ----------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCategoryCombo();
        initAdminCombo();
        // static DAO 주입
        ProductDAO.setCategoryDAO(cateDao);
        ProductDAO.setAdminDAO(adminDao);
    }

    private void initCategoryCombo() {
        try {
            ObservableList<Category> list = cateDao.findAllCategory();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (Category c : list) names.add(c.getCategoryName());
            categoryComboBox.setItems(names);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("카테고리 로딩 실패", e.getMessage());
        }
    }

    private void initAdminCombo() {
        try {
            ObservableList<Admin> list = adminDao.getAllAdmins();
            ObservableList<String> names = FXCollections.observableArrayList();
            for (Admin a : list) names.add(a.getAdminName());
            adminComboBox.setItems(names);
        } catch (Exception e) {
            // 무시해도 무방
        }
    }

    // ---------- 이벤트 ----------
    @FXML
    private void handleImageAdd() {
        FileChooser fc = new FileChooser();
        fc.setTitle("이미지 선택");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fc.showOpenDialog(nameField.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imageFileNameLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            String productName   = trim(nameField.getText());
            String categoryName  = categoryComboBox.getValue();
            String stockLocation = trim(locationField.getText());
            String imageLocation = selectedImageFile != null
                                 ? selectedImageFile.getAbsolutePath()
                                 : null;

            int price    = parseInt(priceField.getText(),    "판매가");
            int cost     = parseInt(costField.getText(),     "원가");
            int quantity = parseInt(quantityField.getText(), "수량");

            if (productName.isEmpty()
             || categoryName == null || categoryName.isEmpty()) {
                AlertUtil.showWarning("입력 오류", "필수 항목을 모두 입력/선택하세요.");
                return;
            }

            // DTO 조립 (imageLocation 포함)
            ProductInsertDto dto = ProductInsertDto.builder()
                .productName(productName)
                .price(price)
                .cost(cost)
                .productQuantity(quantity)
                .stockLocation(stockLocation)
                .categoryName(categoryName)
                .imageLocation(imageLocation)
                .build();

            // 상품+재고+이미지 모두 등록 (저장 프로시저 호출)
            ProductDAO.insertProduct(dto);

            AlertUtil.showInfo("등록 완료", "상품이 등록되었습니다.");
            close();
        }
        catch (NumberFormatException nfe) {
            AlertUtil.showError("숫자 형식 오류", nfe.getMessage());
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            AlertUtil.showError("DB 오류", e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("예상치 못한 오류", e.toString());
        }
    }

    // ---------- 헬퍼 ----------
    private void close() {
        Stage stage = (Stage)nameField.getScene().getWindow();
        stage.close();
    }
    private String trim(String s) {
        return (s == null ? "" : s.trim());
    }
    private int parseInt(String text, String field) {
        try { return Integer.parseInt(text.trim()); }
        catch(Exception e) {
            throw new NumberFormatException(field + "은(는) 숫자여야 합니다.");
        }
    }
}

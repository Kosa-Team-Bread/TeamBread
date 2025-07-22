// CouponController.java

package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.category.Category;
import model.category.CategoryDAO;
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;
import model.product.ProductDAO;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CouponController implements Initializable {

    // FXML 필드
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponCategoryCol;
    @FXML private TableColumn<Coupon, String> couponNameCol;
    @FXML private TableColumn<Coupon, Integer> percentCol;
    @FXML private TableColumn<Coupon, String> startTimeCol;
    @FXML private TableColumn<Coupon, String> deadlineCol;
    @FXML private ComboBox<String> couponCategoryFilter; // 카테고리 필터
    @FXML private TextField couponSearchField;         // 쿠폰명 검색

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productCategoryCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Number> priceCol;
    @FXML private TableColumn<Product, Number> costCol;
    @FXML private ComboBox<String> productCategoryFilter; // 카테고리 필터
    @FXML private TextField productSearchField;        // 상품명 검색

    // DAO
    private CouponDAO couponDAO;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    // 데이터 리스트
    private ObservableList<Coupon> couponMasterList;
    private FilteredList<Coupon> couponFilteredList;
    
    private ObservableList<Product> productMasterList;
    private FilteredList<Product> productFilteredList;

    // 카테고리 정보를 저장할 Map (ID-이름 매핑)
    private Map<Integer, String> categoryMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DAO 객체 생성
        couponDAO = new CouponDAO();
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();

        // 테이블 컬럼 설정
        setupTableColumns();

        // DB에서 데이터 로드
        loadDataFromDB();

        // 필터링 기능 설정
        setupFilterListeners();
        
        // 이벤트 핸들러 설정
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                handleProductSelection(productTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** 테이블의 각 컬럼과 데이터 모델의 필드를 연결(바인딩)합니다. */
    private void setupTableColumns() {
        // 쿠폰 테이블
        couponCategoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName()));
        couponNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouponName()));
        percentCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPercent()).asObject());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)));
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadLine().format(formatter)));

        // 상품 테이블 (Map을 이용해 카테고리 ID를 이름으로 변환)
        productCategoryCol.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryMap.getOrDefault(categoryId, "미분류"));
        });
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        priceCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()));
        costCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCost()));
    }

    /** DB에서 카테고리, 쿠폰, 상품 데이터를 로드합니다. */
    private void loadDataFromDB() {
        // 1. 카테고리 정보 로드 및 ComboBox 채우기
        categoryMap = new HashMap<>();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        categoryNames.add("전체"); // "전체" 항목 추가
        try {
            for (Category category : categoryDAO.findAllCategory()) {
                categoryMap.put(category.getCategoryId(), category.getCategoryName());
                categoryNames.add(category.getCategoryName());
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        couponCategoryFilter.setItems(categoryNames);
        productCategoryFilter.setItems(categoryNames);
        couponCategoryFilter.setValue("전체"); // 기본값 설정
        productCategoryFilter.setValue("전체"); // 기본값 설정

        // 2. 쿠폰 원본 데이터 로드
        couponMasterList = couponDAO.getAllCoupons();

        // 3. 상품 원본 데이터 로드
        try {
            productMasterList = productDAO.findAllProduct();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** ComboBox와 TextField의 값이 변경될 때 필터링이 동작하도록 리스너를 설정합니다. */
    private void setupFilterListeners() {
        // 쿠폰 목록 필터링
        couponFilteredList = new FilteredList<>(couponMasterList, p -> true);
        couponTable.setItems(couponFilteredList);
        couponCategoryFilter.valueProperty().addListener((obs, old, val) -> applyCouponFilter());
        couponSearchField.textProperty().addListener((obs, old, val) -> applyCouponFilter());

        // 상품 목록 필터링
        productFilteredList = new FilteredList<>(productMasterList, p -> true);
        productTable.setItems(productFilteredList);
        productCategoryFilter.valueProperty().addListener((obs, old, val) -> applyProductFilter());
        productSearchField.textProperty().addListener((obs, old, val) -> applyProductFilter());
    }

    /** 현재 선택된 필터 조건들을 쿠폰 목록에 적용합니다. */
    private void applyCouponFilter() {
        String selectedCategory = couponCategoryFilter.getValue();
        String searchText = couponSearchField.getText().toLowerCase();

        couponFilteredList.setPredicate(coupon -> {
            boolean categoryMatch = "전체".equals(selectedCategory) ||
                                    (coupon.getCategoryName() != null && coupon.getCategoryName().equals(selectedCategory));
            boolean searchMatch = searchText.isEmpty() || coupon.getCouponName().toLowerCase().contains(searchText);
            return categoryMatch && searchMatch;
        });
    }

    /** 현재 선택된 필터 조건들을 상품 목록에 적용합니다. */
    private void applyProductFilter() {
        String selectedCategory = productCategoryFilter.getValue();
        String searchText = productSearchField.getText().toLowerCase();

        productFilteredList.setPredicate(product -> {
            String productCategoryName = categoryMap.getOrDefault(product.getCategoryId(), "");
            boolean categoryMatch = "전체".equals(selectedCategory) || productCategoryName.equals(selectedCategory);
            boolean searchMatch = searchText.isEmpty() || product.getProductName().toLowerCase().contains(searchText);
            return categoryMatch && searchMatch;
        });
    }

    /** 상품 더블클릭 시 팝업창을 띄웁니다. */
    private void handleProductSelection(Product selectedProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/CouponApplyPopup.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(selectedProduct.getProductName() + " - 쿠폰 적용");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
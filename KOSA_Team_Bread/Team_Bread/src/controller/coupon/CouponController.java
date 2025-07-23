// CouponController.java

package controller.coupon;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import model.category.Category;
import model.category.CategoryDAO;
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;
import model.product.ProductDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import controller.coupon.CouponApplyPopupController;

public class CouponController implements Initializable {

    // FXML 필드
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponCategoryCol;
    @FXML private TableColumn<Coupon, String> couponNameCol;
    @FXML private TableColumn<Coupon, Integer> percentCol;
    @FXML private TableColumn<Coupon, String> startTimeCol;
    @FXML private TableColumn<Coupon, String> deadlineCol;
    @FXML private ComboBox<String> couponCategoryFilter;    // 카테고리 필터
    @FXML private TextField couponSearchField;              // 쿠폰명 검색
    
    // 외부 버튼(쿠폰 수정/삭제 버튼)을 위한 FXML 필드 추가
    @FXML private Button addCouponBtn;
    @FXML private Button editCouponBtn;
    @FXML private Button deleteCouponBtn;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productCategoryCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Number> priceCol;
    @FXML private TableColumn<Product, Number> costCol;
    @FXML private ComboBox<String> productCategoryFilter;   // 카테고리 필터
    @FXML private TextField productSearchField;             // 상품명 검색

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
        
        // 카테고리 Map을 먼저 로드해야 테이블 컬럼 설정 시 사용 가능
        loadDataFromDB();
        
        // 테이블 컬럼 설정
        setupTableColumns();
        
        // 필터링 기능 설정
        setupFilterListeners();
        
        // 버튼 상태 관리 기능 설정
        setupButtonControls();
        
        // 이벤트 핸들러 설정
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                handleProductSelection(productTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** 테이블의 각 컬럼과 데이터 모델의 필드 연결(바인딩) */
    private void setupTableColumns() {
        // --- 쿠폰 테이블 설정 ---
        couponCategoryCol.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryMap.getOrDefault(categoryId, "미분류"));
        });
        couponNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouponName()));
        percentCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPercent()).asObject());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 날짜 포맷은 시/분 제외
        startTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)));
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadLine().format(formatter)));
        
        // --- 상품 테이블 설정 ---
        productCategoryCol.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryMap.getOrDefault(categoryId, "미분류"));
        });
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        priceCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()));
        costCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCost()));

        // 가격/원가 컬럼의 표시 형식 변경
        DecimalFormat currencyFormatter = new DecimalFormat("#,###");

        Callback<TableColumn<Product, Number>, TableCell<Product, Number>> currencyCellFactory = column -> {
            return new TableCell<Product, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(currencyFormatter.format(item.doubleValue()));
                    }
                }
            };
        };

        priceCol.setCellFactory(currencyCellFactory);
        costCol.setCellFactory(currencyCellFactory);
    }
    
    /** 
     * 테이블 선택 상태에 따라 쿠폰 수정/삭제 버튼의 활성화/비활성화 상태를 관리
     */
    private void setupButtonControls() {
        // 초기에는 수정/삭제 버튼을 비활성화
        editCouponBtn.setDisable(true);
        deleteCouponBtn.setDisable(true);

        // 테이블의 선택된 항목이 변경될 때마다 리스너가 호출됨
        couponTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // 새로 선택된 항목이 있으면(null이 아니면) 버튼 활성화, 없으면 비활성화
            boolean isItemSelected = (newSelection != null);
            editCouponBtn.setDisable(!isItemSelected);
            deleteCouponBtn.setDisable(!isItemSelected);
        });
    }
    
    
    /**
     * '쿠폰 추가' 버튼 클릭 시 실행될 로직
     * @param event 액션 이벤트 객체
     */
    @FXML
    private void handleAddAction(ActionEvent event) {
        System.out.println("추가 버튼 클릭");
        // TODO: 쿠폰 추가 팝업창을 띄우는 로직 구현
    }
    
    /**
     * '쿠폰 수정' 버튼 클릭 시 실행될 로직
     * @param event 액션 이벤트 객체
     */
    @FXML
    private void handleEditAction(ActionEvent event) {
        Coupon selectedCoupon = couponTable.getSelectionModel().getSelectedItem();
        if (selectedCoupon == null) {
            showAlert(Alert.AlertType.WARNING, "선택 오류", "수정할 쿠폰을 먼저 선택해주세요.");
            return;
        }
        System.out.println("수정 버튼 클릭: " + selectedCoupon.getCouponName());
        // TODO: 쿠폰 수정 팝업창을 띄우고 selectedCoupon 객체를 전달하는 로직 구현
    }
    
    /**
     * '쿠폰 삭제' 버튼 클릭 시 실행될 로직
     * @param event 액션 이벤트 객체
     */
    @FXML
    private void handleDeleteAction(ActionEvent event) {
        Coupon selectedCoupon = couponTable.getSelectionModel().getSelectedItem();
        if (selectedCoupon == null) {
            showAlert(Alert.AlertType.WARNING, "선택 오류", "삭제할 쿠폰을 먼저 선택해주세요.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("쿠폰 삭제 확인");
        alert.setHeaderText("'" + selectedCoupon.getCouponName() + "' 쿠폰을 삭제하시겠습니까?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                couponDAO.deleteCoupon(selectedCoupon.getCouponId());
                couponMasterList.remove(selectedCoupon);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "삭제 실패", "데이터베이스 오류로 인해 삭제에 실패했습니다.");
                e.printStackTrace();
            }
        }
    }

    /** DB에서 카테고리, 쿠폰, 상품 데이터를 로드 */
    private void loadDataFromDB() {
        // 1. 카테고리 정보 로드 및 ComboBox 채우기
        categoryMap = new HashMap<>();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        categoryNames.add("전체");
        try {
            for (Category category : categoryDAO.findAllCategory()) {
                categoryMap.put(category.getCategoryId(), category.getCategoryName());
                categoryNames.add(category.getCategoryName());
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        couponCategoryFilter.setItems(categoryNames);
        productCategoryFilter.setItems(categoryNames);
        couponCategoryFilter.setValue("전체");
        productCategoryFilter.setValue("전체");

        // 2. 쿠폰 원본 데이터 로드
        couponMasterList = couponDAO.getAllCoupons();

        // 3. 상품 원본 데이터 로드
        try {
            productMasterList = productDAO.findAllProduct();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** ComboBox와 TextField의 값이 변경될 때 필터링이 동작하도록 리스너 설정 */
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

    /** 현재 선택된 필터 조건들을 쿠폰 목록에 적용 */
    private void applyCouponFilter() {
        String selectedCategory = couponCategoryFilter.getValue();
        String searchText = couponSearchField.getText().toLowerCase();

        couponFilteredList.setPredicate(coupon -> {
            String couponCategoryName = categoryMap.getOrDefault(coupon.getCategoryId(), "");
            boolean categoryMatch = "전체".equals(selectedCategory) || couponCategoryName.equals(selectedCategory);
            boolean searchMatch = searchText.isEmpty() || coupon.getCouponName().toLowerCase().contains(searchText);
            return categoryMatch && searchMatch;
        });
    }

    /** 현재 선택된 필터 조건들을 상품 목록에 적용 */
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

    /**
     * 상품 더블클릭 시 쿠폰 적용 팝업창 띄움
     * 팝업 컨트롤러에 선택된 상품 객체를 전달하는 로직 추가
     * @param selectedProduct 선택된 상품 객체
     */
    private void handleProductSelection(Product selectedProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/coupon/CouponApplyPopup.fxml"));
            Parent root = loader.load();

            CouponApplyPopupController popupController = loader.getController();
            popupController.initData(selectedProduct);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(selectedProduct.getProductName() + " - 쿠폰 적용");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (IOException e) {
            System.out.println("팝업창 로드 중 오류 발생");
            e.printStackTrace();
        }
    }
    
    /**
     * [신규] Alert 창을 띄우는 공통 메소드
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
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

import model.admin.AdminDAO;
import model.category.Category;
import model.category.CategoryDAO;
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;
import model.product.ProductDAO;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import controller.coupon.CouponApplyPopupController;


// Made By 김기성
/**
 * '쿠폰 관리' 화면의 모든 UI 이벤트와 비즈니스 로직을 담당하는 컨트롤러 클래스
 * MVC(Model-View-Controller)에서 Controller의 역할로, 
 * View(CouponManagement.fxml)와 Model(CouponDAO.java) 사이의 중재자 역할 담당 
*/
public class CouponController implements Initializable {

    // --- FXML UI 컴포넌트 ---
    // fxml 파일에 정의된 UI 컨트롤들을 코드와 연결하기 위한 필드들
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

    // --- 데이터 접근 객체 (DAO) ---
    // 각 DB 테이블과의 통신을 담당하는 클래스 인스턴스
    private CouponDAO couponDAO;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    // --- 데이터 모델 리스트 ---
    // TableView에 데이터를 표시하기 위한 리스트들
    private ObservableList<Coupon> couponMasterList;        // DB에서 가져온 모든 쿠폰의 원본 데이터
    private FilteredList<Coupon> couponFilteredList;        // 검색 및 필터 조건이 적용된 후, 실제 화면에 보여질 데이터
    
    private ObservableList<Product> productMasterList;      // DB에서 가져온 모든 상품의 원본 데이터
    private FilteredList<Product> productFilteredList;      // 검색 및 필터 조건이 적용된 후, 실제 화면에 보여질 데이터

    // --- 데이터 캐시 ---
    // 카테고리 정보를 저장할 Map (ID-이름 매핑)
    // DB 조회를 최소화하기 위해 카테고리 정보를 메모리에 저장해두는 Map
    private Map<Integer, String> categoryMap;

    // fxml 로드가 완료된 후, 컨트롤러 초기화 코드
    // UI 컴포넌트 설정, 데이터 로딩, 이벤트 리스너 등록 등 초기 상태 설정 로직 담당'
    // * @param location FXML 파일의 경로
    // * @param resources FXML 파일에서 사용하는 리소스
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) 필드 DAO 객체 생성 및 초기화 (의존성 주입)
        // CouponDAO / ProductDAO가 다른 DAO를 필요로 하므로, 의존성이 낮은 객체부터 생성, 
        // 생성자의 인자로 전달 (생성자 주입) 
        // >>> 객체 간의 결합도를 낮추고 코드 구조 명확성 높임
        categoryDAO = new CategoryDAO();
        AdminDAO adminDAO = new AdminDAO();

        // ProductDAO의 static DAO도 함께 설정
        ProductDAO.setCategoryDAO(categoryDAO);
        ProductDAO.setAdminDAO(adminDAO);

        // 생성자 주입
        productDAO = new ProductDAO(categoryDAO, adminDAO);
        couponDAO  = new CouponDAO(productDAO, categoryDAO);

        // 2) DB에서 데이터 로드
        loadDataFromDB();

        // 3) 테이블 컬럼 내용 및 형식 설정
        setupTableColumns();

        // 4) 검색 & 필터링 기능 설정
        setupFilterListeners();

        // 5) 버튼 상태(활성화/비활성화) 관리 기능 설정
        setupButtonControls();

        // 6) 더블 클릭 팝업 호출 (더블 클릭 이벤트 핸들러 설정)
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 &&
                productTable.getSelectionModel().getSelectedItem() != null) {
                handleProductSelection(productTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    
    /**
     * 2) DB에서 데이터 로드
     * DB에서 카테고리, 쿠폰, 상품 데이터를 로드 
    */
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

    /**
     * 3) 테이블 컬럼 내용 및 형식 설정
     * 테이블의 각 컬럼과 데이터 모델의 필드 연결(바인딩) 정의 메소드
     * TableView의 각 컬럼에 어떤 데이터를 어떻게 표시할지 정의
     * setCellValueFactory를 통해 데이터 모델(Coupon, Product)의 필드와 테이블 셀 연결(바인딩)
    */
    private void setupTableColumns() {
        // --- 쿠폰 테이블 컬럼 설정 ---
        // 카테고리 ID를 categoryMap을 참조, 실제 카테고리 이름으로 변환하여 표시
        couponCategoryCol.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryMap.getOrDefault(categoryId, "미분류"));
        });
        couponNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouponName()));
        percentCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPercent()).asObject());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

        // 가격/원가 컬럼의 표시 형식 정의
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
     * 4) 검색 & 필터링 기능 설정
     * ComboBox와 TextField의 값이 변경될 때마다 실시간으로 테이블 내용을 필터링하도록 리스너 설정 
     * FilteredList는 원본 리스트(couponMasterList)를 래핑, 원본 데이터는 그대로 둔 채 보여지는 데이터만 필터링
    */
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

    /* 현재 선택된 필터 조건들을 쿠폰 목록에 적용 (필터링) */
    private void applyCouponFilter() {
        String selectedCategory = couponCategoryFilter.getValue();
        String searchText = couponSearchField.getText().toLowerCase();

        // setPredicate: FilteredList가 어떤 항목을 보여줄지 결정하는 규칙(Predicate) 정의
        // 이 람다식은 리스트의 모든 항목에 대해 실행, true를 반환하는 항목만 화면에 출력
        couponFilteredList.setPredicate(coupon -> {
            String couponCategoryName = categoryMap.getOrDefault(coupon.getCategoryId(), "");
            boolean categoryMatch = "전체".equals(selectedCategory) || couponCategoryName.equals(selectedCategory);
            boolean searchMatch = searchText.isEmpty() || coupon.getCouponName().toLowerCase().contains(searchText);
            return categoryMatch && searchMatch;
        });
    }

    /* 현재 선택된 필터 조건들을 상품 목록에 적용 */
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
     * 5) 버튼 상태(활성화/비활성화) 관리 기능 설정
     * 테이블 선택 상태에 따라 쿠폰 수정/삭제 버튼의 활성화/비활성화 상태 관리
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
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/coupon/CouponEditPopup.fxml"));
            Parent root = loader.load();

            CouponEditPopupController popupController = loader.getController();
            // 팝업 컨트롤러에 필요한 데이터 전달
            popupController.initData(selectedCoupon, couponDAO, categoryMap);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("쿠폰 정보 수정");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // 팝업창이 닫힌 후, 수정이 완료되었는지 확인하고 목록을 새로고침
            if (popupController.isUpdated()) {
                refreshCouponTable();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "수정 화면을 여는 데 실패했습니다.");
        }
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
    
    /**
     * 쿠폰 목록 테이블을 새로고침하는 메소드
     * 쿠폰이 추가/수정/삭제되었을 때 호출, 화면을 최신 상태로 유지
    */
    private void refreshCouponTable() {
        this.couponMasterList = couponDAO.getAllCoupons();
        this.couponFilteredList = new FilteredList<>(couponMasterList, p -> true);
        couponTable.setItems(this.couponFilteredList);
        applyCouponFilter();
    }
    
    /**
     * Alert 창을 띄우는 공통 헬퍼 메소드
     * @param alertType 경고, 정보, 오류 등 Alert의 종류
     * @param title 창 제목
     * @param message 창에 표시할 메시지
    */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    /** 상품 더블 클릭 시 쿠폰 적용 팝업창 호출 */
    private void handleProductSelection(Product selectedProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/coupon/CouponApplyPopup.fxml"));
            Parent root = loader.load();

            CouponApplyPopupController popupCtrl = loader.getController();
            popupCtrl.initData(selectedProduct, couponDAO);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(selectedProduct.getProductName() + " - 쿠폰 적용");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); // 팝업창이 닫힐 때까지 대기
            
            // 팝업창이 닫힌 후, 쿠폰 목록 데이터를 DB에서 다시 불러와 화면 갱신
            this.couponMasterList = couponDAO.getAllCoupons();
            // FilteredList는 원본 리스트의 변경을 자동으로 감지하지 않으므로, 아래와 같이 다시 설정
            this.couponFilteredList = new FilteredList<>(couponMasterList, p -> true);
            couponTable.setItems(this.couponFilteredList);
            // 현재 설정된 필터 값을 그대로 유지하며 목록 다시 필터링
            applyCouponFilter();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
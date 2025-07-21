// CouponController.java

package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.category.CategoryDAO;      // ProductDAO가 의존하므로 임포트
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;           // Product 모델 클래스 (DTO) 임포트
import model.product.ProductDAO;        // ProductDAO 임포트

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

// CouponManagement.fxml의 컨트롤러 클래스
public class CouponController implements Initializable {

    // FXML과 연결된 UI 컴포넌트
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponNameCol;
    @FXML private TableColumn<Coupon, Integer> percentCol;
    @FXML private TableColumn<Coupon, String> startTimeCol;
    @FXML private TableColumn<Coupon, String> deadlineCol;
    @FXML private TextField couponSearchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Number> priceCol; // Integer 대신 Number 사용
    @FXML private TableColumn<Product, Number> costCol;  // Integer 대신 Number 사용
    @FXML private TextField productSearchField;

    private CouponDAO couponDAO;
    private ProductDAO productDAO;

    private ObservableList<Coupon> couponList;
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DAO 객체 생성
        couponDAO = new CouponDAO();
        // ProductDAO는 CategoryDAO를 필요로 하므로 함께 생성
        productDAO = new ProductDAO(new CategoryDAO());

        // 테이블 컬럼과 데이터 모델의 속성 연결
        setupCouponTable();
        setupProductTable(); // 수정된 메소드 호출

        // DB에서 데이터 로드
        loadCouponData();
        loadProductData(); // 수정된 메소드 호출

        // 검색 기능 설정
        setupSearchFunctionality();
        
        // 상품 테이블 행 더블 클릭 이벤트 처리
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                handleProductSelection(productTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** 쿠폰 테이블 초기 설정 */
    private void setupCouponTable() {
        // Coupon 모델은 JavaFX Property를 사용하므로 PropertyValueFactory를 그대로 사용
        couponNameCol.setCellValueFactory(new PropertyValueFactory<>("couponName"));
        percentCol.setCellValueFactory(new PropertyValueFactory<>("percent"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
    }

    /** 
     * 상품 테이블 초기 설정 
     * Product 모델이 일반 POJO(Plain Old Java Object)이므로, 람다식을 사용, 각 셀의 값 설정
     */
    private void setupProductTable() {
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        priceCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()));
        costCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCost()));
    }

    /** 쿠폰 데이터를 로드하여 테이블에 표시 */
    private void loadCouponData() {
        couponList = couponDAO.getAllCoupons();
        couponTable.setItems(couponList);
    }

    /**
     * 상품 데이터를 로드하여 테이블에 표시
     * ProductDAO 사용
     */
    private void loadProductData() {
        try {
            productList = productDAO.findAllProduct();
            productTable.setItems(productList);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("상품 목록 로드 중 오류 발생");
            e.printStackTrace();
            // 사용자에게 오류를 알리는 Alert 창을 띄우는 로직 추가 가능
        }
    }
    
    /** 검색 기능 설정 */
    private void setupSearchFunctionality() {
        // 쿠폰 검색 (클라이언트 측 필터링)
        FilteredList<Coupon> filteredCoupons = new FilteredList<>(couponList, p -> true);
        couponSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCoupons.setPredicate(coupon -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return coupon.getCouponName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        couponTable.setItems(filteredCoupons);

        // 상품 검색 (클라이언트 측 필터링)
        FilteredList<Product> filteredProducts = new FilteredList<>(productList, p -> true);
        productSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return product.getProductName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        productTable.setItems(filteredProducts);
    }

    /**
     * 상품 선택 시 쿠폰 적용 팝업창 띄움
     * @param selectedProduct 선택된 상품 객체
     */
    private void handleProductSelection(Product selectedProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/CouponApplyPopup.fxml")); // 팝업 FXML 경로
            Parent root = loader.load();

            // (선택사항) 팝업 컨트롤러에 선택된 상품 객체 전달
            // CouponApplyPopupController popupController = loader.getController();
            // popupController.initData(selectedProduct);

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
}
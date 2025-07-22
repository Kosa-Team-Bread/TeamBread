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
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;
import model.product.ProductDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CouponController implements Initializable {

    // FXML 필드 선언
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponNameCol;
    @FXML private TableColumn<Coupon, Integer> percentCol;
    @FXML private TableColumn<Coupon, String> startTimeCol;
    @FXML private TableColumn<Coupon, String> deadlineCol;
    @FXML private TextField couponSearchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, Number> priceCol;
    @FXML private TableColumn<Product, Number> costCol;
    @FXML private TextField productSearchField;

    private CouponDAO couponDAO;
    private ProductDAO productDAO;

    private ObservableList<Coupon> couponList;
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        couponDAO = new CouponDAO();
        productDAO = new ProductDAO();

        setupCouponTable();
        setupProductTable();

        loadCouponData();
        loadProductData();

        setupSearchFunctionality();
        
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                handleProductSelection(productTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** [수정] 쿠폰 테이블 초기 설정 (람다식으로 변경) */
    private void setupCouponTable() {
        // 람다식을 사용, 각 셀의 값을 설정
        couponNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouponName()));
        percentCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPercent()).asObject());
        
        // LocalDate를 원하는 형식의 문자열로 변환하여 표시
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)));
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadLine().format(formatter)));
    }

    /** 상품 테이블 초기 설정 */
    private void setupProductTable() {
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        priceCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()));
        costCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCost()));
    }
    
    private void loadCouponData() {
        couponList = couponDAO.getAllCoupons();
        couponTable.setItems(couponList);
    }
    
    private void loadProductData() {
        try {
            productList = productDAO.findAllProduct();
            productTable.setItems(productList);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("상품 목록 로드 중 오류 발생");
            e.printStackTrace();
        }
    }
    
    private void setupSearchFunctionality() {
        FilteredList<Coupon> filteredCoupons = new FilteredList<>(couponList, p -> true);
        couponSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCoupons.setPredicate(coupon -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return coupon.getCouponName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        couponTable.setItems(filteredCoupons);

        FilteredList<Product> filteredProducts = new FilteredList<>(productList, p -> true);
        productSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return product.getProductName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        productTable.setItems(filteredProducts);
    }

    private void handleProductSelection(Product selectedProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popup/CouponApplyPopup.fxml"));
            Parent root = loader.load();

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

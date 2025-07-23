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
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.product.Product;
import model.product.ProductDAO;
import model.category.CategoryDAO;
import model.admin.AdminDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CouponController implements Initializable {

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
        couponDAO = new CouponDAO(new ProductDAO(new CategoryDAO(), new AdminDAO()), new CategoryDAO());
        productDAO = new ProductDAO(new CategoryDAO(), new AdminDAO());

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

    private void setupCouponTable() {
        couponNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCouponName()));
        percentCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getPercent()).asObject());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startTimeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStartTime().format(fmt)));
        deadlineCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDeadLine().format(fmt)));
    }

    private void setupProductTable() {
        productNameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProductName()));
        priceCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getPrice()));
        costCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getCost()));
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
            e.printStackTrace();
        }
    }

    private void setupSearchFunctionality() {
        FilteredList<Coupon> fc = new FilteredList<>(couponList, p -> true);
        couponSearchField.textProperty().addListener((o, ov, nv) ->
            fc.setPredicate(c -> nv == null || nv.isEmpty() || c.getCouponName().toLowerCase().contains(nv.toLowerCase()))
        );
        couponTable.setItems(fc);

        FilteredList<Product> fp = new FilteredList<>(productList, p -> true);
        productSearchField.textProperty().addListener((o, ov, nv) ->
            fp.setPredicate(p -> nv == null || nv.isEmpty() || p.getProductName().toLowerCase().contains(nv.toLowerCase()))
        );
        productTable.setItems(fp);
    }

    /** 더블 클릭 시 쿠폰 적용 팝업 호출 */
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
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

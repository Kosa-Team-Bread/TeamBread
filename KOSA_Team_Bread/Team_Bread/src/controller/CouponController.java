//// CouponManageController.java
//
//package controller;
//
//// (import 문들은 이전과 거의 동일하며, 일부 추가/삭제가 있을 수 있습니다)
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ResourceBundle;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.chart.BarChart;
//import javafx.scene.chart.CategoryAxis;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.XYChart;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import model.Product;
//import util.DBUtil;
//
///**
// * 'CouponManagement.fxml'과 연결되어 UI 이벤트 및 로직을 처리하는 컨트롤러 클래스입니다.
// * Initializable 인터페이스를 구현하여 FXML 로딩 후 초기화 작업을 수행합니다.
// */
//public class CouponManageController implements Initializable {
//
//    // --- FXML과 연결된 UI 요소들 ---
//    @FXML private BorderPane rootPane;
//    @FXML private ComboBox<String> categoryComboBox;
//    @FXML private TextField searchField;
//    @FXML private Button searchButton;
//    @FXML private TableView<Product> productTable;
//
//    // TableView에 데이터를 채우기 위한 ObservableList
//    private ObservableList<Product> productData = FXCollections.observableArrayList();
//
//    /**
//     * FXML 파일이 로드된 후 자동으로 호출되는 초기화 메서드입니다.
//     * UI 컨트롤의 초기 상태 설정, 이벤트 핸들러 등록 등의 작업을 수행합니다.
//     */
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        // 1. TableView와 데이터 리스트 연결
//        productTable.setItems(productData);
//
//        // 2. 카테고리 콤보박스 초기화
//        categoryComboBox.getItems().addAll("전체", "빵", "떡", "케이크");
//        categoryComboBox.setValue("전체");
//
//        // 3. 테이블 로우(row) 더블 클릭 시 상세 팝업 창 열기 이벤트 핸들러 등록
//        productTable.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2 && !productTable.getSelectionModel().isEmpty()) {
//                Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
//                showDetailPopup(selectedProduct);
//            }
//        });
//
//        // 4. 초기 데이터 로드
//        loadInitialData();
//    }
//
//    /**
//     * FXML의 onAction="#searchProducts"에 의해 호출됩니다.
//     * 검색 조건에 따라 상품 목록을 DB에서 조회하여 테이블에 표시합니다.
//     */
//    @FXML
//    private void searchProducts() {
//        String selectedCategory = categoryComboBox.getValue();
//        String searchText = searchField.getText();
//        List<Product> products = new ArrayList<>();
//
//        String sql = "SELECT p.product_id, c.category_name, p.name, p.price, p.cost " +
//                     "FROM tbl_product p " +
//                     "JOIN tbl_category c ON p.category_id = c.category_id " +
//                     "WHERE p.name LIKE ? ";
//
//        if (!"전체".equals(selectedCategory)) {
//            sql += "AND c.category_name = ?";
//        }
//        
//        sql += "ORDER BY c.category_name, p.name";
//
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, "%" + searchText + "%");
//            if (!"전체".equals(selectedCategory)) {
//                pstmt.setString(2, selectedCategory);
//            }
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    products.add(new Product(
//                            rs.getInt("product_id"),
//                            rs.getString("category_name"),
//                            rs.getString("name"),
//                            rs.getInt("price"),
//                            rs.getInt("cost")
//                    ));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("데이터베이스 오류", "상품 정보를 불러오는 중 오류가 발생했습니다.");
//        }
//
//        productData.setAll(products); // 조회된 데이터로 테이블 갱신
//    }
//    
//    /**
//     * 초기 데이터를 로드합니다.
//     */
//    private void loadInitialData() {
//        searchProducts();
//    }
//    
//    // (showDetailPopup, createBepChart, getIssuedCouponCount, issueCoupon, getProductNameById, showAlert 메서드는 이전과 동일합니다.)
//    // ... 이전 코드의 해당 메서드들을 여기에 그대로 복사하여 붙여넣으세요 ...
//
//    //<editor-fold desc="이전과 동일한 팝업 및 DB 관련 메서드 모음">
//    private void showDetailPopup(Product product) {
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle(product.getProductName() + " - 손익분기 분석 및 쿠폰 발행");
//
//        VBox popupRoot = new VBox(20);
//        popupRoot.setPadding(new Insets(25));
//        popupRoot.setAlignment(Pos.CENTER);
//        
//        Label chartTitle = new Label("할인율별 예상 손익 (1개 판매 기준)");
//        chartTitle.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 16));
//        
//        BarChart<String, Number> bepChart = createBepChart(product);
//        
//        GridPane infoGrid = new GridPane();
//        infoGrid.setHgap(10);
//        infoGrid.setVgap(10);
//        infoGrid.setAlignment(Pos.CENTER);
//        
//        infoGrid.add(new Label("상품명:"), 0, 0);
//        infoGrid.add(new Label(product.getProductName()), 1, 0);
//
//        int issuedCount = getIssuedCouponCount(product.getProductId());
//        infoGrid.add(new Label("현재 발행된 쿠폰 수:"), 0, 1);
//        infoGrid.add(new Label(issuedCount + " 매"), 1, 1);
//        
//        infoGrid.add(new Label("할인율 선택:"), 0, 2);
//        ComboBox<Integer> discountCombo = new ComboBox<>();
//        discountCombo.getItems().addAll(5, 10, 15, 20, 25, 30);
//        discountCombo.setValue(10);
//        infoGrid.add(discountCombo, 1, 2);
//        
//        Button issueButton = new Button("선택한 할인율로 쿠폰 발행");
//        issueButton.setOnAction(e -> {
//            int discount = discountCombo.getValue();
//            if (issueCoupon(product.getProductId(), discount)) {
//                showAlert("성공", product.getProductName() + " " + discount + "% 할인 쿠폰이 성공적으로 발행되었습니다.");
//                popupStage.close();
//                searchProducts(); 
//            } else {
//                showAlert("실패", "쿠폰 발행 중 오류가 발생했습니다.");
//            }
//        });
//
//        popupRoot.getChildren().addAll(chartTitle, bepChart, infoGrid, issueButton);
//        
//        Scene scene = new Scene(popupRoot, 600, 650);
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//    }
//    
//    private BarChart<String, Number> createBepChart(Product product) {
//        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("할인율 (%)");
//
//        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("예상 손익 (원)");
//        
//        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
//        barChart.setLegendVisible(false);
//
//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        series.setName("손익 분석");
//
//        int price = product.getPrice();
//        int cost = product.getCost();
//        
//        int[] discountRates = {0, 5, 10, 15, 20, 25, 30};
//        for (int rate : discountRates) {
//            double discountedPrice = price * (1 - rate / 100.0);
//            double profit = discountedPrice - cost;
//            series.getData().add(new XYChart.Data<>(String.valueOf(rate), profit));
//        }
//        
//        barChart.getData().add(series);
//        return barChart;
//    }
//    
//    private int getIssuedCouponCount(int productId) {
//        String sql = "SELECT COUNT(*) FROM tbl_coupon WHERE product_id = ?";
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, productId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//    
//    private boolean issueCoupon(int productId, int discountPercent) {
//        String sql = "INSERT INTO tbl_coupon (coupon_id, product_id, coupon_name, percent, starttime, deadline) " +
//                     "VALUES (coupon_seq.NEXTVAL, ?, ?, ?, ?, ?)";
//        
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            
//            String couponName = getProductNameById(productId) + " " + discountPercent + "% 할인 쿠폰";
//            LocalDate today = LocalDate.now();
//            LocalDate deadline = today.plusDays(7);
//
//            pstmt.setInt(1, productId);
//            pstmt.setString(2, couponName);
//            pstmt.setInt(3, discountPercent);
//            pstmt.setDate(4, java.sql.Date.valueOf(today));
//            pstmt.setDate(5, java.sql.Date.valueOf(deadline));
//            
//            int affectedRows = pstmt.executeUpdate();
//            return affectedRows > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    
//    private String getProductNameById(int productId) {
//        String sql = "SELECT name FROM tbl_product WHERE product_id = ?";
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, productId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getString("name");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//    
//    private void showAlert(String title, String message) {
//        Alert alert = new Alert(AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//    //</editor-fold>
//}
package controller;


package controller.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.admin.AdminDAO;
import model.category.Category;
import model.category.CategoryDAO;
import model.image.ImageDAO;
import model.product.ProductDAO;
import model.stock.Stock;
import model.stock.StockDAO;
import util.AlertUtil;

// Made By 정영규
public class StockController {

    @FXML private TableView<Stock> stockTableView;
    @FXML private TableColumn<Stock, String> stockNameColumn;
    @FXML private TableColumn<Stock, Integer> stockQuantityColumn;
    @FXML private TableColumn<Stock, String> locationColumn;
    @FXML private TableColumn<Stock, String> stockRegDateColumn;
    @FXML private TableColumn<Stock, String> stockModDateColumn;
    @FXML private TableColumn<Stock, String> categoryNameColumn;

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField searchField;

    private final CategoryDAO categoryDao = new CategoryDAO();
    private final StockDAO    stockDao    = new StockDAO(categoryDao);
    private Map<Integer, String> categoryMap = new HashMap<>();

    @FXML
    public void initialize() {
        cacheCategoryMap();
        setupTableColumns();
        setupRowClick();

        ProductDAO.setCategoryDAO(categoryDao);
        ProductDAO.setImageDAO(new ImageDAO(categoryDao));
        ProductDAO.setAdminDAO(new AdminDAO());

        loadCategoryComboBox();
        loadAllStock();
        setupCategoryFilter();
    }

    // 매번 조회시 Map에서 저장하여 쿼리 중복 방지
    private void cacheCategoryMap() {
        try {
            for (Category cate : categoryDao.findAllCategory()) {
                categoryMap.put(cate.getCategoryId(), cate.getCategoryName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 조회 데이터 삽입
    private void setupTableColumns() {
        stockNameColumn.setCellValueFactory(cd ->
            Bindings.createStringBinding(() -> cd.getValue().getStockName())
        );
        stockQuantityColumn.setCellValueFactory(cd ->
            Bindings.createObjectBinding(() -> cd.getValue().getStockQuantity())
        );
        locationColumn.setCellValueFactory(cd ->
            Bindings.createStringBinding(() -> cd.getValue().getLocation())
        );
        stockRegDateColumn.setCellValueFactory(cd ->
            Bindings.createStringBinding(() -> cd.getValue().getStockRegDate().toString())
        );
        stockModDateColumn.setCellValueFactory(cd ->
            Bindings.createStringBinding(() -> cd.getValue().getStockModDate().toString())
        );
        categoryNameColumn.setCellValueFactory(cd ->
            Bindings.createStringBinding(() -> {
                Integer cateId = cd.getValue().getCategoryId();
                return categoryMap.getOrDefault(cateId, "알 수 없음");
            })
        );
    }

    // 더블 클릭 이벤트 처리
    private void setupRowClick() {
        stockTableView.setRowFactory(tv -> {
            TableRow<Stock> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    int pid = row.getItem().getProductId();
                    showDetailPopup(pid);
                }
            });
            return row;
        });
    }

    // 카테고리 combox박스 삽입
    private void loadCategoryComboBox() {
        try {
            categoryComboBox.getItems().add("전체");
            categoryComboBox.getItems().addAll(
                new ArrayList<>(
                    categoryDao.findAllCategory().stream()
                        .map(Category::getCategoryName)
                        .collect(Collectors.toList())
                )
            );
            categoryComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 전체조회
    private void loadAllStock() {
        try {
            ObservableList<Stock> stockList = stockDao.findAllStock();
            stockTableView.setItems(stockList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 카테고리 검색
    private void setupCategoryFilter() {
        categoryComboBox.setOnAction(evt -> {
            String sel = categoryComboBox.getValue();
            if ("전체".equals(sel)) {
                loadAllStock();
            } else {
                try {
                    stockTableView.setItems(stockDao.getStockFromCategory(sel));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // 상품검색
    @FXML
    private void searchProducts(ActionEvent event) {
        String text = searchField.getText();
        if (text == null || text.trim().isEmpty()) {
            loadAllStock();
        } else {
            try {
                stockTableView.setItems(stockDao.searchStock(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 상품 삽입
    @FXML
    private void addProduct(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/stock/ProductAddPopup.fxml")
            );
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("상품 추가");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadAllStock();
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtil.showError("팝업 오류", ex.getMessage());
        }
    }

    // 더블클릭 시 상품 상세 팝업창 처리
    private void showDetailPopup(int productId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/stock/ProductDetailPopup.fxml")
            );
            Parent root = loader.load();
            controller.stock.ProductDetailPopupController ctrl = loader.getController();
            ctrl.setProductId(productId);

            Stage popup = new Stage();
            popup.initOwner(stockTableView.getScene().getWindow());
            popup.setTitle("상품 상세 정보");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root));
            popup.showAndWait();

            loadAllStock();
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtil.showError("상세 팝업 오류", ex.getMessage());
        }
    }
}

// CouponApplyPopupController.java

package controller.coupon;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.product.Product;

public class CouponApplyPopupController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label costLabel;
    @FXML private BarChart<String, Number> bepChart;    // 차트
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button closeButton;

    private Product selectedProduct;
    private final DecimalFormat formatter = new DecimalFormat("#,##0");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // BarChart가 StackedBarChart처럼 보이도록 설정
        bepChart.setCategoryGap(50);    // 막대 간 간격 조정
    }

    /**
     * CouponController로부터 상품 데이터를 받아와 필드에 저장, 모든 UI 업데이트
     * @param product CouponController에서 더블클릭된 상품 객체
     */
    public void initData(Product product) {
        this.selectedProduct = product;
        
        // 1. 상품 기본 정보 표시
        updateProductInfo();
        
        // 2. BEP 차트 생성 및 데이터 설정
        createAndSetChartData();
    }
    
    /** 받아온 상품 정보로 상단의 라벨들 업데이트 */
    private void updateProductInfo() {
        titleLabel.setText("상품명 : " + selectedProduct.getProductName());
        priceLabel.setText(formatter.format(selectedProduct.getPrice()) + " 원");
        costLabel.setText(formatter.format(selectedProduct.getCost()) + " 원");
    }

    /**
     * 할인율에 따른 손익분기점(BEP) 계산, 차트 데이터 생성
     */
    private void createAndSetChartData() {
        // 기존 차트 데이터 초기화
        bepChart.getData().clear();

        // 계산할 할인율 목록
        List<Integer> discountRates = Arrays.asList(3, 5, 10, 15, 30);
        
        // 차트에 표시할 두 개의 데이터 시리즈 생성 (원가, 이익)
        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        costSeries.setName("원가");

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("이익");

        // 각 할인율에 대해 반복하며 데이터 계산
        for (int rate : discountRates) {
            // 1. 최종 판매가격 계산
            double finalPrice = selectedProduct.getPrice() * (1 - (rate / 100.0));
            
            // 2. 이익 계산 (최종 판매가 - 원가)
            double profit = finalPrice - selectedProduct.getCost();

            // 3. 계산된 데이터를 각 시리즈에 추가
            String category = rate + "%";
            costSeries.getData().add(new XYChart.Data<>(category, selectedProduct.getCost()));
            profitSeries.getData().add(new XYChart.Data<>(category, profit));
        }

        // 완성된 데이터 시리즈들을 차트에 추가
        addChartData(costSeries, profitSeries);
    }

    /**
     * 차트에 데이터 시리즈를 안전하게 추가
     * @SafeVarargs 어노테이션으로 타입 안정성 경고 제거
     */
    @SafeVarargs
    private final void addChartData(XYChart.Series<String, Number>... series) {
        bepChart.getData().addAll(series);
    }
    
    /** '닫기' 버튼을 클릭했을 때 호출되는 메소드 */
    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
// CouponApplyPopupController.java
package controller.coupon;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.coupon.CouponDAO;
import model.product.Product;

import java.time.LocalDate;

public class CouponApplyPopupController {

    @FXML private WebView chartWebView;
    @FXML private Label priceLabel;
    @FXML private Label costLabel;
    @FXML private TextField discountField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button applyButton;

    private WebEngine webEngine;
    private Product selectedProduct;
    private CouponDAO couponDAO;

    @FXML
    public void initialize() {
       
        webEngine = chartWebView.getEngine();
        applyButton.setOnAction(e -> applyCoupon());
        discountField.textProperty().addListener((obs, o, n) -> updateChart());
    }

    /**
     * 팝업 호출 시 상품과 DAO를 초기화하는 메서드
     */
    public void initData(Product product, CouponDAO couponDAO) {
        this.selectedProduct = product;
        this.couponDAO = couponDAO;

        priceLabel.setText(String.valueOf(product.getPrice()));
        costLabel.setText(String.valueOf(product.getCost()));

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30));
        discountField.setText("0");

        updateChart();
    }

    /** 할인율, 가격, 원가 데이터를 반영해 차트를 갱신 */
    private void updateChart() {
        int P = selectedProduct.getPrice();
        int C = selectedProduct.getCost();
        double D;
        try {
            int pct = Integer.parseInt(discountField.getText());
            D = Math.max(0, Math.min(pct, 100)) / 100.0;
        } catch (NumberFormatException e) {
            D = 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
          .append("<script src='https://www.gstatic.com/charts/loader.js'></script>")
          .append("<script>")
          .append("google.charts.load('current',{packages:['corechart']});")
          .append("google.charts.setOnLoadCallback(drawChart);")
          .append("function drawChart(){")
          .append("var data=new google.visualization.DataTable();")
          .append("data.addColumn('number','할인율');")
          .append("data.addColumn('number','이득');")
          .append("data.addColumn('number','손해');")
          .append("var rows=[];")
          .append("for(var i=0;i<=100;i++){")
          .append("  var d=i/100.0;")
          .append("  var disc=").append(P).append("*(1-d);")
          .append("  var profit=Math.max(disc-").append(C).append(",0);")
          .append("  var loss=Math.max(").append(C).append("-disc,0);")
          .append("  rows.push([i,profit,loss]);")
          .append("}")
          .append("data.addRows(rows);")
          .append("var options={")
          .append("  title:'할인율에 따른 이득 및 손해',")
          .append("  legend:{position:'top'},")
          .append("  hAxis:{title:'할인율 (%)'},")
          .append("  vAxis:{title:'금액 (원)'},")
          .append("  pointSize:6")
          .append("};")
          .append("new google.visualization.ScatterChart(")
          .append("    document.getElementById('chart_div')")
          .append(").draw(data,options);")
          .append("}")
          .append("</script></head><body>")
          .append("<div id='chart_div' style='width:450px;height:360px;'></div>")
          .append("</body></html>");

        webEngine.loadContent(sb.toString());
    }

    /** 쿠폰을 DB에 삽입 후 팝업 닫기 */
    private void applyCoupon() {
        try {
            int percent = Integer.parseInt(discountField.getText());
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            couponDAO.insertCoupon(
                selectedProduct.getProductId(),
                percent,
                start,
                end
            );

            ((Stage) applyButton.getScene().getWindow()).close();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(
                Alert.AlertType.ERROR,
                "쿠폰 등록 중 오류:\n" + ex.getClass().getSimpleName()
                + " - " + ex.getMessage()
            ).showAndWait();
        }
    }
}

// CouponEditPopupController

package controller.coupon;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.coupon.Coupon;
import model.coupon.CouponDAO;
import model.coupon.CouponDTO_Update;

// Made By 김기성
/**
 * 쿠폰 수정 팝업창(CouponEditPopup.fxml)의 UI 이벤트 & 로직을 처리하는 컨트롤러 클래스
 */
public class CouponEditPopupController implements Initializable {
    // fxml UI 컴포넌트
    @FXML private TextField couponNameField;
    @FXML private TextField percentField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // --- 비즈니스 로직을 위한 필드 ---
    private Coupon editingCoupon;       // 메인 컨트롤러로부터 전달받은, 현재 수정 중인 쿠폰 객체
    private CouponDAO couponDAO;        // DB 업데이트를 위한 DAO 객체
    private Map<String, Integer> categoryNameToIdMap;   // 카테고리 이름 -> ID 변환용 Map
    private boolean updated = false;    // 수정 작업이 성공적으로 완료되었는지 여부를 저장하는 플래그

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    // CouponController로부터 수정할 쿠폰 객체와 DAO, 카테고리 정보를 받아 초기화
    public void initData(Coupon coupon, CouponDAO couponDAO, Map<Integer, String> categoryIdToNameMap) {
        this.editingCoupon = coupon;
        this.couponDAO = couponDAO;

        // 카테고리 이름 -> ID 맵을 생성 (ComboBox 선택 시 ID를 찾기 위함)
        this.categoryNameToIdMap = categoryIdToNameMap.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // UI 컨트롤에 기존 쿠폰 데이터 채우기
        couponNameField.setText(coupon.getCouponName());
        percentField.setText(String.valueOf(coupon.getPercent()));
        startDatePicker.setValue(coupon.getStartTime());
        endDatePicker.setValue(coupon.getDeadLine());
        
        // 카테고리 ComboBox 채우기 및 현재 쿠폰의 카테고리 선택
        categoryComboBox.getItems().setAll(categoryNameToIdMap.keySet());
        String currentCategoryName = categoryIdToNameMap.get(coupon.getCategoryId());
        categoryComboBox.setValue(currentCategoryName);
    }

    /** 
     * '저장' 버튼 클릭 시 실행될 로직
     * 사용자 입력값 검증, 유효하다면 DB에 업데이트 요청
    */
    @FXML
    void handleSaveAction(ActionEvent event) {
        if (!validateInput()) {
            return; // 입력값 검증 실패 시, 더 이상 진행 x
        }

        try {
            // UI 컨트롤에서 사용자가 수정한 값을 가져와 CouponDTO_Update 객체 생성
            CouponDTO_Update dto = CouponDTO_Update.builder()
                .couponId(editingCoupon.getCouponId())
                .couponName(couponNameField.getText())
                .percent(Integer.parseInt(percentField.getText()))
                .startTime(startDatePicker.getValue())
                .deadline(endDatePicker.getValue())
                .categoryId(categoryNameToIdMap.get(categoryComboBox.getValue()))
                .productId(editingCoupon.getProductId()) // 상품 ID는 변경되지 않음
                .build();
            
            // DAO를 통해 DB 업데이트 실행
            couponDAO.updateCoupon(dto);
            this.updated = true; // DB 업데이트 성공 시, 성공 플래그 true로 설정
            closeStage();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "입력 오류", "할인율은 숫자로 입력해야 합니다.");
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "DB 오류", "데이터베이스 저장 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    // '취소' 버튼 클릭 시 실행될 로직
    @FXML
    void handleCancelAction(ActionEvent event) {
        closeStage();
    }

    // 팝업창(Stage) 을 닫는 공통 메소드 
    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    // 수정 작업이 성공적으로 완료되었는지 여부를 반환 
    // @return 수정이 성공적으로 완료: true, 그렇지 않으면: false
    public boolean isUpdated() {
        return this.updated;
    }
   
    // 사용자 입력값 유효성 검증 메소드
    private boolean validateInput() {
        String couponName = couponNameField.getText();
        String percentText = percentField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String category = categoryComboBox.getValue();

        if (couponName == null || couponName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "쿠폰명을 입력해주세요.");
            return false;
        }
        if (percentText == null || percentText.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "할인율을 입력해주세요.");
            return false;
        }
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "시작 시간과 종료 시간을 모두 선택해주세요.");
            return false;
        }
        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "종료 시간은 시작 시간보다 이후여야 합니다.");
            return false;
        }
        if (category == null) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "카테고리를 선택해주세요.");
            return false;
        }
        // 숫자 형식 검증
        try {
            int percent = Integer.parseInt(percentText);
            if (percent <= 0 || percent > 100) {
                 showAlert(Alert.AlertType.WARNING, "입력 오류", "할인율은 1에서 100 사이의 숫자여야 합니다.");
                 return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", "할인율은 숫자로만 입력해주세요.");
            return false;
        }

        return true;
    }
    
    // Alert 창을 띄우는 공통 헬퍼 메소드 
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
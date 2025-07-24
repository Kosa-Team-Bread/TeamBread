package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

//Made By 정영규
public class AlertUtil {

	// 알림메세지 - 정보
    public static void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 알림메세지 - 에러
    public static void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 알림메세지 - 경고
    public static void showWarning(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Made By 강기범
    // 알림 메세지 - 회원가입및 회원가입시
 	public static void showAlert(Alert.AlertType type, String title, String message) {
 		Alert alert = new Alert(type);
 		alert.setTitle(title);
 		alert.setHeaderText(null);
 		alert.setContentText(message);
 		alert.showAndWait();
 	}
}

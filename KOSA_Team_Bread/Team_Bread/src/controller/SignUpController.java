package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;
import util.ValidationUtil;

public class SignUpController implements Initializable {

	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField passwordConfirmField;
	@FXML
	private Button signupButton;

	private AdminDAO adminDAO;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminDAO = new AdminDAO();

		// 이메일 포커스 확인
		emailField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue && !emailField.getText().trim().isEmpty()) {
					checkEmailDuplicateRealtime();
				}
			}
		});

		// 비밀번호 포커스 확인
		passwordConfirmField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue && !passwordConfirmField.getText().isEmpty()) {
					checkPasswordMatchRealtime();
				}
			}
		});
	}
	
	// 회원가입
	@FXML
	private void handleSignUp(ActionEvent event) {
		String name = nameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String passwordConfirm = passwordConfirmField.getText().trim();

		// 입력값 검증
		if (!validateInput(name, email, password, passwordConfirm)) {
			return;
		}

		try {
			// 이메일 중복 확인
			if (adminDAO.checkEmailDuplicate(email)) {
				showAlert(Alert.AlertType.WARNING, "회원가입 실패", "이미 사용 중인 이메일입니다.");
				emailField.requestFocus();
				return;
			}

			// Admin 객체 생성
			Admin newAdmin = new Admin(name, email, password);

			// 회원가입 처리
			boolean success = adminDAO.signupAdmin(newAdmin);

			if (success) {
				showAlert(Alert.AlertType.INFORMATION, "회원가입 성공", "회원가입이 완료되었습니다.\n로그인 화면으로 이동합니다.");
				goToLogin(event);
			} else {
				showAlert(Alert.AlertType.ERROR, "회원가입 실패", "회원가입 처리 중 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.");
			}

		} catch (SQLException | ClassNotFoundException e) {
			System.err.println("회원가입 처리 중 오류 발생: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "시스템 오류", "데이터베이스 오류가 발생했습니다.");
		}
	}

	// 로그인 클릭 이벤트
	@FXML
	private void onGotoLogin(MouseEvent event) {
		goToLogin(event);
	}

	// 입력값 검증
	private boolean validateInput(String name, String email, String password, String passwordConfirm) {
		// 필수 입력값 확인
		if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "모든 필드를 입력해주세요.");
			return false;
		}
		if (name.length() < 2 || name.length() > 10) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "이름은 2자 이상 10자 이하로 입력해주세요.");
			nameField.requestFocus();
			return false;
		}

		if (!ValidationUtil.isValidEmail(email)) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "올바른 이메일 형식이 아닙니다.");
			emailField.requestFocus();
			return false;
		}

		if (password.length() < 4) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "비밀번호는 4자 이상으로 입력해주세요.");
			passwordField.requestFocus();
			return false;
		}
		if (!password.equals(passwordConfirm)) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "비밀번호가 일치하지 않습니다.");
			passwordConfirmField.requestFocus();
			return false;
		}
		return true;
	}

	// 이메일 중복 검사
	private void checkEmailDuplicateRealtime() {
		String email = emailField.getText().trim();

		// 비어있거나, 이메일 형식에 맞는지 검사
		if (!email.isEmpty() && ValidationUtil.isValidEmail(email)) {
			try {
				// 중복검사
				if (adminDAO.checkEmailDuplicate(email)) {
					emailField.setStyle(null);
				} else {
					emailField.setStyle(null);
				}
			} catch (SQLException | ClassNotFoundException e) {
				System.err.println("이메일 중복 검사 중 오류: " + e.getMessage());
			}
		} else {
			emailField.setStyle(null);
		}
	}

	// 비밀번호 확인
	private void checkPasswordMatchRealtime() {
		String password = passwordField.getText();
		String passwordConfirm = passwordConfirmField.getText();

		if (!passwordConfirm.isEmpty()) {
			if (password.equals(passwordConfirm)) {
				passwordConfirmField.setStyle(null);
			} else {
				passwordConfirmField.setStyle(null);
			}
		} else {
			passwordConfirmField.setStyle(null);
		}
	}

	// 로그인 화면 이동
	private void goToLogin(Object event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
			Parent loginRoot = loader.load();

			Scene loginScene = new Scene(loginRoot);
			Stage stage;

			if (event instanceof MouseEvent) {
				stage = (Stage) ((Node) ((MouseEvent) event).getSource()).getScene().getWindow();
			} else {
				stage = (Stage) ((Node) ((ActionEvent) event).getSource()).getScene().getWindow();
			}
			stage.setScene(loginScene);
			stage.setTitle("성심당 할인관리스템 - 로그인");

		} catch (IOException e) {
			System.err.println("로그인 화면 로드 실패: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "화면 오류", "로그인 화면을 불러오는데 실패했습니다.");
		}
	}

	// 알림 메세지
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}

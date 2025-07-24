package controller.sign;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;
import util.Session;
import util.ValidationUtil;

public class LoginController implements Initializable {
	
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private CheckBox rememberCheck;
	@FXML
	private Button loginButton;
	
	private AdminDAO adminDAO;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminDAO = new AdminDAO();
		
		// 로그인 이메일 기억하기
		loadRememberedEmail();
		
		passwordField.setOnAction(this::onLogin);
	}

	// 로그인 버튼 클릭시 이벤트 처리
	@FXML
	private void onLogin(ActionEvent event) {
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		
		// 입력값 확인
		if (!validateInput(email, password)) {
			return;
		}
		
		try {
			// 로그인 인증
			Admin admin = adminDAO.loginAdmin(email, password);
			
			if (admin != null) {
				// 로그인 성공
				handleLoginSuccess(admin, email, event);
			} else {
				// 로그인 실패
				showAlert(Alert.AlertType.ERROR, "로그인 실패", "이메일 또는 비밀번호가 잘못되었습니다.");
				passwordField.clear();
				passwordField.requestFocus();
			}
		} catch (SQLException | ClassNotFoundException e) {
			System.err.println("로그인 처리 중 오류 발생: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "시스템 오류", "로그인 처리 중 오류가 발생했습니다.");
		}
	}

	// 회원가입 링크 클릭 이벤트 처리
	@FXML
	private void onGotoSignup(MouseEvent event) {
		try {
			// 회원가입 화면으로 전환
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/sign/signup.fxml"));
			Parent signupRoot = loader.load();
			
			Scene signupScene = new Scene(signupRoot);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(signupScene);
			stage.setTitle("성심당 할인관리 시스템 - 회원가입");
		} catch (IOException e) {
			System.err.println("회원가입 화면 로드 실패: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "화면오류", "회원가입 화면을 불러오는데 실패했습니다.");
		}
	}
	
	// 입력값 확인
	private boolean validateInput(String email, String password) {
		if(email.isEmpty() || password.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "이메일과 비밀번호를 모두 입력해주세요.");
			return false;
		}
		
		// 이메일 형식 검증
		if (!ValidationUtil.isValidEmail(email)) {
			showAlert(Alert.AlertType.WARNING, "입력 오류", "올바른 이메일 형식이 아닙니다.");
			emailField.requestFocus();
			return false;
		}
		return true;
	}
	
	// 로그인 성공 처리
	private void handleLoginSuccess(Admin admin, String email, ActionEvent event) {
		// 세션에 사용자 정보 저장
		Session.setCurrentUser(admin);
		
		// 아이디 기억하기 처리
		if (rememberCheck.isSelected()) {
			saveRememberedEmail(email);
		} else {
			clearRememberedEmail();
		}
		
		showAlert(Alert.AlertType.INFORMATION, "로그인 성공", admin.getAdminName() + "님, 환영합니다!");
		
		// 메인화면으로 전환
		loadMainScreen(event);
		
	}
	
	// 메인화면 로드
	private void loadMainScreen(ActionEvent event) {
		try {
			// 메인화면 FXML 파일로 변경
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main/MainPage.fxml"));
			Parent mainRoot = loader.load();
			
			Scene mainScene = new Scene(mainRoot, 1440, 900);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(mainScene);
			stage.setTitle("성심당 할인관리 시스템 - 메인");
			
			// 창 닫기시 로그아웃
			stage.setOnCloseRequest(e -> {
				Session.clear();
				System.out.println("로그아웃 되었습니다.");
			});
		} catch (IOException e) {
			System.err.println("메인 화면 로드 실패: " + e.getMessage());
			showAlert(Alert.AlertType.INFORMATION, "로그인 완료", "로그인이 완료되었습니다. \n 메인화면 구현 후 연결됩니다.");
		}
		
	}
	
	// 저장된 이메일 불러오기
	private void loadRememberedEmail() {
		String saveEmail = System.getProperty("remembered.email", "");
		if(!saveEmail.isEmpty()) {
			emailField.setText(saveEmail);
			rememberCheck.setSelected(true);
			passwordField.requestFocus();
		}
		
	}
	
	// 이메일 저장
	private void saveRememberedEmail(String email) {
		System.setProperty("remembered.email", email);
		
	}
	
	// 저장된 이메일 삭제
	private void clearRememberedEmail() {
		System.setProperty("remembered.email", "");
		
	}
	
	
	private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

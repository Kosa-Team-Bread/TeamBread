package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;

public class ProfileEditController implements Initializable {
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField newPasswordField;
	@FXML
	private Button cancelButton;
	@FXML
	private Button saveButton;

	// 현재 사용자 정보 (부모 창에서 전달받을 데이터)
	private Admin currentUser;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 초기화 로직
	}

	// 현재 유저 상태 설정 메서드
	public void setCurrentUser(Admin user) {
		this.currentUser = user;

		// 사용자 정보에 따라 UI 초기화
		if (user != null) {
			nameField.setText(user.getAdminName());
			emailField.setText(user.getEmail());
		}
	}

	@FXML
	private void handleCancel() {
		((Stage) cancelButton.getScene().getWindow()).close();
	}

	@FXML
	private void handleSave() {
		// 저장 로직
		if (currentUser != null) {
			try {
				String adminName = nameField.getText();
				String adminEmail = emailField.getText();
				String newPassword = newPasswordField.getText();

				// 올바른 패스워드 처리
				String pw;
				if (newPassword == null || newPassword.trim().isEmpty()) {
					// 새 패스워드가 입력되지 않았으면 기존 패스워드 유지
					pw = currentUser.getPw();
				} else {
					// 새 패스워드가 입력되었으면 새 패스워드 사용
					pw = newPassword;
				}

				System.out.println(
						adminName + " " + adminEmail + " " + pw + " " + currentUser.toString() + " " + newPassword);

				// 데이터베이스에 업데이트하는 로직
				AdminDAO.updateAdmin(adminName, adminEmail, pw, currentUser.getAdminId());

				// 로컬 객체도 업데이트
				currentUser.setAdminName(adminName);
				currentUser.setEmail(adminEmail);
				currentUser.setPw(pw);

				System.out.println("회원 정보가 성공적으로 변경되었습니다.");

			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("회원 정보 변경에 실패했습니다: " + e.getMessage());
			}
		}

		((Stage) saveButton.getScene().getWindow()).close();
	}
}

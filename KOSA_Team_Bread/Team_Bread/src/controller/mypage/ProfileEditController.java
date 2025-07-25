package controller.mypage;

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
import util.AlertUtil;

//Made By 나규태 + CHATGPT
public class ProfileEditController implements Initializable {
	// 이름, 이메일, 새 비밀번호 입력 필드
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField newPasswordField;

	// 취소 및 저장 버튼
	@FXML
	private Button cancelButton;
	@FXML
	private Button saveButton;

	// 현재 로그인한 사용자 정보
	private Admin currentUser;

	// DB 접근을 위한 관리자 DAO 인스턴스 생성
	private final AdminDAO adminDAO = new AdminDAO();

	// 저장 성공 여부
	private boolean isSuccessful = false;

	// 저장 성공 여부 반환
	public boolean isSuccessful() {
		return isSuccessful;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 초기화 시 별도 로직 없음
	}

	// 외부에서 현재 로그인한 사용자 설정 시 호출
	public void setCurrentUser(Admin user) {
		this.currentUser = user;

		// 사용자 정보에 따라 UI 초기화
		if (user != null) {
			nameField.setText(user.getAdminName());
			emailField.setText(user.getEmail());
		}
	}

	// [취소] 버튼 클릭 시 창 닫기
	@FXML
	private void handleCancel() {
		((Stage) cancelButton.getScene().getWindow()).close();
	}

	// [저장] 버튼 클릭 시 사용자 정보 DB 업데이트
	@FXML
	private void handleSave() {
		// 저장 로직
		if (currentUser != null) {
			try {
				String adminName = nameField.getText();
				String adminEmail = emailField.getText();
				String newPassword = newPasswordField.getText();

				// 올바른 패스워드 처리
				if (newPassword == null || newPassword.trim().isEmpty()) {
					// 새 패스워드가 입력되지 않았으면 기존 패스워드 유지 후 데이터베이스 업데이트
					adminDAO.updateAdmin(adminName, adminEmail, currentUser.getAdminId());
				} else {
					// 새 패스워드가 입력되었으면 새 패스워드 사용 후 데이터베이스 업데이트
					adminDAO.updateAdmin(adminName, adminEmail, newPassword, currentUser.getAdminId());
				}

				// 로컬 객체도 업데이트
				currentUser.setAdminName(adminName);
				currentUser.setEmail(adminEmail);

				// 성공 알림
				this.isSuccessful = true;
				AlertUtil.showInfo("등록 성공", "회원 정보가 성공적으로 변경되었습니다.");

				((Stage) saveButton.getScene().getWindow()).close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				AlertUtil.showError("등록 실패", "회원 정보 변경에 실패했습니다: " + e.getMessage());
				this.isSuccessful = false;
			}
		}

	}
}

package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;

public class RoleEditController implements Initializable {
	@FXML
	private RadioButton managerRadio;
	@FXML
	private RadioButton adminRadio;
	@FXML
	private ToggleGroup roleToggleGroup;
	@FXML
	private Button cancelButton;
	@FXML
	private Button saveButton;

	// 현재 사용자 정보 (부모 창에서 전달받을 데이터)
	private Admin currentUser;

	private final AdminDAO adminDAO = new AdminDAO();
	
	private boolean isSuccessful = false;

	public boolean isSuccessful() {
		return isSuccessful;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 초기화 로직
	}

	// 현재 유저 상태 설정 메서드
	public void setCurrentUser(Admin user) {
		this.currentUser = user;

		// 사용자 정보에 따라 UI 초기화
		if (user != null) {
			// 현재 등급에 따라 라디오 버튼 선택
			if (user.getGrade() == 1) { // 관리자
				adminRadio.setSelected(true);
			} else if (user.getGrade() == 2) {
				managerRadio.setSelected(true); // 사장
			}
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

				int newGrade = 0;
				// 선택된 라디오 버튼에 따라 등급 설정
				if (adminRadio.isSelected()) {
					newGrade = 1;

				} else if (managerRadio.isSelected()) {
					newGrade = 2;
				}

				// 데이터베이스에 저장하는 로직
				adminDAO.updateAdminGrade(newGrade, currentUser.getAdminId());

				// 로컬 객체도 업데이트
				currentUser.setGrade(newGrade);
				this.isSuccessful = true;
				System.out.println("등급이 성공적으로 변경되었습니다.");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("등급 변경에 실패했습니다: " + e.getMessage());
				this.isSuccessful = false;
			}
		}

		((Stage) saveButton.getScene().getWindow()).close();
	}
}

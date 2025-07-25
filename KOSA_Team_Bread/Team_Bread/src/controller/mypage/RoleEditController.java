package controller.mypage;

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
import util.AlertUtil;

//Made By 나규태 + CHATGPT
public class RoleEditController implements Initializable {
	// 라디오 버튼: 관리자, 점장
	@FXML
	private RadioButton managerRadio;
	@FXML
	private RadioButton adminRadio;
	@FXML
	private ToggleGroup roleToggleGroup;

	// 취소 및 저장 버튼
	@FXML
	private Button cancelButton;
	@FXML
	private Button saveButton;

	// 현재 등급 변경 대상인 사용자
	private Admin currentUser;

	// 관리자 DAO 인스턴스 생성
	private final AdminDAO adminDAO = new AdminDAO();

	// 저장 성공 여부
	private boolean isSuccessful = false;

	public boolean isSuccessful() {
		return isSuccessful;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 초기화 시 별도 로직 없음
	}

	// 외부에서 사용자 정보 세팅 시 호출
	public void setCurrentUser(Admin user) {
		this.currentUser = user;

		// 사용자 정보에 따라 UI 초기화
		if (user != null) {
			// 현재 등급에 따라 라디오 버튼 선택
			if (user.getGrade() == 1) { // 관리자
				adminRadio.setSelected(true);
			} else if (user.getGrade() == 2) {
				managerRadio.setSelected(true); // 점장
			}
		}
	}

	// [취소] 버튼 클릭 시 창 닫기
	@FXML
	private void handleCancel() {
		((Stage) cancelButton.getScene().getWindow()).close();
	}

	// [저장] 버튼 클릭 시 등급 변경 처리
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

				AlertUtil.showInfo("등록 성공", "등급이 성공적으로 변경되었습니다.");

				((Stage) saveButton.getScene().getWindow()).close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				AlertUtil.showError("등록 실패", "등급 변경에 실패했습니다: " + e.getMessage());
				this.isSuccessful = false;
			}
		}

	}
}

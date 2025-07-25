package controller.mypage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.admin.Admin;
import model.admin.AdminDAO;
import util.Session;

// Made By 나규태 + CHAT
public class MyPageController implements Initializable {
	// FXML로부터 연결된 UI 컴포넌트
	@FXML
	private Label currentUserLabel;
	@FXML
	private Label currentUserGradeLabel;
	@FXML
	private Button modifyUserBtn;
	@FXML
	private Button modifyRoleBtn;
	@FXML
	private VBox addMemberForm;
	@FXML
	private TextField newEmailField;
	@FXML
	private TextField newAdminNameField;
	@FXML
	private TextField newGradeField;
	@FXML
	private TableView<Admin> adminTableView;
	@FXML
	private TableColumn<Admin, String> emailColumn;
	@FXML
	private TableColumn<Admin, String> adminNameColumn;
	@FXML
	private TableColumn<Admin, String> gradeColumn;
	@FXML
	private TableColumn<Admin, LocalDate> regDateColumn;
	@FXML
	private TableColumn<Admin, LocalDate> modDateColumn;
	@FXML
	private TableColumn<Admin, Void> actionsColumn;

	// 모든 관리자 데이터 목록
	private ObservableList<Admin> allAdminData;

	// 관리자 DAO 인스턴스
	private final AdminDAO adminDAO = new AdminDAO();

	// 초기화 시 관리자 리스트와 사용자 정보 표시
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 관리자 데이터 로드 실패시 빈 리스트 반환
		try {
			allAdminData = getAdminData();
			adminTableView.setItems(allAdminData);
		} catch (Exception e) {
			e.printStackTrace();
			allAdminData = FXCollections.observableArrayList();
		}

		// 현재 로그인한 사용자 정보 표시
		currentUserLabel.setText("현재 사용자: " + Session.getCurrentUser().getAdminName());
		currentUserGradeLabel.setText(Session.getCurrentUser().getGradeDisplayName());

		// 등급 표시 글자로 변경
		gradeColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGradeDisplayName()));

		// 작업 컬럼에 '등급 변경' 버튼 추가
		actionsColumn.setCellFactory(column -> {
			return new TableCell<Admin, Void>() {
				private final Button modifyRoleBtn = new Button("등급 변경");

				{
					modifyRoleBtn.setStyle(
							"-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 14px; -fx-padding: 8 16 8 16;");
					modifyRoleBtn.setOnAction(event -> {
						Admin admin = getTableView().getItems().get(getIndex());
						onModifyRole(admin); // 해당 멤버 정보를 전달
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);

					// 셀이 비어있으면 아무것도 표시하지 않음
					if (empty) {
						setGraphic(null);
					} else {
						// 현재 행의 사용자(Admin) 정보를 가져옴
						Admin rowAdmin = getTableView().getItems().get(getIndex());
						// 버튼을 표시할지 여부 결정
						// 조건. 로그인한 유저가 '최고 관리자'이고, 현재 행의 유저가 '점장'인 경우
						boolean showButton = (Session.getCurrentUser().getGrade() == 1 && rowAdmin.getGrade() == 2);

						if (showButton) {
							// 조건을 만족하면 버튼 표시
							setGraphic(modifyRoleBtn);
						} else {
							// 로그인한 유저가 점장이거나, 같은 관리자 등급일 때에는 버튼 숨김
							setGraphic(null);
						}
					}
				}
			};
		});
	}

	// 관리자 목록을 DB에서 가져옴
	public ObservableList<Admin> getAdminData() {
		try {
			return adminDAO.getAllAdmins();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	// 개인정보 수정 버튼 클릭 시 호출
	@FXML
	private void onModifyUser() {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profileEdit/profileEdit.fxml"));
			Parent root = loader.load();

			// 컨트롤러에 현재 로그인한 사용자 정보 전달
			ProfileEditController profileEditController = loader.getController();
			profileEditController.setCurrentUser(Session.getCurrentUser());

			// 새 stage 생성
			Stage dialogStage = new Stage();
			dialogStage.setTitle("개인정보 수정");
			dialogStage.initModality(Modality.WINDOW_MODAL); // 모달 창으로 설정
			dialogStage.initOwner(modifyUserBtn.getScene().getWindow()); // 부모 창 설정
			dialogStage.setResizable(false); // 크기 변경 불가

			// Scene 설정
			Scene scene = new Scene(root);
			dialogStage.setScene(scene);

			// 창 표시 및 대기
			dialogStage.showAndWait();

			// 수정 성공 시 테이블 및 라벨 갱신
			if (profileEditController.isSuccessful()) {
				allAdminData = getAdminData();
				adminTableView.setItems(allAdminData);
				// 테이블 전체 새로고침
				adminTableView.refresh();
				// 현재 로그인한 사용자 이름
				currentUserLabel.setText("현재 사용자: " + Session.getCurrentUser().getAdminName());

				// 현재 로그인한 사용자 등급
				currentUserGradeLabel.setText(Session.getCurrentUser().getGradeDisplayName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 등급 변경 버튼 클릭 시 호출
	@FXML
	private void onModifyRole(Admin currentUser) {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profileEdit/roleEdit.fxml"));
			Parent root = loader.load();

			// 컨트롤러에 현재 로그인한 사용자 정보 전달
			RoleEditController roleEditController = loader.getController();
			roleEditController.setCurrentUser(currentUser);

			// 새 stage 생성
			Stage dialogStage = new Stage();
			dialogStage.setTitle("등급변경");
			dialogStage.initModality(Modality.WINDOW_MODAL); // 모달 창으로 설정
			dialogStage.initOwner(modifyUserBtn.getScene().getWindow()); // 부모 창 설정
			dialogStage.setResizable(false); // 크기 변경 불가

			// Scene 설정
			Scene scene = new Scene(root);
			dialogStage.setScene(scene);

			// 창 표시 및 대기
			dialogStage.showAndWait();

			// 등급 변경 성공 시 테이블 갱신
			if (roleEditController.isSuccessful()) {
				// DB에서 최신 데이터를 가져옵니다.
				allAdminData = getAdminData();

				// TableView에 새로운 리스트를 설정합니다.
				adminTableView.setItems(allAdminData);

				// 테이블 전체 새로고침
				adminTableView.refresh();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

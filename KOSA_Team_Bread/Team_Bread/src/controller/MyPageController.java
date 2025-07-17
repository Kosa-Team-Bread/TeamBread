package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.admin.Admin;
import model.admin.AdminDAO;

public class MyPageController implements Initializable {
	@FXML
	private Label currentUserLabel;
	@FXML
	private Label currentUserGradeLabel;
	@FXML
	private Button modifyUserBtn;
	@FXML
	private VBox addMemberForm;
	@FXML
	private TextField newEmailField;
	@FXML
	private TextField newAdminNameField;
	@FXML
	private TextField newGradeField;
//	@FXML
//	private ComboBox<GradeItem> newGradeComboBox;

	@FXML
	private TableView<Admin> adminTableView;
	@FXML
	private TableColumn<Admin, String> emailColumn;
	@FXML
	private TableColumn<Admin, String> adminNameColumn;
	@FXML
	private TableColumn<Admin, Integer> gradeColumn;
	@FXML
	private TableColumn<Admin, LocalDate> regDateColumn;
	@FXML
	private TableColumn<Admin, LocalDate> modDateColumn;
	@FXML
	private TableColumn<Admin, Void> actionsColumn;

	/**
	 * 컨트롤러가 초기화될 때 호출되는 메소드입니다. 이곳에서 테이블 데이터 로딩 등 초기화 작업을 수행합니다.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// FXML에서 PropertyValueFactory를 이미 설정했으므로,
		// 여기서는 테이블에 데이터 리스트만 넣어주면 됩니다.

		adminTableView.setItems(getAdminData());
	}

	public ObservableList<Admin> getAdminData() {
		try {
			return AdminDAO.getAllAdmins();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	@FXML
	private void onModifyUser() {

	}

	@FXML
	private void onCancelAdd() {

	}

	@FXML
	private void onConfirmAdd() {

	}
}

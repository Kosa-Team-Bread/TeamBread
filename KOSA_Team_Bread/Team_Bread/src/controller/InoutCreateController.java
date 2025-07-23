package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.admin.Admin;
import model.inout.InoutDAO;
import model.inout.InoutInsertDto;

public class InoutCreateController implements Initializable {
	@FXML
	private TextField productNameField;
	@FXML
	private ComboBox<String> inoutTypeComboBox;
	@FXML
	private ComboBox<String> categoryComboBox;
	@FXML
	private TextField adminNameField;
	@FXML
	private TextField inoutQuantityField;
	@FXML
	private TextField contentField;
	@FXML
	private Button cancelButton;
	@FXML
	private Button saveButton;

	// 현재 사용자 정보 (부모 창에서 전달받을 데이터)
	private Admin currentUser;
	// 입출고 제품명 리스트
	private ObservableList<String> productNames;
	// 자동 완성 리스트
	private ContextMenu suggestions = new ContextMenu();

	private final InoutDAO inoutDAO = new InoutDAO();
	
	private boolean isSuccessful = true;

	public boolean isSuccessful() {
		return isSuccessful;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	// 현재 유저 상태 설정 메서드
	public void setCurrentUser(Admin user) {
		this.currentUser = user;

		// 사용자 정보에 따라 UI 초기화
		if (user != null) {
			adminNameField.setText(user.getAdminName());
		}
	}

	public void setProductNames(ObservableList<String> names) {
		this.productNames = names;

		// 입력 감지하여 추천 필터링
		productNameField.textProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal == null || newVal.isEmpty()) {
				suggestions.hide();
				return;
			}

			// 필터링된 이름 목록
			List<String> filtered = productNames.stream()
					.filter(name -> name.toLowerCase().contains(newVal.toLowerCase())).limit(5)
					.collect(Collectors.toList());

			if (filtered.isEmpty()) {
				suggestions.hide();
			} else {
				suggestions.getItems().clear();

				for (String name : filtered) {
					MenuItem item = new MenuItem(name);
					item.setOnAction(e -> {
						productNameField.setText(name);
						suggestions.hide();
					});
					suggestions.getItems().add(item);
				}

				if (!suggestions.isShowing()) {
					suggestions.show(productNameField,
							productNameField.localToScreen(0, productNameField.getHeight()).getX(),
							productNameField.localToScreen(0, productNameField.getHeight()).getY());
				}
			}
		});
	}

	@FXML
	private void handleCancel() {
		((Stage) cancelButton.getScene().getWindow()).close();
	}

	@FXML
	private void handleSave() {
		// 등록 로직
		if (currentUser != null) {
			try {
				String productName = productNameField.getText();
				String adminName = adminNameField.getText();
				String inoutType = inoutTypeComboBox.getValue();
				String categoryName = categoryComboBox.getValue();
				Integer inoutQuantity = Integer.valueOf(inoutQuantityField.getText());
				String content = contentField.getText();
				InoutInsertDto inoutInsert = InoutInsertDto.builder()
						.productName(productName)
						.adminName(adminName)
						.inoutType(inoutType)
						.categoryName(categoryName)
						.inoutQuantity(inoutQuantity)
						.content(content)
						.build();

				inoutDAO.insertCategory(inoutInsert);
				this.isSuccessful = true;
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("입출고 신규 등록에 실패했습니다: " + e.getMessage());
				this.isSuccessful = false;
			}
		}

		((Stage) saveButton.getScene().getWindow()).close();
	}
}

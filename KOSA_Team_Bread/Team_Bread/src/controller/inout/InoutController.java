package controller.inout;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.inout.InoutDAO;
import model.inout.inoutSelectDto;
import util.Session;

// Made By 나규태
public class InoutController implements Initializable {
	// @FXML 못 넣은 요소 많음(넣어야함)
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private ComboBox<String> inoutTypeComboBox;
	@FXML
	private ComboBox<String> categoryComboBox;
	@FXML
	private TextField productNameField;
	@FXML
	private Button newInoutButton;
	@FXML
	private TableView<inoutSelectDto> inoutTableView;
	@FXML
	private TableColumn<inoutSelectDto, LocalDate> inoutRegDateColumn;
	@FXML
	private TableColumn<inoutSelectDto, String> categoryNameColumn;
	@FXML
	private TableColumn<inoutSelectDto, String> productNameColumn;
	@FXML
	private TableColumn<inoutSelectDto, String> inoutTypeColumn;
	@FXML
	private TableColumn<inoutSelectDto, Integer> inoutQuantityColumn;
	@FXML
	private TableColumn<inoutSelectDto, String> inoutContentColumn;
	@FXML
	private TableColumn<inoutSelectDto, String> adminNameColumn;

	private ObservableList<inoutSelectDto> allInoutData;

	private final InoutDAO inoutDAO = new InoutDAO();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 입출고 리스트
		try {
			allInoutData = getInoutData();
			inoutTableView.setItems(allInoutData);
		} catch (Exception e) {
			e.printStackTrace();
			allInoutData = FXCollections.observableArrayList();
		}

		// 필터 조건 바뀔 때마다 필터링 실행
		inoutTypeComboBox.setOnAction(e -> filterData());
		categoryComboBox.setOnAction(e -> filterData());
		startDatePicker.setOnAction(e -> filterData());
		endDatePicker.setOnAction(e -> filterData());
		productNameField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
	}

	public ObservableList<inoutSelectDto> getInoutData() {
		try {
			return inoutDAO.findAllInout();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	// 필터 조건 보관
	private Predicate<inoutSelectDto> buildFilterPredicate() {
		return item -> matchesDateRange(item) && matchesInoutType(item) && matchesCategory(item)
				&& matchesProductName(item);
	}

	private boolean matchesDateRange(inoutSelectDto item) {
		LocalDate start = startDatePicker.getValue();
		LocalDate end = endDatePicker.getValue();

		if (start != null && item.getInoutRegDate().isBefore(start))
			return false;
		if (end != null && item.getInoutRegDate().isAfter(end))
			return false;

		return true;
	}

	private boolean matchesInoutType(inoutSelectDto item) {
		String selectedType = inoutTypeComboBox.getValue();
		if (selectedType == null || selectedType.equals("전체"))
			return true;

		return item.getTypeName().equals(selectedType);
	}

	private boolean matchesCategory(inoutSelectDto item) {
		String selectedCategory = categoryComboBox.getValue();
		if (selectedCategory == null || selectedCategory.equals("전체"))
			return true;

		return item.getCategoryName().equals(selectedCategory);
	}

	private boolean matchesProductName(inoutSelectDto item) {
		String keyword = productNameField.getText();
		if (keyword == null || keyword.trim().isEmpty())
			return true;

		return item.getProductName().toLowerCase().contains(keyword.toLowerCase());
	}

	// 메인 필터 함수
	public void filterData() {
		ObservableList<inoutSelectDto> filtered = allInoutData.filtered(buildFilterPredicate());
		inoutTableView.setItems(filtered);
	}

	@FXML
	private void handleNewInoutAction() {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inout/InoutCreate.fxml"));
			Parent root = loader.load();

			// 컨트롤러 가져오기
			InoutCreateController inoutCreateController = loader.getController();

			// 현재 사용자 정보
			inoutCreateController.setCurrentUser(Session.getCurrentUser());

			// 품목명 리스트 추출 후 전달
			ObservableList<String> productNames = allInoutData.stream()
					.map(inoutSelectDto::getProductName)
					.distinct()
					.collect(Collectors.toCollection(FXCollections::observableArrayList));
			inoutCreateController.setProductNames(productNames);

			// 새 stage 생성
			Stage dialogStage = new Stage();
			dialogStage.setTitle("입출고 신규 등록");
			dialogStage.initModality(Modality.WINDOW_MODAL); // 모달 창으로 설정
			dialogStage.initOwner(newInoutButton.getScene().getWindow()); // 부모 창 설정
			dialogStage.setResizable(false); // 크기 변경 불가

			// Scene 설정
			Scene scene = new Scene(root);
			dialogStage.setScene(scene);

			// 창 표시 및 대기
			dialogStage.showAndWait();
			
			if(inoutCreateController.isSuccessful()) {
				allInoutData = getInoutData(); // DB 다시 읽기
				filterData(); 				   // 필터 적용해서 갱신
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

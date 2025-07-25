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

// Made By 나규태 + CHATGPT
// 입출고 관리 화면 컨트롤러
public class InoutController implements Initializable {
	// FXML 요소 선언
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

	// 전체 입출고 데이터 목록
	private ObservableList<inoutSelectDto> allInoutData;

	// DAO 객체 생성
	private final InoutDAO inoutDAO = new InoutDAO();

	// 초기화 메서드
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 입출고 리스트
		try {
			// DB에서 입출고 데이터 불러오기
			allInoutData = getInoutData();
			inoutTableView.setItems(allInoutData);
		} catch (Exception e) {
			e.printStackTrace();
			allInoutData = FXCollections.observableArrayList();
		}

		// 필터 조건 이벤트 리스너를 등록하여 바뀔 때마다 필터링 실행
		inoutTypeComboBox.setOnAction(e -> filterData());
		categoryComboBox.setOnAction(e -> filterData());
		startDatePicker.setOnAction(e -> filterData());
		endDatePicker.setOnAction(e -> filterData());
		productNameField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
	}

	// DAO를 통해 전체 입출고 데이터를 가져오는 메서드
	public ObservableList<inoutSelectDto> getInoutData() {
		try {
			return inoutDAO.findAllInout();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	// 전체 필터 조건을 결합한 Predicate 반환
	private Predicate<inoutSelectDto> buildFilterPredicate() {
		return item -> matchesDateRange(item) && matchesInoutType(item) && matchesCategory(item)
				&& matchesProductName(item);
	}

	// 날짜 범위 조건 필터
	private boolean matchesDateRange(inoutSelectDto item) {
		LocalDate start = startDatePicker.getValue();
		LocalDate end = endDatePicker.getValue();

		if (start != null && item.getInoutRegDate().isBefore(start))
			return false;
		if (end != null && item.getInoutRegDate().isAfter(end))
			return false;

		return true;
	}

	// 입출고 유형 조건 필터
	private boolean matchesInoutType(inoutSelectDto item) {
		String selectedType = inoutTypeComboBox.getValue();
		if (selectedType == null || selectedType.equals("전체"))
			return true;

		return item.getTypeName().equals(selectedType);
	}

	// 카테고리 조건 필터
	private boolean matchesCategory(inoutSelectDto item) {
		String selectedCategory = categoryComboBox.getValue();
		if (selectedCategory == null || selectedCategory.equals("전체"))
			return true;

		return item.getCategoryName().equals(selectedCategory);
	}

	// 제품명 키워드 조건 필터
	private boolean matchesProductName(inoutSelectDto item) {
		String keyword = productNameField.getText();
		if (keyword == null || keyword.trim().isEmpty())
			return true;

		return item.getProductName().toLowerCase().contains(keyword.toLowerCase());
	}

	// 필터링된 데이터로 테이블 뷰 갱신
	public void filterData() {
		ObservableList<inoutSelectDto> filtered = allInoutData.filtered(buildFilterPredicate());
		inoutTableView.setItems(filtered);
	}

	// 입출고 신규 등록 버튼 클릭 시 실행
	@FXML
	private void handleNewInoutAction() {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inout/InoutCreate.fxml"));
			Parent root = loader.load();

			// 컨트롤러 가져오기
			InoutCreateController inoutCreateController = loader.getController();

			// 현재 사용자 정보 전달
			inoutCreateController.setCurrentUser(Session.getCurrentUser());

			// 제품명 리스트 전달
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

			// 등록 성공시 테이블 뷰 갱신
			if (inoutCreateController.isSuccessful()) {
				allInoutData = getInoutData(); // DB 다시 읽기
				filterData(); // 필터 적용해서 갱신
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

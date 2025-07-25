package controller.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

// Made By 나규태 + CHATGPT
public class MainController implements Initializable {
	// FXML로부터 연결된 UI 컴포넌트
	@FXML
	private TabPane tabPane;
	@FXML
	private Label stockMenu;
	@FXML
	private Label inoutMenu;
	@FXML
	private Label mypageMenu;
	@FXML
	private Label couponMenu;
	@FXML
	private AnchorPane mainContent;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 초기화 시 필요한 동작
	}

	// 재고 관리 화면 로드
	@FXML
	private void handleStockClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/stock/Stock.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			setAnchors(mypageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 입출고 관리 화면 로드
	@FXML
	private void handleInoutClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/inout/Inout.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			setAnchors(mypageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 마이페이지 화면 로드
	@FXML
	private void handleMypageClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/mypage/Mypage.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			setAnchors(mypageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 쿠폰 관리 화면 로드
	@FXML
	private void handleCouponClick(MouseEvent event) {
		try {
			// CouponManagement.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/coupon/CouponManagement.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			setAnchors(mypageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// AnchorPane에 view를 꽉 채우도록 설정
	private void setAnchors(Parent view) {
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setBottomAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
	}
}

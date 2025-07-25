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

// Made By 나규태
public class MainController implements Initializable {
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

	}

	@FXML
	private void handleStockClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/stock/Stock.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			// 탭화면을 AnchorPane에 꽉 차게 설정 (optional)
			AnchorPane.setTopAnchor(mypageView, 0.0);
			AnchorPane.setBottomAnchor(mypageView, 0.0);
			AnchorPane.setLeftAnchor(mypageView, 0.0);
			AnchorPane.setRightAnchor(mypageView, 0.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleInoutClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/inout/Inout.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			// 탭화면을 AnchorPane에 꽉 차게 설정 (optional)
			AnchorPane.setTopAnchor(mypageView, 0.0);
			AnchorPane.setBottomAnchor(mypageView, 0.0);
			AnchorPane.setLeftAnchor(mypageView, 0.0);
			AnchorPane.setRightAnchor(mypageView, 0.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleMypageClick(MouseEvent event) {
		try {
			// mypage.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/mypage/Mypage.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			// 탭화면을 AnchorPane에 꽉 차게 설정 (optional)
			AnchorPane.setTopAnchor(mypageView, 0.0);
			AnchorPane.setBottomAnchor(mypageView, 0.0);
			AnchorPane.setLeftAnchor(mypageView, 0.0);
			AnchorPane.setRightAnchor(mypageView, 0.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCouponClick(MouseEvent event) {
		try {
			// CouponManagement.fxml 로드
			Parent mypageView = FXMLLoader.load(getClass().getResource("/view/coupon/CouponManagement.fxml"));

			// 기존 내용 제거하고 새로운 화면 추가
			mainContent.getChildren().clear();
			mainContent.getChildren().add(mypageView);

			// 탭화면을 AnchorPane에 꽉 차게 설정 (optional)
			AnchorPane.setTopAnchor(mypageView, 0.0);
			AnchorPane.setBottomAnchor(mypageView, 0.0);
			AnchorPane.setLeftAnchor(mypageView, 0.0);
			AnchorPane.setRightAnchor(mypageView, 0.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

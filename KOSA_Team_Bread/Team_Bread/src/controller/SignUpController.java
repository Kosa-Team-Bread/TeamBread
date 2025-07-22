package controller;


import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.admin.AdminDAO;
import util.DBUtil;

public class SignUpController implements Initializable{
	
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField passwordConfirmField;
	@FXML
	private Button signupButton;
	
	private AdminDAO adminDAO;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminDAO = new AdminDAO();
		
		emailField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			
			@Override
			public void changed(ObservableValue<Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				if (!newValue && !emailField.getText().trim().isEmpty()) {
					checkEmailDuplicateRealtime();
				}
			}
		});
		
		passwordConfirmField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			
			@Override
			public void changed(ObservableValue<Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				if (!newValue && !passwordConfirmField.getText().isEmpty()) {
					checkPasswordMatchRealtime();
				}
			}
		});
	}

	
	// 회원가입
	@FXML
	private void handleSignUp(ActionEvent event) {
		
		String name = nameField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String passwordConfirm = passwordConfirmField.getText().trim();
		
		if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
			showAlert("모두 입력해주세요");
			return;
		}
		
		if (!password.equals(passwordConfirm)) {
			showAlert("비밀번호가 일치하지 않습니다.");
			return;
		}
		
		try {
			
			List<Object> params = new ArrayList<>();
			params.add(email);
			
			ResultSet dupEmail = DBUtil.dbExecuteQuery(
					"SELECT 1 FROM tbl_admin WHERE email = ?",
					params);		
				
		}
	}
	
	// 이메일 중복 검사
	private void checkEmailDuplicateRealtime() {
		String email = emailField.getText().trim();
		
		if (!email.isEmpty() &&  )
		
	}
	
	// 비밀번호 확인
	private void checkPasswordMatchRealtime() {
		// TODO Auto-generated method stub
		
	}

	// 알림 메세지
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
}

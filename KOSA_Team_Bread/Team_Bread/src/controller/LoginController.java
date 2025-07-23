package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.admin.AdminDAO;

public class LoginController implements Initializable {
	
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private CheckBox rememberCheck;
	@FXML
	private Button loginButton;
	
	private AdminDAO adminDAO;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminDAO = new AdminDAO();
		
		// 로그인 이메일 기억하기
//		loadRememberedEmail();
	}
}

package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.stock.Stock;

public class StockController implements Initializable {
	@FXML
	private TableView<Stock> tableView;
	@FXML
	private ComboBox categoryComboBox;
	@FXML
	private TextField searchField;
	@FXML
	private Button searchButton;
	@FXML
	private TableView<Stock> stockTableView;
	@FXML
	private TableColumn<Stock, String> stockNameColumn;
	@FXML
	private TableColumn<Stock, String> categoryNameColumn;
	@FXML
	private TableColumn<Stock, Integer> stockQuantityColumn;
	@FXML
	private TableColumn<Stock, String> locationColumn;
	@FXML
	private TableColumn<Stock, LocalDate> stockRegDateColumn;
	@FXML
	private TableColumn<Stock, LocalDate> stockModDateColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void searchProducts() {

	}
}

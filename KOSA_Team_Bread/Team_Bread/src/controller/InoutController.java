package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.inout.Inout;

public class InoutController implements Initializable {
	// @FXML 못 넣은 요소 많음(넣어야함)
	@FXML
	private TableView<Inout> inoutTableView;
	@FXML
	private TableColumn<Inout, LocalDate> inoutRegDateColumn;
	@FXML
	private TableColumn<Inout, String> categoryNameColumn;
	@FXML
	private TableColumn<Inout, String> productNameColumn;
	@FXML
	private TableColumn<Inout, Integer> inoutTypeColumn;
	@FXML
	private TableColumn<Inout, Integer> inoutQuantityColumn;
	@FXML
	private TableColumn<Inout, String> inoutContentColumn;
	@FXML
	private TableColumn<Inout, String> adminNameColumn;
	@FXML
	private TableColumn<Inout, Void> inoutActionColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void searchProducts() {

	}

	@FXML
	private void handleNewEntryAction() {

	}
}

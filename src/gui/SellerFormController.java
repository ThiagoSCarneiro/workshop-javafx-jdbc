package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentServices;
import model.services.SellerServices;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerServices serviceSeller;

	private DepartmentServices serviceDepartment;

	private List<DataChangeListener> dataChageListeners = new ArrayList();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalry;

	@FXML
	private ComboBox<Department> cbDepartment;

	@FXML
	private Label errorLabelName;

	@FXML
	private Label errorLabelEmail;

	@FXML
	private Label errorLabelBirthDate;

	@FXML
	private Label errorLabelBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setService(SellerServices service, DepartmentServices serviceDepartment) {
		this.serviceSeller = service;
		this.serviceDepartment = serviceDepartment;
	}

	public void subscribeDataChangeListerner(DataChangeListener listener) {
		dataChageListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (serviceSeller == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			serviceSeller.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getError());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChageListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToLong(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")
				|| txtName.getText().trim().toLowerCase().equals("null")) {
			exception.addErro("Name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")
				|| txtEmail.getText().trim().toLowerCase().equals("null")) {
			exception.addErro("Email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addErro("BirthDate", "Field can't be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (txtBaseSalry.getText() == null || txtBaseSalry.getText().trim().equals("")
				|| txtBaseSalry.getText().trim().toLowerCase().equals("null")) {
			exception.addErro("BaseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalry.getText()));
		
		obj.setDepartment(cbDepartment.getValue());

		if (exception.getError().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalry);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		txtBaseSalry.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepartment() == null) {
			cbDepartment.getSelectionModel().selectFirst();
		}
		cbDepartment.setValue(entity.getDepartment());
	}

	public void loadAssociatedObjects() {
		if (serviceDepartment == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = serviceDepartment.findAll();
		obsList = FXCollections.observableArrayList(list);
		cbDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> error) {
		Set<String> field = error.keySet();

		errorLabelName.setText(field.contains("Name")? error.get("Name") : "");
		errorLabelEmail.setText(field.contains("Email")? error.get("Email") : "");
		errorLabelBaseSalary.setText(field.contains("BaseSalary")? error.get("BaseSalary") : "");
		errorLabelBirthDate.setText(field.contains("BirthDate")? error.get("BirthDate") : "");
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cbDepartment.setCellFactory(factory);
		cbDepartment.setButtonCell(factory.call(null));
	}

}

package by.grodno.gwtPersistence.client;

import by.grodno.gwtPersistence.shared.FieldChecker;
import by.grodno.gwtPersistence.shared.Patient;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.Date;
import java.util.List;

public class PatientTableView implements EntryPoint {

    private List<Patient> CONTACTS;
    private CellTable<Patient> patientTable;
    private DialogBox addPatientBox;
    private DialogBox editPatientBox;
    private SimplePager pager;
    private ListDataProvider<Patient> dataProvider = new ListDataProvider<>();
    private MainRpcServiceAsync rpcService = GWT.create(MainRpcService.class);
    private SingleSelectionModel<Patient> selectionModel = new SingleSelectionModel<>();
    private Button deletePatientButton;
    private Button searchButton;
    private Button editPatientButton;
    private final FieldChecker fieldChecker = new FieldChecker();

    public void onModuleLoad() {
        createSearchPanel();
        rpcService.getAllPatients(new AsyncCallback<List<Patient>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Ошибка при подключении к базе данных, перезапустите приложение");
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<Patient> result) {
                CONTACTS = dataProvider.getList();
                CONTACTS.addAll(result);
                createTable();
                createServiceButtonPanel();
            }
        });
    }

    //create search
    private void createSearchPanel() {
        Label searchHeader = new Label("Поиск по фамилии:");
        searchHeader.setStyleName("textHeader");
        RootPanel.get("patientTable").add(searchHeader);
        final TextBox searchValue = new TextBox();
        searchValue.setTitle("Введите заглавную букву для отображения всех пациентов с фамилией на данную букву");
        searchValue.addStyleName("searchText");
        String searchBoxDefaultValue = "Введите фамилию пациента";
        searchValue.setValue(searchBoxDefaultValue);
        searchValue.addFocusHandler(event -> {
            if (searchValue.getValue().equals(searchBoxDefaultValue))
                searchValue.setValue("");
        });
        searchValue.addKeyPressHandler(event -> searchButton.click());
        RootPanel.get("patientTable").add(searchValue);
        searchButton = new Button(" Найти ");
        RootPanel.get("patientTable").add(searchButton);
        searchButton.addClickHandler(event -> {
            String search = searchValue.getValue();
            if (!search.equals(searchBoxDefaultValue)) {
                doSearch(search);
            }
        });
    }

    //search in the database
    private void doSearch(String searchValue) {
        dataProvider.getList().clear();
        rpcService.findPatient(searchValue, new AsyncCallback<List<Patient>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Ошибка при подключении к базе данных, перезапустите приложение");
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<Patient> result) {
                CONTACTS.clear();
                CONTACTS.addAll(result);
            }
        });
    }

    //create edit/delete/add buttons
    private void createServiceButtonPanel() {
        addPatientBox = createDialogBox();
        addPatientBox.setGlassEnabled(true);
        addPatientBox.setAnimationEnabled(true);
        Button addPatientOpenDialog = new Button(
                "Добавить пациента", new ClickHandler() {
            public void onClick(ClickEvent sender) {
                selectionModel.clear();
                addPatientBox.center();
                addPatientBox.show();
            }
        });
        addPatientOpenDialog.setStyleName("serviceButton");
        deletePatientButton = new Button("Удалить пациента", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Patient selected = selectionModel.getSelectedObject();
                if (selected != null) deletePatient(selected);
            }
        });
        editPatientButton = new Button("Редактировать", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editPatientBox = createEditDialogBox();
                editPatientBox.setAnimationEnabled(true);
                editPatientBox.center();
                editPatientBox.show();
            }
        });
        deletePatientButton.setEnabled(false);
        deletePatientButton.setStyleName("serviceButton");
        editPatientButton.setEnabled(false);
        editPatientButton.setStyleName("serviceButton");
        HorizontalPanel serviceButtons = new HorizontalPanel();
        serviceButtons.setStyleName("addButtonPanel");
        serviceButtons.setSpacing(8);
        serviceButtons.add(addPatientOpenDialog);
        serviceButtons.add(deletePatientButton);
        serviceButtons.add(editPatientButton);
        RootPanel.get("patientTable").add(serviceButtons);
    }

    //delete patient from database
    private void deletePatient(Patient patient) {
        rpcService.deletePatient(patient, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Ошибка удаления");
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(Void result) {
                CONTACTS.remove(patient);
                selectionModel.clear();
                deletePatientButton.setEnabled(false);
            }
        });
    }

    //addPatientBox
    private DialogBox createDialogBox() {
        DialogBox dialogBox = new DialogBox(true);
        dialogBox.setSize("300px", "300px");
        dialogBox.setStyleName("addPatientDialog");
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);
        HTML details = new HTML("<h4>Введите данные пациента</h4>");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);
        TextBox firstNameBox = new TextBox();
        firstNameBox.setMaxLength(50);
        firstNameBox.setValue(FieldChecker.firstNameDefault);
        firstNameBox.addFocusHandler(event -> {
            if (firstNameBox.getValue().equals(FieldChecker.firstNameDefault))
                firstNameBox.setValue("");
        });
        TextBox lastNameBox = new TextBox();
        lastNameBox.addFocusHandler(event -> {
            if (lastNameBox.getValue().equals(FieldChecker.lastNameDefault))
                lastNameBox.setValue("");
        });
        lastNameBox.setMaxLength(100);
        lastNameBox.setText(FieldChecker.lastNameDefault);
        DatePicker birthdayPicker = new DatePicker();
        birthdayPicker.setTitle("выберите дату");
        birthdayPicker.setStyleName("gwt-DateBox");
        birthdayPicker.setYearAndMonthDropdownVisible(true);
        birthdayPicker.setVisibleYearCount(200);
        dialogContents.add(firstNameBox);
        dialogContents.add(lastNameBox);
        dialogContents.add(birthdayPicker);

        // Add a close button at the bottom of the dialog
        Button addButton = new Button(
                "Добавить", new ClickHandler() {
            public void onClick(ClickEvent event) {
                Patient newPatient = new Patient();
                newPatient.setFirstName(firstNameBox.getText());
                newPatient.setLastName(lastNameBox.getText());
                newPatient.setBirthDate(birthdayPicker.getValue());
                String validate = fieldChecker.verifyPatient(newPatient);
                if (!validate.equals("")) {
                    Window.alert(validate);
                    return;
                }
                CONTACTS.add(newPatient);
                addNewPatient(newPatient);
                firstNameBox.setValue(FieldChecker.firstNameDefault);
                lastNameBox.setValue(FieldChecker.lastNameDefault);
                dialogBox.hide();
            }
        });
        Button closeButton = new Button("Закрыть", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        HorizontalPanel addButtonsPanel = new HorizontalPanel();
        addButtonsPanel.add(addButton);
        addButtonsPanel.add(closeButton);
        addButtonsPanel.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_LEFT);
        addButtonsPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        dialogContents.add(addButtonsPanel);
        return dialogBox;
    }

    //add Patient to database
    private void addNewPatient(Patient patient) {
        rpcService.addNewPatient(patient, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Произошла ошибка.");
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(Void result) {
            }
        });
    }

    private void updatePatient(Patient patient) {
        rpcService.updatePatient(patient, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Произошла ошибка");
            }

            @Override
            public void onSuccess(Void result) {
            }
        });
    }

    //editPatientBox
    private DialogBox createEditDialogBox() {
        DialogBox dialogBox = new DialogBox(true);
        dialogBox.setSize("300px", "300px");
        dialogBox.setStyleName("addPatientDialog");
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);
        HTML details = new HTML("<h4>Измените данные пациента</h4>");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);
        TextBox firstNameBox = new TextBox();
        firstNameBox.setMaxLength(50);
        firstNameBox.setValue(FieldChecker.firstNameDefault);
        TextBox lastNameBox = new TextBox();
        lastNameBox.setMaxLength(100);
        lastNameBox.setText(FieldChecker.lastNameDefault);
        DatePicker birthdayPicker = new DatePicker();
        birthdayPicker.setTitle("выберите дату");
        birthdayPicker.setStyleName("gwt-DateBox");
        birthdayPicker.setYearAndMonthDropdownVisible(true);
        birthdayPicker.setVisibleYearCount(200);
        dialogContents.add(firstNameBox);
        dialogContents.add(lastNameBox);
        dialogContents.add(birthdayPicker);
        Patient newPatient = selectionModel.getSelectedObject();
        firstNameBox.setValue(newPatient.getFirstName());
        lastNameBox.setValue(newPatient.getLastName());
        birthdayPicker.setValue(newPatient.getBirthDate());
        Button addButton = new Button(
                "Сохранить", new ClickHandler() {
            public void onClick(ClickEvent event) {
                CONTACTS.remove(newPatient);
                newPatient.setFirstName(firstNameBox.getText());
                newPatient.setLastName(lastNameBox.getText());
                newPatient.setBirthDate(birthdayPicker.getValue());
                String validate = fieldChecker.verifyPatient(newPatient);
                if (!validate.equals("")) {
                    Window.alert(validate);
                    return;
                }
                CONTACTS.add(newPatient);
                updatePatient(newPatient);
                dialogBox.hide();
            }
        });
        Button closeButton = new Button("Закрыть", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        HorizontalPanel addButtonsPanel = new HorizontalPanel();
        addButtonsPanel.add(addButton);
        addButtonsPanel.add(closeButton);
        addButtonsPanel.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_LEFT);
        addButtonsPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        dialogContents.add(addButtonsPanel);
        return dialogBox;
    }

    //create table with Patients data
    private void createTable() {
        patientTable = new CellTable<>();
        patientTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        TextColumn<Patient> nameColumn = new TextColumn<Patient>() {
            @Override
            public String getValue(Patient object) {
                return object.getFirstName();
            }
        };
        patientTable.addColumn(nameColumn, FieldChecker.firstNameDefault);
        TextColumn<Patient> lastNameColumn = new TextColumn<Patient>() {
            @Override
            public String getValue(Patient object) {
                return object.getLastName();
            }
        };
        patientTable.addColumn(lastNameColumn, FieldChecker.lastNameDefault);
        DateCell dateCell = new DateCell();
        Column<Patient, Date> dateColumn = new Column<Patient, Date>(dateCell) {
            @Override
            public Date getValue(Patient object) {
                return object.getBirthDate();
            }
        };
        patientTable.addColumn(dateColumn, "Дата рождения");
        patientTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(event -> {
            deletePatientButton.setEnabled(true);
            editPatientButton.setEnabled(true);
        });
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, true, 0, true);
        pager.setDisplay(patientTable);
        pager.setVisible(true);
        pager.setStyleName("pager");
        dataProvider.addDataDisplay(patientTable);
        patientTable.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CHANGE_PAGE);
        patientTable.setStyleName("patientTableView");
        RootPanel.get("patientTable").add(patientTable);
        RootPanel.get("patientTable").add(pager);
    }
}



package com.example.application.views;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.application.data.OrderItem;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.vaadin.flow.ai.formfiller.FormFiller;
import com.vaadin.flow.ai.formfiller.FormFillerResult;
import com.vaadin.flow.ai.formfiller.services.ChatGPTService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Form Filler Demo")
@Route("")
public class FormFillerTextDemo extends VerticalLayout {

    private final ComboBox<String> texts;
    private TextField nameField;
    private EmailField emailField;
    private FormLayout formLayout;
    private InstructionsDialog instructionsDialog;

    public FormFillerTextDemo() {
        setPadding(true);

        createFormLayout();

        Grid<OrderItem> orderGrid = createOrderGrid();

        // Example of editor with FormFiller just as any regular editor of Flow Grid
        Editor<OrderItem> editor = orderGrid.getEditor();
        Grid.Column<OrderItem> editColumn = orderGrid.addComponentColumn(order -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                orderGrid.getEditor().editItem(order);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0).setKey("editor");

        Binder<OrderItem> binder = new Binder<>(OrderItem.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField edOrderIdField = new TextField();
        edOrderIdField.setWidthFull();
        binder.forField(edOrderIdField).bind(OrderItem::getOrderId, OrderItem::setOrderId);
        orderGrid.getColumnByKey("orderId").setEditorComponent(edOrderIdField);

        TextField edNItemNameField = new TextField();
        edNItemNameField.setWidthFull();
        binder.forField(edNItemNameField).bind(OrderItem::getItemName, OrderItem::setItemName);
        orderGrid.getColumnByKey("itemName").setEditorComponent(edNItemNameField);

        NumberField edCostField = new NumberField();
        edCostField.setWidthFull();
        binder.forField(edCostField).bind(OrderItem::getOrderTotal, OrderItem::setOrderTotal);
        orderGrid.getColumnByKey("orderTotal").setEditorComponent(edCostField);

        DatePicker edDateField = new DatePicker();
        edDateField.setWidthFull();
        binder.forField(edDateField).bind(OrderItem::getOrderDate,
                OrderItem::setOrderDate);
        orderGrid.getColumnByKey("orderDate").setEditorComponent(edDateField);

        TextField edStatusItemField = new TextField();
        edStatusItemField.setWidthFull();
        binder.forField(edStatusItemField).bind(OrderItem::getOrderStatus, OrderItem::setOrderStatus);
        orderGrid.getColumnByKey("orderStatus").setEditorComponent(edStatusItemField);

        Button saveButton = new Button("Save", e -> editor.save());
        Button cancelButton = new Button("Close", e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        formLayout.add(orderGrid);

        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if mainLayout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));
        // Stretch the username field over 2 columns
        formLayout.setColspan(orderGrid, 2);

        add(formLayout);

        texts = new ComboBox<>();
        texts.setPlaceholder("Select a text example...");
        texts.setItems("Text1", "Text2", "Text3");
        texts.setAllowCustomValue(false);

        TextArea inputText = new TextArea("Input Text", "Select a text or type your own...");
        texts.addValueChangeListener(e ->
                inputText.setValue(getExampleTexts().get(texts.getValue())));

        Button fillButton = new Button("Fill the form");
        fillButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        fillButton.addClickListener(event -> {
            String input = inputText.getValue();
            if (input != null && !input.isEmpty()) {
                clearForm();

                HashMap<Component, String> instructions = new HashMap<>();
                instructions.put(nameField, instructionsDialog.getInstructionForName());
                instructions.put(emailField, instructionsDialog.getInstructionForEmail());

                ArrayList<String> contextInstructions = new ArrayList<>();
                contextInstructions.add(instructionsDialog.getContextInstruction());

                FormFiller formFiller = new FormFiller(formLayout, instructions, contextInstructions, new ChatGPTService());
                FormFillerResult result = formFiller.fill(input);
                getLogger().debug("GPT request for input text: \n\n {}", result.getRequest());
                getLogger().debug("GPT response for input text: \n\n {}", result.getResponse());
            } else {
                inputText.setErrorMessage("Input a text to fill the form.");
            }
        });

        instructionsDialog = new InstructionsDialog();

        Button clearButton = new Button("Clear", click -> inputText.clear());
        clearButton.setThemeName(ButtonVariant.LUMO_ERROR.getVariantName());

        Button addInstructionsButton = new Button("Add instructions...", click -> {
            instructionsDialog.open();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(clearButton, texts, addInstructionsButton, fillButton);

        VerticalLayout inputLayout = new VerticalLayout();
        inputText.setSizeFull();
        inputLayout.add(buttonLayout);
        inputLayout.add(inputText);

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(true);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(inputLayout);
        mainLayout.add(formLayout);

        add(mainLayout);

    }

    @NotNull
    private Grid<OrderItem> createOrderGrid() {
        final Grid<OrderItem> orderGrid;
        // To make the grid supported by FormFiller it is necessary to set an ID
        // and a Bean class to the Grid.
        orderGrid = new Grid<>(OrderItem.class);
        orderGrid.setId("orders");

        // Grid columns headers and Ids for the FormFiller. The IDs have to be
        // equals to the name of the related field of the Bean class.
        orderGrid.getColumnByKey("orderId").setHeader("Id");
        orderGrid.getColumnByKey("itemName").setHeader("Name");
        orderGrid.getColumnByKey("orderDate").setHeader("Date");
        orderGrid.getColumnByKey("orderStatus").setHeader("Status");
        orderGrid.getColumnByKey("orderTotal").setHeader("Cost");
        return orderGrid;
    }

    private void clearForm() {
        formLayout.getChildren().forEach(component -> {
            if (component instanceof HasValue<?, ?>) {
                ((HasValue<?, ?>) component).clear();
            } else if (component instanceof Grid) {
                ((Grid<?>) component).setItems(new ArrayList<>());
            }
        });
    }

    public HashMap<String, String> getExampleTexts() {
        HashMap<String, String> textsMap = new HashMap<>();
        texts.getListDataView().getItems().forEach(text -> {
            try {
                File textFile = new ClassPathResource("text/" + text + ".txt").getFile();
                textsMap.put(text, FileUtils.readFileToString(textFile, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Couldn't find a text example", e);
            }
        });
        return textsMap;
    }

    private void createFormLayout() {
        formLayout = new FormLayout();

        nameField = new TextField("Name");
        nameField.setId("name");
        formLayout.add(nameField);

        TextField addressField = new TextField("Address");
        addressField.setId("address");
        formLayout.add(addressField);

        TextField phoneField = new TextField("Phone");
        phoneField.setId("phone");
        formLayout.add(phoneField);

        IntegerField age = new IntegerField("Age");
        age.setId("age");
        formLayout.add(age);

        emailField = new EmailField("Email");
        emailField.setId("email");
        formLayout.add(emailField);

        PasswordField clientId = new PasswordField("Client Id");
        clientId.setId("clientId");
        formLayout.add(clientId);

        DateTimePicker dateCreationField = new DateTimePicker("Creation Date");
        dateCreationField.setId("creationDate");
        formLayout.add(dateCreationField);

        DatePicker dueDateField = new DatePicker("Due Date");
        dueDateField.setId("dueDate");
        formLayout.add(dueDateField);

        ComboBox<String> orderEntity = new ComboBox<>("Order Entity");
        orderEntity.setId("orderEntity");
        orderEntity.setItems("Person", "Company");
        formLayout.add(orderEntity);

        NumberField orderTotal = new NumberField("Order Total");
        orderTotal.setId("orderTotal");
        formLayout.add(orderTotal);

        BigDecimalField orderTaxes = new BigDecimalField("Order Taxes");
        orderTaxes.setId("orderTaxes");
        formLayout.add(orderTaxes);

        TextArea orderDescription = new TextArea("Order Description");
        orderDescription.setId("orderDescription");
        formLayout.add(orderDescription);

        RadioButtonGroup<String> paymentMethod = new RadioButtonGroup<>("Payment Method");
        paymentMethod.setItems("Credit Card", "Cash", "Paypal");
        paymentMethod.setId("paymentMethod");
        formLayout.add(paymentMethod);

        Checkbox isFinnishCustomer = new Checkbox("Is Finnish Customer");
        isFinnishCustomer.setId("isFinnishCustomer");
        formLayout.add(isFinnishCustomer);

        CheckboxGroup<String> typeService = new CheckboxGroup<>("Type of Service");
        typeService.setItems("Software", "Hardware", "Consultancy");
        typeService.setId("typeService");
        formLayout.add(typeService);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(FormFillerTextDemo.class.getName());
    }

}

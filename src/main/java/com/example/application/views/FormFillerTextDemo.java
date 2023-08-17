package com.example.application.views;

import com.example.application.data.OrderItem;
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
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

@PageTitle("Form Filler Demo")
@Route("")
public class FormFillerTextDemo extends Main {

    private ComboBox<String> templates;
    private TextArea textArea;
    private TextField nameField;
    private EmailField emailField;
    private FormLayout form;
    private InstructionsDialog instructionsDialog;
    private MultiSelectComboBox<String> typeServiceMulti;

    public FormFillerTextDemo() {
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Padding.LARGE);

        H1 title = new H1("Form Filler Demo");
        title.addClassNames(LumoUtility.FontSize.XXLARGE);

        Paragraph instruction = new Paragraph("Enter the content you want to use for the form in the text area below. You can also make use of the templates provided. Our AI will analyze the information and use it to automatically populate the relevant fields in the form on the right.");
        add(title, instruction);

        Div mainLayout = new Div(createInputSection(), createFormSection());
        mainLayout.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.LARGE);
        add(mainLayout);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(FormFillerTextDemo.class.getName());
    }

    private Component createInputSection() {
        H2 title = new H2("1. Data Entry");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);

        templates = new ComboBox<>();
        templates.addClassNames(LumoUtility.Flex.GROW);
        templates.addValueChangeListener(e -> textArea.setValue(getExampleTexts().get(templates.getValue())));
        templates.setAllowCustomValue(false);
        templates.setAriaLabel("Templates");
        templates.setItems("Template 001", "Template 002", "Template 003");
        templates.setPlaceholder("Select template");

        Button instructionsButton = new Button("Instructions", click -> instructionsDialog.open());
        instructionsDialog = new InstructionsDialog();

        Button clearButton = new Button("Clear", click -> textArea.clear());
        clearButton.setThemeName(ButtonVariant.LUMO_ERROR.getVariantName());

        Button fillButton = new Button("Fill the form", event -> {
            String input = textArea.getValue();
            if (input != null && !input.isEmpty()) {
                clearForm();

                HashMap<Component, String> instructions = new HashMap<>();
                instructions.put(nameField, instructionsDialog.getInstructionForName());
                instructions.put(emailField, instructionsDialog.getInstructionForEmail());
                instructions.put(typeServiceMulti, instructionsDialog.getInstructionsForTypeOfService());

                ArrayList<String> contextInstructions = new ArrayList<>();
                contextInstructions.add(instructionsDialog.getContextInstruction());

                FormFiller formFiller = new FormFiller(form, instructions, contextInstructions, new ChatGPTService());
                FormFillerResult result = formFiller.fill(input);
                getLogger().debug("GPT request for input text: \n\n {}", result.getRequest());
                getLogger().debug("GPT response for input text: \n\n {}", result.getResponse());
            } else {
                textArea.setErrorMessage("Input a text to fill the form.");
            }
        });
        fillButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Div toolbar = new Div(templates, instructionsButton, clearButton, fillButton);
        toolbar.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);

        textArea = new TextArea("Input Text", "Select a text or type your own...");
        textArea.setClearButtonVisible(true);
        textArea.setSizeFull();

        Section section = new Section(title, toolbar, textArea);
        section.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        section.getStyle().set("flex", "1");
        return section;
    }

    public HashMap<String, String> getExampleTexts() {
        HashMap<String, String> textsMap = new HashMap<>();
        templates.getListDataView().getItems().forEach(text -> {
            try {
                File textFile = new ClassPathResource("text/" + text + ".txt").getFile();
                textsMap.put(text, FileUtils.readFileToString(textFile, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Couldn't find a text example", e);
            }
        });
        return textsMap;
    }

    private void clearForm() {
        form.getChildren().forEach(component -> {
            if (component instanceof HasValue<?, ?>) {
                ((HasValue<?, ?>) component).clear();
            } else if (component instanceof Grid) {
                ((Grid<?>) component).setItems(new ArrayList<>());
            }
        });
    }

    private Component createFormSection() {
        H2 title = new H2("2. Review");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.LARGE);

        Section section = new Section(title, createForm());
        section.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        section.getStyle().set("flex", "1");
        return section;
    }

    private FormLayout createForm() {
        nameField = new TextField("Name");
        nameField.setId("name");

        TextField addressField = new TextField("Address");
        addressField.setId("address");

        TextField phoneField = new TextField("Phone");
        phoneField.setId("phone");

        IntegerField age = new IntegerField("Age");
        age.setId("age");

        emailField = new EmailField("Email");
        emailField.setId("email");

        PasswordField clientId = new PasswordField("Client id");
        clientId.setId("clientId");

        DateTimePicker dateCreationField = new DateTimePicker("Creation date");
        dateCreationField.setId("creationDate");

        DatePicker dueDateField = new DatePicker("Due date");
        dueDateField.setId("dueDate");

        ComboBox<String> orderEntity = new ComboBox<>("Order entity");
        orderEntity.setId("orderEntity");
        orderEntity.setItems("Person", "Company");

        NumberField orderTotal = new NumberField("Order total");
        orderTotal.setId("orderTotal");

        BigDecimalField orderTaxes = new BigDecimalField("Order taxes");
        orderTaxes.setId("orderTaxes");

        TextArea orderDescription = new TextArea("Order description");
        orderDescription.setId("orderDescription");

        RadioButtonGroup<String> paymentMethod = new RadioButtonGroup<>("Payment method");
        paymentMethod.setItems("Credit card", "Cash", "Paypal");
        paymentMethod.setId("paymentMethod");

        Checkbox isFinnishCustomer = new Checkbox("Finnish customer");
        isFinnishCustomer.setId("isFinnishCustomer");

        CheckboxGroup<String> typeService = new CheckboxGroup<>("Type of service");
        typeService.setItems("Software", "Hardware", "Consultancy");
        typeService.setId("typeService");

        typeServiceMulti = new MultiSelectComboBox<>("Type of service");
        typeServiceMulti.setItems("Software", "Hardware", "Consultancy");
        typeServiceMulti.setId("typeServiceMs");

        H3 orders = new H3("Orders");
        orders.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);

        Grid<OrderItem> grid = createOrderGrid();

        form = new FormLayout(nameField, addressField, phoneField, age, emailField, clientId, dateCreationField,
                dueDateField, orderEntity, orderTotal, orderTaxes, orderDescription, paymentMethod, isFinnishCustomer,
                typeService, typeServiceMulti, orders, grid);

        // Stretch the grid over 2 columns
        form.setColspan(orders, 2);
        form.setColspan(grid, 2);

        form.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if mainLayout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));

        return form;
    }

    @NotNull
    private Grid<OrderItem> createOrderGrid() {
        // To make the grid supported by FormFiller it is necessary to set an ID
        // and a Bean class to the Grid.
        final Grid<OrderItem> grid = new Grid<>(OrderItem.class);
        grid.setId("orders");

        // Grid columns headers and Ids for the FormFiller. The IDs have to be
        // equals to the name of the related field of the Bean class.
        grid.getColumnByKey("orderId").setHeader("Id");
        grid.getColumnByKey("itemName").setHeader("Name");
        grid.getColumnByKey("orderDate").setHeader("Date");
        grid.getColumnByKey("orderStatus").setHeader("Status");
        grid.getColumnByKey("orderTotal").setHeader("Cost");

        // Editor
        initGridEditor(grid);

        return grid;
    }

    private void initGridEditor(Grid grid) {
        // Example of editor with FormFiller just as any regular editor of Flow Grid
        Editor<OrderItem> editor = grid.getEditor();
        Grid.Column<OrderItem> editColumn = grid.addComponentColumn(order -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(order);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0).setKey("editor");

        Binder<OrderItem> binder = new Binder<>(OrderItem.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField edOrderIdField = new TextField();
        edOrderIdField.setWidthFull();
        binder.forField(edOrderIdField).bind(OrderItem::getOrderId, OrderItem::setOrderId);
        grid.getColumnByKey("orderId").setEditorComponent(edOrderIdField);

        TextField edNItemNameField = new TextField();
        edNItemNameField.setWidthFull();
        binder.forField(edNItemNameField).bind(OrderItem::getItemName, OrderItem::setItemName);
        grid.getColumnByKey("itemName").setEditorComponent(edNItemNameField);

        NumberField edCostField = new NumberField();
        edCostField.setWidthFull();
        binder.forField(edCostField).bind(OrderItem::getOrderTotal, OrderItem::setOrderTotal);
        grid.getColumnByKey("orderTotal").setEditorComponent(edCostField);

        DatePicker edDateField = new DatePicker();
        edDateField.setWidthFull();
        binder.forField(edDateField).bind(OrderItem::getOrderDate,
                OrderItem::setOrderDate);
        grid.getColumnByKey("orderDate").setEditorComponent(edDateField);

        TextField edStatusItemField = new TextField();
        edStatusItemField.setWidthFull();
        binder.forField(edStatusItemField).bind(OrderItem::getOrderStatus, OrderItem::setOrderStatus);
        grid.getColumnByKey("orderStatus").setEditorComponent(edStatusItemField);

        Button saveButton = new Button("Save", e -> editor.save());

        Button cancelButton = new Button("Close", e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setPadding(false);

        editColumn.setEditorComponent(actions);
    }

}

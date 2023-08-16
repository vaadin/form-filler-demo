package com.example.application.views;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.application.data.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.ai.formfiller.FormFiller;
import com.vaadin.flow.ai.formfiller.FormFillerResult;
import com.vaadin.flow.ai.formfiller.services.ChatGPTService;
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

    private final FormLayout formLayout;

    public FormFillerTextDemo() {
        setPadding(true);

        formLayout = new FormLayout();

        TextField nameField = new TextField("Name");
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

        EmailField emailField = new EmailField("Email");
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

        // To make the grid supported by FormFiller it is necessary to set an ID
        // and a Bean class to the Grid.
        Grid<OrderItem> orderGrid = new Grid<>(OrderItem.class);
        orderGrid.setId("orders");

        // Grid columns headers and Ids for the FormFiller. The IDs have to be
        // equals to the name of the related field of the Bean class.
        orderGrid.getColumnByKey("orderId").setHeader("Id");
        orderGrid.getColumnByKey("itemName").setHeader("Name");
        orderGrid.getColumnByKey("orderDate").setHeader("Date");
        orderGrid.getColumnByKey("orderStatus").setHeader("Status");
        orderGrid.getColumnByKey("orderTotal").setHeader("Cost");

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

        ComboBox<String> texts = new ComboBox<>();
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
                FormFiller formFiller = new FormFiller(formLayout, new HashMap<>(), new ArrayList<>(), new ChatGPTService());
                FormFillerResult result = formFiller.fill(input);
                getLogger().debug("GPT request for input text: \n\n {}", result.getRequest());
                getLogger().debug("GPT response for input text: \n\n {}", result.getResponse());
            } else {
                inputText.setErrorMessage("Input a text to fill the form.");
            }
        });

        Button clearButton = new Button("Clear", click -> inputText.clear());
        clearButton.setThemeName(ButtonVariant.LUMO_ERROR.getVariantName());

        Button addInstructionsButton = new Button("Add instructions...", click -> {

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
        HashMap<String, String> texts = new HashMap<>();
        texts.put("Text1", "Order sent by the customer Andrew Jackson on '2023-04-05 12:13:00'\n" +
                "Address: Ruukinkatu 2-4, FI-20540 Turku, Finland \n" +
                "Phone Number: 555-1234 \n" +
                "Age: 43 \n" +
                "Client ID: 45XXD6543 \n" +
                "Email: 'andrewjackson@gmail.com \n" +
                "Due Date: 2023-05-05\n" +
                "\n" +
                "This order contains the products for the project 'Form filler AI Addon' that is part of the new development of Vaadin AI kit. \n" +
                "\n" +
                "Items list:\n" +
                "Item Number     Items   Type    Cost    Date    Status\n" +
                "1001    2 Smartphones   Hardware    $1000   '2023-01-10' Delivered\n" +
                "1002    1 Laptop    Hardware    $1500   '2023-02-15'    In Transit\n" +
                "1003    5 Wireless Headphones   Hardware    $500    '2023-03-20'    Cancelled\n" +
                "1004    1 Headphones    Hardware    $999    '2023-01-01'    In Transit\n" +
                "1005    1 Windows License    Software    $1500    '2023-02-01'    Delivered\n" +
                "\n" +
                "Taxes: 25,6€ \n" +
                "Total: 15000€ \n" +
                "Payment Method: Cash\n");
        texts.put("Text2", "Order sent by the customer Andrew Jackson on '2023-04-05 12:13:00'\n" +
                "Address: 1234 Elm Street, Springfield, USA \n" +
                "Phone Number: 555-1234 \n" +
                "Age: 37 \n" +
                "Client ID: 45XXD6543 \n" +
                "Email: 'andrewjackson#gmail.com \n" +
                "Due Date: 2023-05-05\n" +
                "\n" +
                "This order contains the products for the project 'Form filler AI Addon' that is part of the new development of Vaadin AI kit. \n" +
                "\n" +
                "Items list:\n" +
                "Item Number     Items   Type    Cost    Date    Status\n" +
                "1001    2 Smartphones   Hardware    $1000   '2023-01-10' Delivered\n" +
                "1002    1 Laptop    Hardware    $1500   '2023-02-15'    In Transit\n" +
                "1003    5 Wireless Headphones   Hardware    $500    '2023-03-20'    Cancelled\n" +
                "1004    1 Headphones    Hardware    $999    '2023-01-01'    In Transit\n" +
                "\n" +
                "Taxes: 35,6€ \n" +
                "Total: 10000€ \n" +
                "Payment Method: Credit Card");
        texts.put("Text3", "This is an invoice of an order for the project 'Vaadin AI Form Filler'" +
                " providing some hardware and sent by the customer Andrew Jackson with client id 45XXD6543, who lives at " +
                "Ruukinkatu 2-4, FI-20540 Turku (Finland), he is 45 years old and can be reached at phone number 555-1234 " +
                "and at email 'andrewjackson@gmail.com. Andrew has placed five items: number 1001 " +
                "contains two items of smartphone for a total of $1,000 placed on 2023 January " +
                "the 10th with a status of deliberate; number 1002 includes one item of laptop " +
                "with a total of $1,500 placed on 2023 February the 15th with a status of in transit; " +
                "number 1003 consists of five items of wireless headphones for a total of $500 placed " +
                "on 2023 March the 20th with a status of cancelled; number 1004 is for 'Headphones' " +
                "with a cost of $999 and placed on '2023-01-01' with status In transit. The invoice " +
                "was paid using a Paypal account. The taxes included in the invoice are 40,6€ and Total is 20000€");
        return texts;
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(FormFillerTextDemo.class.getName());
    }

}

package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

public class InstructionsDialog extends Dialog {

    private String instructionForName = "Format this field in Uppercase";

    private String instructionForEmail = "Format this field as a correct email";

    private String contextInstruction = "Translate item names to English";

    private String instructionsForTypeOfService = "This field describes the type of the items of the order among hardware, software or consultancy options";

    private TextField name;

    private EmailField email;

    private TextField typeOfService;

    private TextField context;

    public InstructionsDialog() {
        setHeaderTitle("Add extra instructions for Form Filler");

        VerticalLayout dialogLayout = createDialogLayout();
        add(dialogLayout);

        Button saveButton = createSaveButton();
        Button cancelButton = new Button("Cancel", e -> close());
        Button clearButton = new Button("Clear", e -> {
            name.setValue("");
            email.setValue("");
            typeOfService.setValue("");
            context.setValue("");
        });
        clearButton.setThemeName(ButtonVariant.LUMO_ERROR.getVariantName());
        getFooter().add(cancelButton);
        getFooter().add(clearButton);
        getFooter().add(saveButton);

        addOpenedChangeListener(e -> fillInstructions());

        setWidth("600px");
    }

    public String getInstructionForName() {
        return instructionForName;
    }

    public String getInstructionForEmail() {
        return instructionForEmail;
    }

    public String getContextInstruction() {
        return contextInstruction;
    }

    public String getInstructionsForTypeOfService() {
        return instructionsForTypeOfService;
    }

    private VerticalLayout createDialogLayout() {
        name = new TextField("Instruction for name");
        email = new EmailField("Instructions for e-mail");
        typeOfService = new TextField("Instructions for Type Of Service");
        context = new TextField("Context instruction");

        VerticalLayout dialogLayout = new VerticalLayout(name, email, typeOfService, context);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        return dialogLayout;
    }

    private void fillInstructions() {
        name.setValue(instructionForName);
        email.setValue(instructionForEmail);
        typeOfService.setValue(instructionsForTypeOfService);
        context.setValue(contextInstruction);
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save", click -> {
            instructionForName = name.getValue();
            instructionForEmail = email.getValue();
            instructionsForTypeOfService = typeOfService.getValue();
            contextInstruction = context.getValue();
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }
}

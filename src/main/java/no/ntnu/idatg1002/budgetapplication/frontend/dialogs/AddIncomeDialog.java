package no.ntnu.idatg1002.budgetapplication.frontend.dialogs;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import no.ntnu.idatg1002.budgetapplication.backend.Income;
import no.ntnu.idatg1002.budgetapplication.backend.IncomeCategory;
import no.ntnu.idatg1002.budgetapplication.backend.RecurringType;

public class AddIncomeDialog extends Dialog<Income> {
  Income newIncome;

  @FXML private TextField incomeAmountField;
  @FXML private TextField incomeDescriptionField;

  @FXML private ComboBox<IncomeCategory> incomeCategoryComboBox;

  @FXML private ComboBox<RecurringType> recurringIntervalComboBox;

  public AddIncomeDialog() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlfiles/addIncomeDialog.fxml"));
    loader.setController(this);

    DialogPane dialogPane = new DialogPane();

    try {
      dialogPane.setContent(loader.load());
    } catch (IOException e) {
      e.printStackTrace();
    }

    String css =
        Objects.requireNonNull(this.getClass().getResource("/cssfiles/dialog.css"))
            .toExternalForm();
    dialogPane.getStylesheets().add(css);

    this.setDialogPane(dialogPane);
    this.setTitle("Add Income");

    ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
    this.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

    this.setResultConverter(
        dialogButton -> {
          if (dialogButton == submitButton) {
            if (assertAllFieldsValid()) {
              newIncome =
                  new Income(
                      Integer.parseInt(getIncomeAmountField()),
                      getIncomeDescriptionField(),
                      getRecurringIntervalComboBox(),
                      getIncomeCategoryComboBox());
              return newIncome;
            } else {
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle("Error");
              alert.setHeaderText(null);
              alert.setContentText("Please fill out all fields in dialog");
              alert.showAndWait();
            }
          }
          return null;
        });

    // adds enums to combo boxes
    recurringIntervalComboBox.getItems().addAll(RecurringType.values());
    incomeCategoryComboBox.getItems().addAll(IncomeCategory.values());

    // force the field to be numeric only
    incomeAmountField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.matches("\\d*")) {
                incomeAmountField.setText(newValue.replaceAll("[^\\d]", ""));
              }
            });

    // force the field to not start with space
    incomeDescriptionField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if ((oldValue.isEmpty() || oldValue.isBlank()) && newValue.matches(" ")) {
                incomeDescriptionField.clear();
              }
            });
  }

  public String getIncomeDescriptionField() {
    return incomeDescriptionField.getText();
  }

  public String getIncomeAmountField() {
    return incomeAmountField.getText();
  }

  public RecurringType getRecurringIntervalComboBox() {
    return recurringIntervalComboBox.getValue();
  }

  public IncomeCategory getIncomeCategoryComboBox() {
    return incomeCategoryComboBox.getValue();
  }

  boolean assertAllFieldsValid() {
    return (incomeDescriptionField.getText() != null
        && incomeAmountField.getText() != null
        && recurringIntervalComboBox.getValue() != null
        && incomeCategoryComboBox.getValue() != null);
  }
}

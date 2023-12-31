package no.ntnu.idatg1002.budgetapplication.frontend.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import no.ntnu.idatg1002.budgetapplication.backend.SecurityQuestion;
import no.ntnu.idatg1002.budgetapplication.backend.accountinformation.Account;
import no.ntnu.idatg1002.budgetapplication.backend.accountinformation.AccountDAO;
import no.ntnu.idatg1002.budgetapplication.backend.accountinformation.SessionAccount;
import no.ntnu.idatg1002.budgetapplication.frontend.alerts.ExceptionAlert;
import no.ntnu.idatg1002.budgetapplication.frontend.alerts.WarningAlert;

/**
 * Controller class for registering new accounts in the budget application.
 *
 * @author Erik Bjørnsen, Simon Husås Houmb, Eskil Alstad
 * @version 1.2
 */
public class RegisterNewAccountController {

  @FXML // ResourceBundle that was given to the FXMLLoader
  private ResourceBundle resources;

  @FXML // URL location of the FXML file that was given to the FXMLLoader
  private URL location;

  @FXML private Text emailText; // Value injected by FXMLLoader
  @FXML private Text pinCodeText; // Value injected by FXMLLoader
  @FXML private Text registerNewAccountText; // Value injected by FXMLLoader
  @FXML private Text securityQuestionAnswerText; // Value injected by FXMLLoader
  @FXML private Text securityQuestionText; // Value injected by FXMLLoader
  @FXML private Text usernameText; // Value injected by FXMLLoader

  @FXML private TextField securityQuestionAnswerTextField; // Value injected by FXMLLoader
  @FXML private TextField usernameTextField; // Value injected by FXMLLoader
  @FXML private TextField pinCodeTextField; // Value injected by FXMLLoader
  @FXML private TextField emailTextField; // Value injected by FXMLLoader

  @FXML private Button registerNewAccountButton; // Value injected by FXMLLoader
  @FXML private Button backToLoginButton; // Value injected by FXMLLoader
  @FXML private ComboBox<String> securityQuestionComboBox; // Value injected by FXMLLoader

  /** Initializes the view. */
  @FXML // This method is called by the FXMLLoader when initialization is complete
  void initialize() {
    configureSecurityQuestionComboBox();
    for (SecurityQuestion securityQuestion : SecurityQuestion.values()) {
      securityQuestionComboBox.getItems().add(securityQuestion.getSecurityQuestionString());

      configureAllTextFields();
    }
  }

  /**
   * Navigates back to the login screen.
   *
   * @param event The action event triggered by the user.
   * @throws IOException If an error occurs while loading the login screen.
   */
  @FXML
  void goBackToLogin(ActionEvent event) throws IOException {
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxmlfiles/login.fxml")));
    String css =
        Objects.requireNonNull(this.getClass().getResource("/cssfiles/primary.css"))
            .toExternalForm();
    Scene scene = ((Node) event.getSource()).getScene();
    scene.getStylesheets().add(css);
    scene.setRoot(root);
  }

  /** Configures the behavior of the security question ComboBox. */
  private void configureSecurityQuestionComboBox() {
    securityQuestionComboBox
        .focusedProperty()
        .addListener(
            (observableValue, oldPropertyValue, newPropertyValue) -> {
              if (Boolean.TRUE.equals(newPropertyValue)) {
                securityQuestionComboBox.show();
              } else {
                securityQuestionComboBox.hide();
              }
            });
  }

  /**
   * Attempts to register a new account with the provided information.
   *
   * @param event The action event triggered by the user.
   * @throws IOException If an error occurs while loading the login screen.
   */
  @FXML
  void registerNewAccount(ActionEvent event) throws IOException {
    if (assertAllFieldsValid()) {
      try {
        Account newAccount =
            new Account(
                usernameTextField.getText(),
                emailTextField.getText(),
                pinCodeTextField.getText(),
                reverseStringToSecurityQuestion(securityQuestionComboBox.getValue()),
                securityQuestionAnswerTextField.getText());
        AccountDAO.getInstance().add(newAccount);
        SessionAccount.getInstance().setAccount(newAccount);
        goToLoginScreen(event);
      } catch (IllegalArgumentException exception) {
        ExceptionAlert exceptionAlert = new ExceptionAlert(exception);
        exceptionAlert.showAndWait();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      generateDynamicFeedbackAlert();
    }
  }

  /**
   * Checks if all required fields are filled in.
   *
   * @return true if all fields are valid, false otherwise.
   */
  private boolean assertAllFieldsValid() {
    return (!usernameTextField.getText().isEmpty()
        && !usernameTextField.getText().isBlank()
        && !emailTextField.getText().isEmpty()
        && !emailTextField.getText().isBlank()
        && !pinCodeTextField.getText().isEmpty()
        && !pinCodeTextField.getText().isBlank()
        && securityQuestionComboBox.getValue() != null
        && !securityQuestionAnswerTextField.getText().isEmpty()
        && !securityQuestionAnswerTextField.getText().isBlank());
  }

  /**
   * Navigates to the login screen.
   *
   * @param event The event that triggered the navigation.
   * @throws IOException If an error occurs while loading the login screen.
   */
  private void goToLoginScreen(Event event) throws IOException {
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxmlfiles/login.fxml")));
    Scene scene = ((Node) event.getSource()).getScene();
    scene.setRoot(root);
  }

  /**
   * Converts a security question string to its corresponding SecurityQuestion enum value.
   *
   * @param questionString The security question string.
   * @return The corresponding SecurityQuestion enum value, or null if not found.
   */
  private SecurityQuestion reverseStringToSecurityQuestion(String questionString) {
    for (SecurityQuestion securityQuestion : SecurityQuestion.values()) {
      if (securityQuestion.getSecurityQuestionString().equalsIgnoreCase(questionString)) {
        return securityQuestion;
      }
    }
    return null;
  }

  /** Configures all text fields to have specific behaviors. */
  private void configureAllTextFields() {
    configurePinCodeTextField();
    configureSecurityQuestionAnswerTextField();
    makeTextFieldNotStartWithSpace(usernameTextField);
    makeTextFieldNotStartWithSpace(emailTextField);
  }

  /** Configures the behavior of the pin code TextField. */
  private void configurePinCodeTextField() {
    pinCodeTextField
        .textProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              // numeric only
              if (!newValue.matches("\\d*")) {
                pinCodeTextField.setText(newValue.replaceAll("[^\\d]", ""));
              }
              // max 4 digits
              if (newValue.length() > 4) {
                pinCodeTextField.setText(oldValue);
              }
            });
  }

  /** Configures the behavior of the security question answer TextField. */
  private void configureSecurityQuestionAnswerTextField() {
    securityQuestionAnswerTextField
        .textProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              // need to have selected a question
              if (reverseStringToSecurityQuestion(securityQuestionComboBox.getValue()) == null
                  || (oldValue.isEmpty() || oldValue.isBlank()) && newValue.matches(" ")) {
                securityQuestionAnswerTextField.clear();
              }
            });
  }

  /**
   * Configures a TextField to not accept space as its first character.
   *
   * @param textField The TextField to configure.
   */
  private void makeTextFieldNotStartWithSpace(TextField textField) {
    textField
        .textProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if ((oldValue.isEmpty() || oldValue.isBlank()) && newValue.matches(" ")) {
                textField.clear();
              }
            });
  }

  /** Displays a dynamic feedback alert with information about missing/invalid fields. */
  @FXML
  private void generateDynamicFeedbackAlert() {
    StringBuilder builder = new StringBuilder("Please fill out the following field(s): \n");
    if (usernameTextField.getText().isEmpty() || usernameTextField.getText().isBlank()) {
      builder.append("Username \n");
    }
    if (emailTextField.getText().isEmpty() || emailTextField.getText().isBlank()) {
      builder.append("Email \n");
    }
    if (pinCodeTextField.getText().isEmpty() || pinCodeTextField.getText().isBlank()) {
      builder.append("Pin code \n");
    }
    if (securityQuestionComboBox.getValue() == null) {
      builder.append("Security question \n");
    }
    if (securityQuestionAnswerTextField.getText().isEmpty()
        || securityQuestionAnswerTextField.getText().isBlank()) {
      builder.append("Security question answer \n");
    }
    WarningAlert warningAlert = new WarningAlert();
    warningAlert.setContentText(builder.toString());
    warningAlert.showAndWait();
  }
}

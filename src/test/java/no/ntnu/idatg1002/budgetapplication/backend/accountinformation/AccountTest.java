package no.ntnu.idatg1002.budgetapplication.backend.accountinformation;

import static org.junit.jupiter.api.Assertions.*;

import no.ntnu.idatg1002.budgetapplication.backend.Budget;
import no.ntnu.idatg1002.budgetapplication.backend.ExpenseCategory;
import no.ntnu.idatg1002.budgetapplication.backend.Expense;
import no.ntnu.idatg1002.budgetapplication.backend.Income;
import no.ntnu.idatg1002.budgetapplication.backend.IncomeCategory;
import no.ntnu.idatg1002.budgetapplication.backend.RecurringType;
import no.ntnu.idatg1002.budgetapplication.backend.SecurityQuestion;
import no.ntnu.idatg1002.budgetapplication.backend.SavingsPlan;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

class AccountTest {
  private AccountDAO accountDAO;
  private SessionAccount sessionAccount;
  private Account erikAccount;
  private Account simonAccount;
  private Budget budget;
  private Income income;
  private Expense expense;
  private SavingsPlan savingsPlan;

  private final ArrayList<String> testEmails =
      new ArrayList<>(List.of(new String[] {"erbj@ntnu.no", "simonhou@ntnu.no"}));

  @BeforeEach
  void setUp() {
    // accountDAO = AccountDAO.getInstance();
    accountDAO = new AccountDAO();
    sessionAccount = SessionAccount.getInstance();

    removeTestAccounts();

    erikAccount =
        new Account(
            "Erik Bjørnsen", "erbj@ntnu.no", "1234", SecurityQuestion.FAVORITE_FOOD, "Pizza");
    simonAccount =
        new Account("Simon Houmb", "simonhou@ntnu.no", "1234", SecurityQuestion.CAR_BRAND, "BMW");

    accountDAO.add(erikAccount);
    accountDAO.add(simonAccount);

    sessionAccount.setAccount(erikAccount);

    budget = new Budget("Test budget");
    income = new Income(50, "Test income", RecurringType.NONRECURRING, IncomeCategory.PASSIVE);
    expense = new Expense(50, "Test expense", RecurringType.NONRECURRING, ExpenseCategory.HOUSING);

    budget.addBudgetIncome(income);
    budget.addBudgetExpenses(expense);

    sessionAccount.getAccount().addBudget(budget);

    savingsPlan = new SavingsPlan("Test savingsplan");

    System.out.println("after setup: " + accountDAO.getAll());
  }

  @AfterEach
  void tearDown() {
    // removeTestAccounts();

    /*
    System.out.println("start of teardown:getAll(): " + accountDAO.getAll());
    for (Account account : accountDAO.getAll()) {
      if (testEmails.contains(account.getEmail())) {
        if (account.getId() != null) {
          accountDAO.remove(account);
          System.out.println("removed" + account);
        } else {
          System.out.println("Account with null ID found: " + account);
        }
      }
    }

     */

    accountDAO.close();

    // System.out.println("after teardown: " + accountDAO.getAllEmails());
  }

  private void removeTestAccounts() {
    if (accountDAO.getAllEmails().contains(testEmails.get(0))
        || accountDAO.getAllEmails().contains(testEmails.get(1))) {
      System.out.println("before removeTestAccounts(): " + accountDAO.getAll());
      accountDAO.remove(accountDAO.getAccountByEmail(testEmails.get(0)));
      accountDAO.remove(accountDAO.getAccountByEmail(testEmails.get(1)));
      System.out.println("after removeTestAccounts(): " + accountDAO.getAll());
    }
  }

  @Nested
  class SetEmailTest {
    @Test
    void emailHasAtSignAndIsNotEmptyOrBlank() {
      assertDoesNotThrow(() -> sessionAccount.getAccount().setEmail("erikb@gmail.com"));
      assertEquals("erikb@gmail.com", sessionAccount.getAccount().getEmail());
    }

    @Test
    void emailHasNoAtSign() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().setEmail("erik.gmail.com"));
      assertEquals("Email does not contain '@'.", thrown.getMessage());
      assertNotEquals("erik.gmail.com", sessionAccount.getAccount().getEmail());
    }

    @Test
    void emailIsEmpty() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> sessionAccount.getAccount().setEmail(""));
      assertEquals("Email must not be empty or blank.", thrown.getMessage());
      assertNotEquals("", sessionAccount.getAccount().getEmail());
    }

    @Test
    void emailIsBlank() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> sessionAccount.getAccount().setEmail(" "));
      assertEquals("Email must not be empty or blank.", thrown.getMessage());
      assertNotEquals(" ", sessionAccount.getAccount().getEmail());
    }

    @Test
    void emailAlreadyInUse() {
      Account testAccount =
          new Account("Test", "test@test.com", "4444", SecurityQuestion.FAVORITE_FOOD, "Pizza");
      accountDAO.add(testAccount);
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> testAccount.setEmail("simon@gmail.com"));
      assertEquals("Email already in use.", thrown.getMessage());
      accountDAO.remove(testAccount);
    }
  }

  @Nested
  class securityQuestionTests {
    @Test
    void getAndSetSecurityQuestionPositive() {
      sessionAccount.getAccount().setSecurityQuestion(SecurityQuestion.FIRST_PET);
      assertEquals(SecurityQuestion.FIRST_PET, sessionAccount.getAccount().getSecurityQuestion());
    }

    @Test
    void getAndSetSecurityAnswerPositive() {
      assertDoesNotThrow(() -> sessionAccount.getAccount().setSecurityAnswer("Test answer"));
      assertEquals("Test answer", sessionAccount.getAccount().getSecurityAnswer());
    }
  }

  @Nested
  class setSecurityAnswerTest {
    @Test
    void securityAnswerIsNotEmptyOrBlank() {
      assertDoesNotThrow(() -> sessionAccount.getAccount().setSecurityAnswer("Test Answer"));
      assertEquals("Test Answer", sessionAccount.getAccount().getSecurityAnswer());
    }

    @Test
    void securityAnswerIsEmpty() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().setSecurityAnswer(""));
      assertEquals("Security answer must not be empty or blank.", thrown.getMessage());
      assertNotEquals("", sessionAccount.getAccount().getSecurityAnswer());
    }

    @Test
    void securityAnswerIsBlank() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().setSecurityAnswer(" "));
      assertEquals("Security answer must not be empty or blank.", thrown.getMessage());
      assertNotEquals(" ", sessionAccount.getAccount().getSecurityAnswer());
    }
  }

  @Nested
  class simpleGettersAndSettersTest {
    @Test
    void getNameReturnsCorrectName() {
      assertEquals("Test", sessionAccount.getAccount().getName());
    }

    @Test
    void getEmailReturnsCorrectEmail() {
      assertEquals("test@test.com", sessionAccount.getAccount().getEmail());
    }

    @Nested
    class setNameTest {
      @Test
      void nameIsNotBlankOrEmpty() {
        assertDoesNotThrow(() -> sessionAccount.getAccount().setName("Test Name"));
        assertEquals("Test Name", sessionAccount.getAccount().getName());
      }

      @Test
      void nameIsEmpty() {
        Exception thrown =
            assertThrows(
                IllegalArgumentException.class, () -> sessionAccount.getAccount().setName(""));
        assertEquals("Account name must not be empty or blank.", thrown.getMessage());
        assertNotEquals("", sessionAccount.getAccount().getName());
      }

      @Test
      void nameIsBlank() {
        Exception thrown =
            assertThrows(
                IllegalArgumentException.class, () -> sessionAccount.getAccount().setName(" "));
        assertEquals("Account name must not be empty or blank.", thrown.getMessage());
        assertNotEquals(" ", sessionAccount.getAccount().getName());
      }
    }
  }

  @Nested
  class SetPinCodeTest {
    @Test
    void pinCodeHasFourDigits() {
      assertDoesNotThrow(() -> sessionAccount.getAccount().setPinCode("0001"));
      assertEquals("0001", sessionAccount.getAccount().getPinCode());
    }

    @Test
    void pinCodeHasLessThanFourDigits() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> sessionAccount.getAccount().setPinCode("001"));
      assertEquals("Pin code must consist of 4 digits.", thrown.getMessage());
      assertNotEquals("001", sessionAccount.getAccount().getPinCode());
    }

    @Test
    void pinCodeHasMoreThanFourDigits() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().setPinCode("00001"));
      assertEquals("Pin code must consist of 4 digits.", thrown.getMessage());
      assertNotEquals("00001", sessionAccount.getAccount().getPinCode());
    }

    @Test
    void pinCodeHasOnlyLetters() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> sessionAccount.getAccount().setPinCode("code"));
      assertEquals("Pin code must only consist of numbers.", thrown.getMessage());
      assertNotEquals("code", sessionAccount.getAccount().getPinCode());
    }

    @Test
    void pinCodeHasLettersAndDigits() {
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class, () -> sessionAccount.getAccount().setPinCode("id09"));
      assertEquals("Pin code must only consist of numbers.", thrown.getMessage());
      assertNotEquals("id09", sessionAccount.getAccount().getPinCode());
    }
  }

  @Nested
  class addSavingsPlanTest {
    @Test
    void addNewSavingsPlanWithNotTakenName() {
      SavingsPlan testSavingsPlan = new SavingsPlan("My goal");
      assertDoesNotThrow(() -> sessionAccount.getAccount().addSavingsPlan(testSavingsPlan));
      assertTrue(sessionAccount.getAccount().getSavingsPlans().contains(testSavingsPlan));
    }

    @Test
    void addNewSavingsPlanWithTakenName() {
      sessionAccount.getAccount().addSavingsPlan(new SavingsPlan("My goal"));
      SavingsPlan testSavingsPlan = new SavingsPlan("My goal");
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().addSavingsPlan(testSavingsPlan));
      assertEquals("Savings plan goal name is taken.", thrown.getMessage());
      assertFalse(sessionAccount.getAccount().getSavingsPlans().contains(testSavingsPlan));
    }

    @Test
    void addExistingSavingsPlan() {
      SavingsPlan testSavingsPlan = new SavingsPlan("My goal");
      sessionAccount.getAccount().addSavingsPlan(testSavingsPlan);
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().addSavingsPlan(testSavingsPlan));
      assertEquals("An instance of the savings plan already exists.", thrown.getMessage());
    }
  }

  @Test
  void removeSavingsPlanTest() {
    sessionAccount.getAccount().addSavingsPlan(savingsPlan);
    sessionAccount.getAccount().removeSavingsPlan(savingsPlan);
    assertFalse(sessionAccount.getAccount().getSavingsPlans().contains(savingsPlan));
  }

  @Nested
  class addBudgetTest {
    @Test
    void addNewBudgetWithNotTakenName() {
      Budget testBudget = new Budget("My budget");
      assertDoesNotThrow(() -> sessionAccount.getAccount().addBudget(testBudget));
      assertTrue(sessionAccount.getAccount().getBudgets().contains(testBudget));
    }

    @Test
    void addNewBudgetWithTakenName() {
      sessionAccount.getAccount().addBudget(new Budget("My budget"));
      Budget testBudget = new Budget("My budget");
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().addBudget(testBudget));
      assertEquals("Budget name is taken.", thrown.getMessage());
      assertFalse(sessionAccount.getAccount().getBudgets().contains(testBudget));
    }

    @Test
    void addExistingBudget() {
      Budget testBudget = new Budget("My budget");
      sessionAccount.getAccount().addBudget(testBudget);
      Exception thrown =
          assertThrows(
              IllegalArgumentException.class,
              () -> sessionAccount.getAccount().addBudget(testBudget));
      assertEquals("An instance of the budget already exists.", thrown.getMessage());
    }
  }

  @Test
  void removeBudgetPositiveTest() {
    sessionAccount.getAccount().addBudget(budget);
    sessionAccount.getAccount().removeBudget(budget);
    assertFalse(sessionAccount.getAccount().getBudgets().contains(budget));
  }

  @Test
  void testThatSelectedBudgetIsInitializedCorrectly() {
    sessionAccount.getAccount().addBudget(budget);
    assertEquals(budget, sessionAccount.getAccount().getSelectedBudget());
  }

  @Test
  void testThatSelectedBudgetCanBeIncrementedAndLoops() {
    sessionAccount.getAccount().addBudget(budget);
    sessionAccount.getAccount().addBudget(new Budget("Test budget 2"));
    sessionAccount.getAccount().addBudget(new Budget("Test budget 3"));
    assertEquals("Test budget 3", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
    // Selected loops
    sessionAccount.getAccount().selectNextBudget();
    assertEquals("Test budget", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
    // Selected increments
    sessionAccount.getAccount().selectNextBudget();
    assertEquals("Test budget 2", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
  }

  @Test
  void testThatSelectedSavingsPlanCanBeIncrementedAndLoops() {
    sessionAccount.getAccount().addSavingsPlan(savingsPlan);
    sessionAccount.getAccount().addSavingsPlan(new SavingsPlan("Test savingsplan 2"));
    sessionAccount.getAccount().addSavingsPlan(new SavingsPlan("Test savingsplan 3"));
    assertEquals("Test savingsplan 3", erikAccount.getSelectedSavingsPlan().getGoalName());
    // Selected loops
    sessionAccount.getAccount().selectNextSavingsPlan();
    assertEquals(
        "Test savingsplan", sessionAccount.getAccount().getSelectedSavingsPlan().getGoalName());
    // Selected increments
    sessionAccount.getAccount().selectNextSavingsPlan();
    assertEquals(
        "Test savingsplan 2", sessionAccount.getAccount().getSelectedSavingsPlan().getGoalName());
  }

  @Test
  void testThatSelectedBudgetCanBeDecreasedAndLoops() {
    sessionAccount.getAccount().addBudget(budget);
    sessionAccount.getAccount().addBudget(new Budget("Test budget 2"));
    assertEquals("Test budget 2", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
    // Selected decreases
    sessionAccount.getAccount().selectPreviousBudget();
    assertEquals("Test budget", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
    // Selected loops
    sessionAccount.getAccount().selectPreviousBudget();
    assertEquals("Test budget 2", sessionAccount.getAccount().getSelectedBudget().getBudgetName());
  }

  @Test
  void testThatSelectedSavingsPlanCanBeDecreasedAndLoops() {
    sessionAccount.getAccount().addSavingsPlan(savingsPlan);
    sessionAccount.getAccount().addSavingsPlan(new SavingsPlan("Test savingsplan 2"));
    assertEquals(
        "Test savingsplan 2", sessionAccount.getAccount().getSelectedSavingsPlan().getGoalName());
    // Selected decreases
    sessionAccount.getAccount().selectPreviousSavingsPlan();
    assertEquals(
        "Test savingsplan", sessionAccount.getAccount().getSelectedSavingsPlan().getGoalName());
    // Selected loops
    sessionAccount.getAccount().selectPreviousSavingsPlan();
    assertEquals(
        "Test savingsplan 2", sessionAccount.getAccount().getSelectedSavingsPlan().getGoalName());
  }

  @Test
  void testThatSelectedSavingsPlanIsInitializedCorrectly() {
    sessionAccount.getAccount().addSavingsPlan(savingsPlan);
    assertEquals(savingsPlan, sessionAccount.getAccount().getSelectedSavingsPlan());
  }
}

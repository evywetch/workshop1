package rsvier.workshop.controller;

import rsvier.workshop.dao.*;
import rsvier.workshop.domain.Account;
import rsvier.workshop.service.Hashing;
import rsvier.workshop.service.Validator;
import rsvier.workshop.view.AccountView;

public class AccountController extends Controller{

	private AccountView accountView = new AccountView();
	private Validator validator = new Validator();
	private Hashing hashing = new Hashing();


	
	public AccountController() {
	}
	
	

	
	@Override
	public void runView() {
		
		accountView.printHeaderMessage();
		changeAccountSwitch(searchAccountByMail());

	}


	public Account searchAccountByMail(){

		accountView.printRequestEmailInput();
		return DAOFactory.getAccountDAO().getAccountByEmail(accountView.getStringInput());

	}

	public void changeAccountSwitch(Account account) {


		if (account.getEmail() != null) {
			boolean updating = true;

			while (updating) {
				accountView.printMenuMessage();
				int choice = accountView.getIntInput();
				switch (choice) {
					case 1: // change e-mail
						updateEmail(account);
						break;

					case 2: // change password
						updatePassword(account);
						break;

					case 3: // save changes
						DAOFactory.getAccountDAO().updateAccount(account);
						accountView.printConfirmUpdateAccount();
						updating = false;
						MainController.setController(MainController.TypeOfController.EMPLOYEE);
						break;

					case 0: // cancel
						updating = false;
						MainController.setController(MainController.TypeOfController.EMPLOYEE);
						break;

					default:
						accountView.printMenuInputIsWrong();
				}
			}
		} else {
			accountView.printAccountNotFound();
			MainController.setController(MainController.TypeOfController.EMPLOYEE);
		}
	}
	
	
	public String requestAndValidateEmail() {
		
		String email;
		
		do {
			accountView.printRequestEmailInput();
			email = accountView.getStringInput();
		
		} while (!validator.validateEmail(email));

		return email;
	}
	
	public void updateEmail (Account account) {
		String email = requestAndValidateEmail();
		account.setEmail(email);
	}

	public void updatePassword (Account account) {
		String hashedPassword = requestAndValidatePassword();
		account.setPassword(hashedPassword);
	}

	public String requestAndValidatePassword() {
		
		String password;
		
		do {
			accountView.printRequestPasswordInput();
			password = accountView.getStringInput();
		
		} while (!validator.validatePassword(password));

		return hashing.createHash(password);
	}

	
	public Account doCreateAccount() {
		
		String email = requestAndValidateEmail();
		String hashedPassword = requestAndValidatePassword();
		Account account = new Account(email, hashedPassword);
		account.setAccountId(DAOFactory.getAccountDAO().createAccount(account));
		accountView.printYourAccountHasBeenCreated();
		return account;
	}
}

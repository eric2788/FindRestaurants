package me.sin.findrestaurants.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

import me.sin.findrestaurants.data.LoginRepository;
import me.sin.findrestaurants.data.Result;
import me.sin.findrestaurants.data.model.LoggedInUser;
import me.sin.findrestaurants.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    public LoginViewModel() {
        this.loginRepository = LoginRepository.getInstance();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        ExecutorService executor = ForkJoinPool.commonPool();
        executor.submit(() -> {
            Result<LoggedInUser> result = loginRepository.login(username, password);
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data.getUserId())));
            } else {
                Exception error = ((Result.Error)result).getError();
                error.printStackTrace();
                loginResult.postValue(new LoginResult(error.getMessage()));
            }
        });
    }

    public void register(String username, String password){
        ExecutorService executor = ForkJoinPool.commonPool();
        executor.submit(() -> {
            Result<LoggedInUser> result = loginRepository.register(username, password);
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data.getUserId())));
            } else {
                Exception error = ((Result.Error)result).getError();
                error.printStackTrace();
                loginResult.postValue(new LoginResult(error.getMessage()));
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
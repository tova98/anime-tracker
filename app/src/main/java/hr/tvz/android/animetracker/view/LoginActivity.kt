package hr.tvz.android.animetracker.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import hr.tvz.android.animetracker.LoginContract
import hr.tvz.android.animetracker.R
import hr.tvz.android.animetracker.databinding.ActivityLoginBinding
import hr.tvz.android.animetracker.dto.UserDto
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.model.User
import hr.tvz.android.animetracker.presenter.LoginPresenter

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var presenter: LoginContract.Presenter
    private lateinit var binding: ActivityLoginBinding

    private lateinit var handler: Handler
    private lateinit var hideMessageRunnable: Runnable
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences("AnimeTrackerSharedPrefs", Context.MODE_PRIVATE)
        val themeId: Int = sp.getInt("themeId", R.style.AnimeTrackerTheme)
        setTheme(themeId)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter(LoginPresenter(this))
        presenter.onViewCreated()

        handler = Handler(Looper.getMainLooper())
        sharedPreferences = getSharedPreferences("AnimeTrackerSharedPrefs", Context.MODE_PRIVATE)

        setButtonListeners()
        setHideErrorMessageRunnable()

        autoLoginIfValuesSet()
    }

    override fun setPresenter(presenter: LoginContract.Presenter) {
        this.presenter = presenter
    }

    override fun navigateOnLoginSuccess(user: User, userDto: UserDto) {
        hideProgress()
        CurrentUser.user = user
        saveLoginInfoToSharedPrefs(userDto)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayRegistrationSuccess(message: String) {
        hideProgress()
        handler.removeCallbacks(hideMessageRunnable)

        binding.messageField.setTextColor(Color.GREEN)
        binding.messageField.text = message
        binding.messageField.visibility = View.VISIBLE

        handler.postDelayed(hideMessageRunnable,3000)
    }

    override fun displayErrorMessage(errorMessage: String) {
        hideProgress()
        handler.removeCallbacks(hideMessageRunnable)

        binding.messageField.setTextColor(Color.RED)
        binding.messageField.text = errorMessage
        binding.messageField.visibility = View.VISIBLE

        handler.postDelayed(hideMessageRunnable,3000)
    }

    override fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progress.visibility = View.INVISIBLE
    }

    override fun setUsernameError(errorMessage: String) {
        binding.usernameField.error = errorMessage
    }

    override fun setPasswordError(errorMessage: String) {
        binding.passwordField.error = errorMessage
    }

    private fun setButtonListeners() {
        binding.loginButton.setOnClickListener {
            val userDto = UserDto(binding.usernameField.text.toString(), binding.passwordField.text.toString())
            presenter.loginUser(userDto)
        }

        binding.registerButton.setOnClickListener {
            val userDto = UserDto(binding.usernameField.text.toString(), binding.passwordField.text.toString())
            presenter.registerUser(userDto)
        }
    }

    private fun setHideErrorMessageRunnable() {
        hideMessageRunnable = Runnable {
            binding.messageField.text = ""
            binding.messageField.visibility = View.INVISIBLE
        }
    }

    private fun saveLoginInfoToSharedPrefs(userDto: UserDto) {
        val spEditor: SharedPreferences.Editor = sharedPreferences.edit()
        spEditor.putString("username", userDto.username)
        spEditor.putString("password", userDto.password)
        spEditor.apply()
    }

    private fun autoLoginIfValuesSet() {
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")
        if(username != "" && password != "") {
            showProgress()
            binding.usernameField.setText(username)
            binding.passwordField.setText(password)
            val userDto = UserDto(username, password)
            presenter.loginUser(userDto)
        }
    }
}
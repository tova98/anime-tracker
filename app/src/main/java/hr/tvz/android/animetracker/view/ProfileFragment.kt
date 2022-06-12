package hr.tvz.android.animetracker.view

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import hr.tvz.android.animetracker.ProfileContract
import hr.tvz.android.animetracker.R
import hr.tvz.android.animetracker.databinding.FragmentProfileBinding
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.model.User
import hr.tvz.android.animetracker.presenter.Api
import hr.tvz.android.animetracker.presenter.ProfilePresenter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var binding: FragmentProfileBinding

    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        resultLauncher = setupActivityForResult()

        sharedPreferences = requireActivity().getSharedPreferences("AnimeTrackerSharedPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if(enter) {
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
        } else {
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        setPresenter(ProfilePresenter(this))
        presenter.onViewCreated()
        setUserFields()

        binding.changePasswordButton.setOnClickListener {
            showDialog(container)
        }

        binding.uploadImageButton.setOnClickListener {
            handleImageUploadButton()
        }

        binding.changeThemeButton.setOnClickListener {
            handleChangeThemeButton(container)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        return binding.root
    }

    override fun setPresenter(presenter: ProfileContract.Presenter) {
        this.presenter = presenter
    }

    override fun displayMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun displayEpisodesWatched(count: Int) {
        binding.episodesProfileField.text = count.toString()
        displayTimeSpent(count)

        if(count >= 100) {
            showCongratulationsNotification()
        }
    }

    override fun displayShowsWatched(count: Int) {
        binding.showsProfileField.text = count.toString()
    }

    override fun updateCurrentUserParams(user: User) {
        CurrentUser.user = user
    }

    private fun displayTimeSpent(timeMinutes: Int) {
        val time = timeMinutes * 24
        binding.timeProfileField.text = "${time/24/60}d ${time/60%24}h ${time%60}m"
    }

    private fun setUserFields() {
        binding.usernameProfileField.text = CurrentUser.user.username
        binding.joinDateField.text = CurrentUser.user.joinDate

        if(CurrentUser.user.profilePicture != null) {
            val url = Api.BASE_URL + "pictures/user/" + CurrentUser.user.profilePicture
            Picasso.get().load(url).into(binding.profileImage)
        }
    }

    private fun handleChangeThemeButton(viewGroup: ViewGroup?) {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
        }

        val view = layoutInflater.inflate(R.layout.change_theme_view, viewGroup, false)
        val defaultTheme: Button = view.findViewById(R.id.defaultThemeButton)
        val redTheme: Button = view.findViewById(R.id.redThemeButton)
        val greenTheme: Button = view.findViewById(R.id.greenThemeButton)
        val blueTheme: Button = view.findViewById(R.id.blueThemeButton)

        builder.setView(view)
        val dialog = builder.setTitle("Change theme").create()
        dialog.show()

        defaultTheme.setOnClickListener {
            dialog.dismiss()
            changeTheme(R.style.AnimeTrackerTheme)
        }

        redTheme.setOnClickListener {
            dialog.dismiss()
            changeTheme(R.style.AnimeTrackerRedTheme)
        }

        greenTheme.setOnClickListener {
            dialog.dismiss()
            changeTheme(R.style.AnimeTrackerGreenTheme)
        }
        blueTheme.setOnClickListener {
            dialog.dismiss()
            changeTheme(R.style.AnimeTrackerBlueTheme)
        }
    }

    private fun changeTheme(themeId: Int) {
        val spEditor: SharedPreferences.Editor = sharedPreferences.edit()
        spEditor.putInt("themeId", themeId)
        spEditor.apply()
        requireActivity().recreate()
    }

    private fun handleImageUploadButton() {
        resultLauncher.launch("image/*")
    }

    private fun setupActivityForResult(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.GetContent()) {
            val imageUri: Uri = it
            val bitmap: Bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri))
            binding.profileImage.setImageBitmap(bitmap)
            presenter.changeUserProfilePicture(CurrentUser.user.id, convertBitmapToMultipart(bitmap))
        }
    }

    private fun convertBitmapToMultipart(bitmap: Bitmap): MultipartBody.Part {
        val file = File(requireContext().cacheDir, "profilePicture.png")
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        return MultipartBody.Part.createFormData("picture", file.name, reqFile)
    }

    private fun showCongratulationsNotification() {
        val builder = Notification.Builder(context, "NOTIFICATION_CHANNEL")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground))
            .setContentTitle("Congratulations!")
            .setContentText("You have watched a 100 episodes!")

        val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

    private fun showDialog(viewGroup: ViewGroup?) {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
        }

        val view = layoutInflater.inflate(R.layout.change_password_view, viewGroup, false)
        val currentPassword: EditText = view.findViewById(R.id.currentPasswordField)
        val newPassword: EditText = view.findViewById(R.id.newPasswordField)

        builder.setView(view)

        val dialog = builder.setTitle("Change password")
            .apply {
                setPositiveButton("Submit") { _, _ ->
                }
                setNegativeButton("Cancel", null)
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            var submit = true
            if(currentPassword.text.toString().isBlank()) {
                currentPassword.error = "Current password can't be blank!"
                submit = false
            }
            if(newPassword.text.toString().isBlank()) {
                newPassword.error = "New password can't be blank!"
                submit = false
            } else if(newPassword.text.toString().length < 5) {
                newPassword.error = "New password must be at least 5 characters long!"
                submit = false
            }

            if(submit) {
                presenter.changeUserPassword(Pair(currentPassword.text.toString(), newPassword.text.toString()))
                dialog.dismiss()
            }
        }
    }

    private fun logout() {
        val spEditor: SharedPreferences.Editor = sharedPreferences.edit()
        spEditor.remove("username")
        spEditor.remove("password")
        spEditor.apply()
        requireActivity().finish()
    }

}
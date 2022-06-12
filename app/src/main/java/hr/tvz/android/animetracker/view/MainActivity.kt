package hr.tvz.android.animetracker.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import hr.tvz.android.animetracker.MainContract
import hr.tvz.android.animetracker.R
import hr.tvz.android.animetracker.databinding.ActivityMainBinding
import hr.tvz.android.animetracker.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var presenter: MainContract.Presenter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences("AnimeTrackerSharedPrefs", Context.MODE_PRIVATE)
        val themeId: Int = sp.getInt("themeId", R.style.AnimeTrackerTheme)
        setTheme(themeId)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter(MainPresenter(this))
        presenter.onViewCreated()

        notificationChannelSetup()

        setupFragments()

        if(savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.home
        }
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    private fun setupFragments() {
        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val libraryFragment = LibraryFragment()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.library -> setCurrentFragment(libraryFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(binding.fragmentContainer.id, fragment)
                commit()
            }
    }

    private fun notificationChannelSetup() {
        val channel = NotificationChannel(
            "NOTIFICATION_CHANNEL",
            "notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "notifications"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
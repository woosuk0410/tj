package com.example.android.tj.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.ui.songs.SongAttrEditFragment
import com.example.android.tj.model.TJServiceCommand
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityV2 : AppCompatActivity(), TJServiceUtil {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_playing, R.id.navigation_songs, R.id.navigation_gallery))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun onPriorityShuffle(view: View) {
        sendCmdToTJService(this, TJServiceCommand(Constants.SERVICE_CMD_PRIORITY_SHUFFLE))
    }

    fun onShuffle(view: View) {
        sendCmdToTJService(this, TJServiceCommand(Constants.SERVICE_CMD_SHUFFLE))
    }


    fun onSwitch(view: View) {
        val switch = view as Switch
        if (switch.isChecked) {
            sendCmdToTJService(this, TJServiceCommand(Constants.SERVICE_CMD_PLAY))
        } else {
            sendCmdToTJService(this, TJServiceCommand(Constants.SERVICE_CMD_PAUSE))
        }
    }

    fun onSort(view: View) {
        sendCmdToTJService(this, TJServiceCommand(Constants.SERVICE_CMD_SORT))
    }

    fun onSaveMetadata(view: View) {
        //TODO: fix
        val fragment = supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
        if (fragment is SongAttrEditFragment) {
            fragment.onSave(view)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // this handles top left corner back arrow button for some fragments
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

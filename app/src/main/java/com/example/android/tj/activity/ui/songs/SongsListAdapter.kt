package com.example.android.tj.activity.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.TJServiceClientUtil
import com.example.android.tj.activity.ui.songs.selected.SelectedSongsFragmentDirections
import com.example.android.tj.model.CurrentListMode
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceSongsSyncData
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class SongsListAdapter(
        private val fragment: Fragment,
        private val viewManager: RecyclerView.LayoutManager,
        private val syncData: TJServiceSongsSyncData,
        private val targetListMode: CurrentListMode = CurrentListMode.Normal) :
        RecyclerView.Adapter<SongsListAdapter.SongsListViewHolder>(), TJServiceClientUtil {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class SongsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songNameTextView: TextView = view.findViewById(R.id.song_name)
        val songPriorityView: TextView = view.findViewById(R.id.song_priority)
        val songHistoryCountView: TextView = view.findViewById(R.id.song_history_count)
        val songHLastPlayedAtView: TextView = view.findViewById(R.id.song_history_last_played_at)
        val selectButton: MaterialButton = view.findViewById(R.id.button_add_to_selected_list)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): SongsListViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.individual_song_content, parent, false)
        // set the view's size, margins, paddings and layout parameters
        // ...

        return SongsListViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SongsListViewHolder, position: Int) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that element
        holder.songNameTextView.text = syncData.fileNamesWithIdx()[position]

        val song = syncData.list[position]

        holder.songHistoryCountView.text = "c:${syncData.histories[song.id]?.size ?: 0}"

        val formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.US)
                        .withZone(ZoneId.systemDefault())
        val lastPlayedAt = syncData.histories[song.id]
                ?.maxBy { Instant.parse(it.playedAt).epochSecond }
        holder.songHLastPlayedAtView.text = lastPlayedAt?.let {
            formatter.format(Instant.parse(it.playedAt))
        } ?: ""

        holder.songPriorityView.text = "p:${song.priority}"

        holder.songNameTextView.setOnClickListener {
            run {
                sendCmdToTJService(
                        fragment.activity, TJServiceCommand(
                        Constants.SERVICE_CMD_SWITCH_TARGET_LIST, targetListMode.value))
                playFromHashCmd(fragment.activity, song.id)
                viewManager.scrollToPosition(0)
            }
        }

        holder.songNameTextView.setOnLongClickListener { view ->
            run {
                val action = if (fragment is SongsFragment)
                    SongsFragmentDirections.actionNavigationSongsToSongAttrEditFragment(song.id)
                else
                    SelectedSongsFragmentDirections.actionSelectedSongsToSongAttrEditFragment(
                            song.id)
                view.findNavController().navigate(action)
                true
            }
        }

        holder.selectButton.setOnClickListener {
            run {
                addToSelectedListCmd(fragment.activity, song)
                val snackBar = Snackbar.make(it, "adding ${song.title}...", Snackbar.LENGTH_SHORT)
                snackBar.show()
            }
        }
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = syncData.list.size
}
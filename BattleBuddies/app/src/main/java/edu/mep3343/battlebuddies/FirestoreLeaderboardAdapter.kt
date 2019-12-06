package edu.mep3343.battlebuddies

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.mep3343.battlebuddies.R
import edu.mep3343.battlebuddies.MainViewModel
import edu.mep3343.battlebuddies.LeaderBoardEntry


class FirestoreLeaderboardAdapter(private var viewModel: MainViewModel)
    : ListAdapter<LeaderBoardEntry, FirestoreLeaderboardAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<LeaderBoardEntry>() {
        override fun areItemsTheSame(
            oldItem: LeaderBoardEntry,
            newItem: LeaderBoardEntry
        ): Boolean {
            return oldItem.bbName == newItem.bbName
        }

        override fun areContentsTheSame(
            oldItem: LeaderBoardEntry,
            newItem: LeaderBoardEntry
        ): Boolean {
            return oldItem.player == newItem.player
                    && oldItem.bbName == newItem.bbName
                    && oldItem.wins == newItem.wins
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var userName = itemView.findViewById<TextView>(R.id.LB_username)
        private var theBBName = itemView.findViewById<TextView>(R.id.LB_bbName)
        private var numWins = itemView.findViewById<TextView>(R.id.LB_wins)

        fun bind(item: LeaderBoardEntry?){
            if (item == null)
                return
            userName.text = item.player
            theBBName.text = item.bbName
            numWins.text = (item.wins).toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_leaderboard, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }


}

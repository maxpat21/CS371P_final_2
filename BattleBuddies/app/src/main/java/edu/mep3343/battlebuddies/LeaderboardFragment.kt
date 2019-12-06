package edu.mep3343.battlebuddies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edu.mep3343.battlebuddies.Auth
import edu.mep3343.battlebuddies.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_name.*
import kotlin.random.Random
import edu.mep3343.battlebuddies.BattleBuddy
import edu.mep3343.battlebuddies.NewPlayer.SpawnBBFrag
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*

class LeaderboardFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var leaderboardAdapter: FirestoreLeaderboardAdapter

    companion object {
        fun newInstance(): LeaderboardFragment {
            return LeaderboardFragment()
        }
    }

    private fun initRecyclerView(root: View){
        val theLeaderboardRV = root.findViewById<RecyclerView>(R.id.leaderboardRV)
        leaderboardAdapter = FirestoreLeaderboardAdapter(viewModel)
        theLeaderboardRV.adapter = leaderboardAdapter
        theLeaderboardRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        val root = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        initRecyclerView(root)
        viewModel.observeLeaderboard().observe(this, Observer{
            leaderboardAdapter.submitList(it)
        })
        return root
    }

}
package edu.mep3343.battlebuddies

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class MainViewModel : ViewModel() {
    private lateinit var db: FirebaseFirestore
    private var leaderboard = MutableLiveData<List<LeaderBoardEntry>>()

    fun init(){
        db = FirebaseFirestore.getInstance()
    }

    fun observeLeaderboard() : LiveData<List<LeaderBoardEntry>> {
        return leaderboard
    }

    fun getLeaderboard() {
        db.collection("leaderboard").orderBy("wins").limit(100).addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w("View model", "listen:error", firebaseFirestoreException)
                return@addSnapshotListener
            }
            leaderboard.value = querySnapshot?.documents?.mapNotNull {
                it.toObject(LeaderBoardEntry::class.java)
            }

        }
    }


}
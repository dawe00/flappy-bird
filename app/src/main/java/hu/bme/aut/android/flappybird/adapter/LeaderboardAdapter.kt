package hu.bme.aut.android.flappybird.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.flappybird.data.Record
import hu.bme.aut.android.flappybird.databinding.CardRecordBinding

class LeaderboardAdapter(private val context: Context) : ListAdapter<Record, LeaderboardAdapter.RecordViewHolder>(ItemCallback) {
    private var leaderboard: MutableList<Record> = mutableListOf()
    private var lastPosition = -1

    class RecordViewHolder(binding: CardRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val rank: TextView = binding.rank
        val username: TextView = binding.nickname
        val score: TextView = binding.score
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecordViewHolder(CardRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val tmpPost = leaderboard[position]
        holder.rank.text = tmpPost.rank.toString()
        holder.username.text = tmpPost.player
        holder.score.text = tmpPost.score.toString()

        setAnimation(holder.itemView, position)
    }

    fun addPost(record: Record?) {
        record ?: return

        leaderboard.add(record)
        submitList(leaderboard)
    }

    fun sort(){
        val sorted = leaderboard.sortedBy { Record -> Record.rank }
        sorted.toMutableList()
        val sortedList = sorted as MutableList<Record>
        sortedList.reverse()
        submitList(sortedList)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem == newItem
            }
        }
    }
}
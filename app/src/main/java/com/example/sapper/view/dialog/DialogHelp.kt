package com.example.sapper.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.sapper.R

class DialogHelp : DialogFragment() {

    private lateinit var llButtons: LinearLayout
    private lateinit var llDescription: LinearLayout
    private lateinit var tvDescription: TextView
    private val duration: Long = 600

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_help, null)

        llButtons = view.findViewById(R.id.ll_dialog_help_buttons)
        llDescription = view.findViewById(R.id.ll_dialog_help_description)
        tvDescription = view.findViewById(R.id.tv_dialog_help_description)

        val buttonGameRules = view.findViewById<Button>(R.id.btn_dialog_help_game_rules)
        val buttonGameplay = view.findViewById<Button>(R.id.btn_dialog_help_gameplay)
        val buttonGameMode = view.findViewById<Button>(R.id.btn_dialog_help_game_modes)
        val buttonBluetoothInfo = view.findViewById<Button>(R.id.btn_dialog_help_bluetooth)

        buttonGameRules.setOnClickListener(onMenuButtonClickListener)
        buttonGameplay.setOnClickListener(onMenuButtonClickListener)
        buttonGameMode.setOnClickListener(onMenuButtonClickListener)
        buttonBluetoothInfo.setOnClickListener(onMenuButtonClickListener)

        val buttonSubmit = view.findViewById<Button>(R.id.btn_dialog_help_submit)

        val builder = AlertDialog.Builder(activity).setView(view)


        buttonSubmit.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    private val onMenuButtonClickListener = View.OnClickListener {
        Log.e("TAG", "called onClick: ")
        when (it.id) {
            R.id.btn_dialog_help_game_rules -> {
                tvDescription.setText(R.string.description_game_rules)
                performAnimation(it)
            }
            R.id.btn_dialog_help_gameplay -> {
                tvDescription.setText(R.string.description_game_process)
                performAnimation(it)
            }
            R.id.btn_dialog_help_game_modes -> {
                tvDescription.setText(R.string.description_game_modes)
                performAnimation(it)
            }
            R.id.btn_dialog_help_bluetooth -> {
                tvDescription.setText(R.string.description_game_bluetooth)
                performAnimation(it)
            }
        }
    }

    private fun performAnimation(view: View) {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        llButtons.animate().translationX(550f).setDuration(duration).start()
        view.postDelayed({
            llButtons.visibility = View.GONE
            llDescription.layoutParams = layoutParams
            llDescription.animate().translationX(0f).setDuration(duration).start()
        }, duration)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}
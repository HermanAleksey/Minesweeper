package com.example.sapper.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.sapper.R
import java.lang.Exception

class AdDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_ad_watcher, null)

        val buttonConfirm = view.findViewById<Button>(R.id.button_setting_size_fragment_accept)
        val buttonCancel = view.findViewById<Button>(R.id.button_setting_size_fragment_cancel)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        buttonConfirm.setOnClickListener {

        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }
}
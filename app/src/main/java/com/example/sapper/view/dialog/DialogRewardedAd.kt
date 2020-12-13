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
import com.example.sapper.dialog.DialogSettingsSize
import java.lang.Exception

class DialogRewardedAd: DialogFragment() {

    lateinit var listener: AdDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_ad_watcher, null)

        val buttonConfirm = view.findViewById<Button>(R.id.button_ad_fragment_accept)
        val buttonCancel = view.findViewById<Button>(R.id.button_ad_fragment_cancel)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        buttonConfirm.setOnClickListener {
            listener.sendResponse(true)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            listener.sendResponse(false)
            dismiss()
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AdDialogListener
        } catch (ex: Exception) {
            throw ClassCastException(
                context.toString() +
                        "must implement AdDialogListener"
            );
        }
    }

    interface AdDialogListener {
        fun sendResponse(success: Boolean)
    }
}
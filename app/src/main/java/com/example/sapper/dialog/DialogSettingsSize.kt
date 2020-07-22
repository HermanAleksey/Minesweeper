package com.example.sapper.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.sapper.R
import java.lang.Exception

class DialogSettingsSize : DialogFragment() {

    lateinit var listener: DialogSettingsSizeListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_settings_size, null)

        val editTextFieldHeight = view.findViewById<EditText>(R.id.edittext_settings_dialog_height)
        val editTextFieldWidth = view.findViewById<EditText>(R.id.edittext_settings_dialog_width)
        val buttonConfirm = view.findViewById<Button>(R.id.button_setting_size_fragment_accept)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        buttonConfirm.setOnClickListener {
            listener.sendSizeParams(editTextFieldWidth.text.toString().toInt(),
                editTextFieldHeight.text.toString().toInt())
            dismiss()
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as DialogSettingsSizeListener
        } catch (ex: Exception){
            throw ClassCastException(context.toString() +
                    "must implement DialogSettingsSizeListener");
        }
    }

    interface DialogSettingsSizeListener {
        fun sendSizeParams(width: Int, height: Int)
    }

}
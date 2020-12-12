package com.example.sapper.dialog

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

class DialogSettingsSize : DialogFragment() {

    lateinit var listener: DialogSettingsSizeListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_settings_size, null)

        val editTextFieldHeight = view.findViewById<EditText>(R.id.edittext_settings_dialog_height)
        val editTextFieldWidth = view.findViewById<EditText>(R.id.edittext_settings_dialog_width)
        val buttonConfirm = view.findViewById<Button>(R.id.button_setting_size_fragment_accept)
        val buttonCancel = view.findViewById<Button>(R.id.button_setting_size_fragment_cancel)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        buttonConfirm.setOnClickListener {
            if (isMeetsTheRequirements(editTextFieldWidth, editTextFieldHeight)) {
                listener.sendSizeParams(editTextFieldWidth.text.trim(' ').toString().toInt(),
                    editTextFieldHeight.text.trim(' ').toString().toInt())
                dismiss()
            } else {
                Toast.makeText(activity, activity!!.resources.getString(
                    R.string.parametersDoNotMeetRequirements), Toast.LENGTH_SHORT).show()
            }
        }

        buttonCancel.setOnClickListener {
            listener.sendSizeParams(8,8)
            dismiss()
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as DialogSettingsSizeListener
        } catch (ex: Exception) {
            throw ClassCastException(
                context.toString() +
                        "must implement DialogSettingsSizeListener"
            );
        }
    }

    interface DialogSettingsSizeListener {
        fun sendSizeParams(width: Int, height: Int)
    }

    private fun isMeetsTheRequirements(
        editTextFieldWidth: EditText,
        editTextFieldHeight: EditText
    ): Boolean {
        if (editTextFieldWidth.text.isEmpty() ||
            editTextFieldHeight.text.isEmpty() ||
            editTextFieldWidth.text.toString().trim(' ').toInt() < 3 ||
            editTextFieldHeight.text.toString().trim(' ').toInt() < 3
        ) {
            return false
        }
        return true
    }

}
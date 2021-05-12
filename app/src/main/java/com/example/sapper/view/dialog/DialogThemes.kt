package com.example.sapper.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.sapper.R
import java.lang.Exception

class DialogThemes : DialogFragment() {

    lateinit var listener: DialogThemesListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_themes, null)

        val buttonThemeNone =
            view.findViewById<Button>(R.id.btn_dialog_theme_none)
        val buttonThemeColorized =
            view.findViewById<Button>(R.id.btn_dialog_help_colorized)
        val buttonThemeOld =
            view.findViewById<Button>(R.id.btn_dialog_theme_old)
        val buttonThemeMaterial =
            view.findViewById<Button>(R.id.btn_dialog_theme_material)

        val buttonCancel =
            view.findViewById<Button>(R.id.btn_dialog_theme_cancel)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        buttonThemeNone.setOnClickListener {
            listener.setThemeCallback(0)
            dismiss()
        }
        buttonThemeOld.setOnClickListener {
            listener.setThemeCallback(1)
            dismiss()
        }
        buttonThemeMaterial.setOnClickListener {
            listener.setThemeCallback(2)
            dismiss()
        }
        buttonThemeColorized.setOnClickListener {
            listener.setThemeCallback(3)
            dismiss()
        }
        buttonCancel.setOnClickListener {
            listener.setThemeCallback(-1)
            dismiss()
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as DialogThemesListener
        } catch (ex: Exception) {
            throw ClassCastException(
                context.toString() +
                        "must implement DialogThemesListener"
            );
        }
    }

    interface DialogThemesListener {
        fun setThemeCallback(count: Int)
    }
}
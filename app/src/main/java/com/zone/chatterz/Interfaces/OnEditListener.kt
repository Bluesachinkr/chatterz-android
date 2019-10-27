package com.zone.chatterz.Interfaces

import android.view.View
import android.widget.EditText
import android.widget.TextView

interface OnEditListener : View.OnClickListener {

    fun performEdit(edit: TextView, textView: TextView, editText: EditText, type:String)
    fun editDetails(edit: TextView, textView: TextView, content : String, type: String, editText: EditText)
}
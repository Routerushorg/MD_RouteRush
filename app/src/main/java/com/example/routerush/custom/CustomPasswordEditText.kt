package com.example.routerush.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout


class CustomPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        setup()
    }

    private fun setup() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parentLayout = parent.parent as? TextInputLayout
                s?.let {
                    if (it.length < 8) {
                        parentLayout?.error = "Password harus memiliki minimal 8 karakter"
                        parentLayout?.setErrorIconDrawable(null)
                    } else {
                        parentLayout?.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
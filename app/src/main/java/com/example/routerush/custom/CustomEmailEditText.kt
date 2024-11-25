package com.example.routerush.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        // Tambahkan listener untuk validasi email ketika teks berubah
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmailWithInputLayout()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Fungsi untuk memvalidasi format email
    private fun validateEmailWithInputLayout() {
        // Mendapatkan parent TextInputLayout menggunakan parent.parent
        val parentLayout = parent?.parent as? TextInputLayout

        val email = text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            parentLayout?.error = "Format email tidak valid"
        } else {
            parentLayout?.error = null
        }
    }
}
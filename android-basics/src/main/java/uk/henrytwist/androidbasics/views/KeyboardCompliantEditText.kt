package uk.henrytwist.androidbasics.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

open class KeyboardCompliantEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    override fun onEditorAction(actionCode: Int) {
        super.onEditorAction(actionCode)

        if (actionCode == EditorInfo.IME_ACTION_DONE) {

            clearFocus()
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {

            clearFocus()
        }

        return super.onKeyPreIme(keyCode, event)
    }
}
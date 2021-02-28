package uk.henrytwist.androidbasics.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun SharedPreferences.getTime(key: String, defValue: LocalTime): LocalTime {

    val i = getInt(key, -1)
    return if (i != -1) TimePickerPreference.toLocalTime(i) else defValue
}

abstract class TimePickerPreference(context: Context, attributeSet: AttributeSet) : SelfHandlingDialogPreference(context, attributeSet) {

    open fun getPickerTitle(): CharSequence? {

        return title
    }

    open fun getDefaultTime(): LocalTime? {

        return null
    }

    override fun createDialog(): DialogFragment {

        val builder = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)

        getPickerTitle()?.let { builder.setTitleText(it) }
        getPersistedTime()?.let {

            builder.setHour(it.hour).setMinute(it.minute)
        }

        val dialog = builder.build()

        dialog.addOnPositiveButtonClickListener {

            persistInt(toInt(dialog.hour, dialog.minute))
            notifyChanged()
        }

        return dialog
    }

    private fun getPersistedTime(): LocalTime? {

        val currentValue = getPersistedInt(-1)
        return if (currentValue != -1) {

            toLocalTime(currentValue)
        } else {

            getDefaultTime()
        }
    }

    override fun getSummary(): CharSequence {

        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(getPersistedTime())
    }

    companion object {

        internal fun toLocalTime(i: Int): LocalTime {

            val hour = i / 60
            val minute = i - (hour * 60)

            return LocalTime.of(hour, minute)
        }

        private fun toInt(hour: Int, minute: Int) = hour * 60 + minute
    }
}
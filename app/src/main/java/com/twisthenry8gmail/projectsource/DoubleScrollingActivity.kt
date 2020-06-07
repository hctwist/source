package com.twisthenry8gmail.projectsource

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.double_scrolling.*

class DoubleScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.double_scrolling)

        list.adapter = object : BaseAdapter() {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                return LayoutInflater.from(this@DoubleScrollingActivity)
                    .inflate(R.layout.list_row, parent, false)
            }

            override fun getCount(): Int {

                return 200
            }

            override fun getItem(position: Int): Any {

                return ""
            }

            override fun getItemId(position: Int): Long {

                return 0
            }
        }

        list.addHeaderView(
            LayoutInflater.from(this)
                .inflate(R.layout.list_row_header, list, false)
        )
    }
}
package ui.anwesome.com.kotlinbaruplineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.baruplineview.BarUpLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUpLineView.create(this)
    }
}

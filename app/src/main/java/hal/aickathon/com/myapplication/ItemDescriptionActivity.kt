package hal.aickathon.com.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item_description.*

class ItemDescriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_description)
        val string = intent.getStringExtra("title") + " " + intent.getStringExtra("name")
        tvHeader.text = string
        tvPrice.text = intent.getStringExtra("price")
        ivImage.setImageResource(intent.getIntExtra("image", R.drawable.ic_android_black_24dp))
        btnSocial.setOnClickListener {
            val i = Intent(this@ItemDescriptionActivity, Instafetch::class.java)
            val string2 = string.replace(" ", "_").toLowerCase()
            i.putExtra("name", string2)
            startActivity(i)
        }
    }
}

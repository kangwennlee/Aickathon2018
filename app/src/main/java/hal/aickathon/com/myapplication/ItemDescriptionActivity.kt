package hal.aickathon.com.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item_description.*

class ItemDescriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_description)
        ivImage.setImageResource(intent.getIntExtra("image", R.drawable.ic_android_black_24dp))
        btnSocial.setOnClickListener {
            val i = Intent(this@ItemDescriptionActivity, SocialActivity::class.java)
            startActivity(i)
        }
    }
}

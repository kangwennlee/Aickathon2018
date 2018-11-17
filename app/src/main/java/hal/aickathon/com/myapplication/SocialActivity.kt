package hal.aickathon.com.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import hal.aickathon.com.myapplication.model.Social
import kotlinx.android.synthetic.main.activity_social.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class SocialActivity : AppCompatActivity() {

    val socials = ArrayList<Social>()
    val pics = ArrayList<Int>()
    lateinit var adapter: SocialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)
        rvItems.layoutManager = LinearLayoutManager(this@SocialActivity)
        pics.add(R.drawable.social1)
        pics.add(R.drawable.social2)
        pics.add(R.drawable.social3)
        pics.add(R.drawable.social4)
        pics.add(R.drawable.social5)
        pics.add(R.drawable.social6)
        pics.add(R.drawable.social7)
        for (i in 0 until pics.size) {
            val bitmap = BitmapFactory.decodeResource(resources, pics[i])
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            postPic(baos.toByteArray(), "https://fashion.recoqnitics.com/analyze", i)
        }
        adapter = SocialAdapter(socials, this@SocialActivity, object : SocialAdapter.OnRecyclerListener {
            override fun onClicked(social: Social, position: Int) {

            }

        })
        rvItems.adapter = adapter

    }

    private fun postPic(imgData: ByteArray, url: String, i: Int) {
        val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, url,
            Response.Listener {
                val data = String(it.data)
                val json = JSONObject(data)
                val person = json.getJSONObject("person")
                if (!person.isNull("styles")) {
                    val boundingBox = person.getJSONObject("boundingBox")
                    val style = person.getJSONArray("styles")
                    val color = person.getJSONArray("colors")
                    val garment = person.getJSONArray("garments")
                    socials.add(Social("testUser", pics[i], "$i likes", person.toString()))
                    adapter.notifyDataSetChanged()
                    //Toast.makeText(this@SocialActivity,person.toString(),Toast.LENGTH_LONG).show()
                } else {

                }
                //tvResponse.text = person.toString()

            }, Response.ErrorListener {
                //tvResponse.text = it.toString()
                Toast.makeText(this@SocialActivity, it.toString(), Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["access_key"] = getString(R.string.coqnitive_access_key)
                params["secret_key"] = getString(R.string.coqnitive_secret_key)
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart> {
                val params = HashMap<String, DataPart>()
                params["filename"] = DataPart("file.jpg", imgData, "image/jpeg")
                return params
            }
        }
        VolleySingleton.getInstance(this@SocialActivity).addToRequestQueue(multipartRequest)
    }
}

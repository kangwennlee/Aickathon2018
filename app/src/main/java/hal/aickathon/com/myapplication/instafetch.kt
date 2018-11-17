package hal.aickathon.com.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.util.*


class instafetch : AppCompatActivity() {

    private lateinit var responseString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instafetch)

        imgExtract()

    }

    private fun imgExtract() {
        val textView = findViewById<TextView>(R.id.textView)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        /*val url = String.format(
            "https://api.diffbot.com/v3/image?token=2416f29c393511a294d8cea49f88cc58&url=%1\$s",
            num1
        )*/
        val url =
            "https://api.diffbot.com/v3/image?token=2416f29c393511a294d8cea49f88cc58&url=https://mulpix.com/instagram/adidas_ultraboost.html"

        // Request a string response from the provided URL.
        val myReq = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                textView.text = response
                responseString = response
                filterResponse()
            },
            Response.ErrorListener {
                textView.text = "That didn't work!"
                responseString = ""
            })

        // Add the request to the RequestQueue.
        queue.add(myReq)
    }

    private fun filterResponse() {

        val json = JSONObject(responseString)
        val array = json.getJSONArray("objects")
        val objects = ArrayList<JSONObject>()
        val url = ArrayList<String>()
        val title = ArrayList<String>()

        for (i in 0 until array.length()) {
            objects.add(array.getJSONObject(i))
        }

        for (i in 0 until objects.size) {
            if (!objects[i].isNull("url") && !objects[i].isNull("title")) {
                url.add(objects[i].getString("url"))
                title.add(objects[i].getString("title"))
            }
        }




    }


}

package hal.aickathon.com.myapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import hal.aickathon.com.myapplication.model.DiffBotItem
import kotlinx.android.synthetic.main.activity_instafetch.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class Instafetch : AppCompatActivity() {

    var diffBotArray = ArrayList<DiffBotItem>()
    var diffBotArrayFiltered = ArrayList<DiffBotItem>()
    lateinit var adapter: DiffBotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instafetch)
        val string = intent.getStringExtra("name")
        val url =
            "https://api.diffbot.com/v3/image?token=2416f29c393511a294d8cea49f88cc58&url=https://mulpix.com/instagram/$string.html"
        //CALL METHOD HERE
        imgExtract(url)
        //
        rvItems.layoutManager = LinearLayoutManager(this@Instafetch)
        adapter = DiffBotAdapter(diffBotArray, this@Instafetch, object : DiffBotAdapter.OnRecyclerListener {
            override fun onClicked(diffBotItem: DiffBotItem, position: Int) {

            }
        })
        rvItems.adapter = adapter
        btnFilter.setOnClickListener {
            for (i in 0 until diffBotArray.size) {
                postPic(diffBotArray[i])
            }
            adapter = DiffBotAdapter(diffBotArrayFiltered, this@Instafetch, object : DiffBotAdapter.OnRecyclerListener {
                override fun onClicked(diffBotItem: DiffBotItem, position: Int) {

                }

            })
            rvItems.adapter = adapter
        }
    }


    private fun postPic(item: DiffBotItem) {
        doAsync {
            val bitmap = Picasso.get().load(item.itemURL).get()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArray = baos.toByteArray()
            uiThread {
                val multipartRequest =
                    object : VolleyMultipartRequest(Request.Method.POST, "https://fashion.recoqnitics.com/analyze",
                        Response.Listener {
                            val data = String(it.data)
                            val json = JSONObject(data)
                            val person = json.getJSONObject("person")
                            if (!person.isNull("styles")) {
                                val boundingBox = person.getJSONObject("boundingBox")
                                val style = person.getJSONArray("styles")
                                val color = person.getJSONArray("colors")
                                val garment = person.getJSONArray("garments")
                                diffBotArrayFiltered.add(item)
                                adapter.notifyDataSetChanged()
                                //Toast.makeText(this@SocialActivity,person.toString(),Toast.LENGTH_LONG).show()
                            } else {

                            }
                            //tvResponse.text = person.toString()

                        }, Response.ErrorListener {
                            //tvResponse.text = it.toString()
                            //Toast.makeText(this@Instafetch, it.toString(), Toast.LENGTH_LONG).show()
                        }) {
                        override fun getParams(): MutableMap<String, String> {
                            val params = HashMap<String, String>()
                            params["access_key"] = getString(R.string.coqnitive_access_key)
                            params["secret_key"] = getString(R.string.coqnitive_secret_key)
                            return params
                        }

                        override fun getByteData(): MutableMap<String, DataPart> {
                            val params = HashMap<String, DataPart>()
                            params["filename"] = DataPart("file.jpg", byteArray, "image/jpeg")
                            return params
                        }
                    }
                VolleySingleton.getInstance(this@Instafetch).addToRequestQueue(multipartRequest)
            }
        }

    }


    private fun imgExtract(url: String) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this@Instafetch)

        // Request a string response from the provided URL.
        val myReq = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                val json = JSONObject(response)
                val array = json.getJSONArray("objects")
                val objects = ArrayList<JSONObject>()
                for (i in 0 until array.length()) {
                    objects.add(array.getJSONObject(i))
                }
                for (i in 0 until objects.size) {
                    if (!objects[i].isNull("url") && !objects[i].isNull("title")) {
                        diffBotArray.add(DiffBotItem(objects[i].getString("url"), objects[i].getString("title")))
                    }
                }
                adapter.notifyDataSetChanged()
                //for (i in 0 until diffBotArray.size) {
                //  postPic(url,diffBotArray[i])
                //}
            },
            Response.ErrorListener {

            })

        // Add the request to the RequestQueue.
        queue.add(myReq)
    }


}

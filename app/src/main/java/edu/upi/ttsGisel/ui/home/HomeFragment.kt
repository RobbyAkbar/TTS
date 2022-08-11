package edu.upi.ttsGisel.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import edu.upi.ttsGisel.R
import edu.upi.ttsGisel.SettingsActivity
import edu.upi.ttsGisel.lesson.LessonActivity
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.AppUtils.Companion.getIdUser
import edu.upi.ttsGisel.utils.AppUtils.Companion.getName
import edu.upi.ttsGisel.utils.Config

class HomeFragment : Fragment(), ListMateriAdapter.RecyclerViewItemClickListener {

    private lateinit var appCompatActivity: AppCompatActivity
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar

    private lateinit var rvBeginner: RecyclerView
    private lateinit var rvIntermediate: RecyclerView
    private lateinit var rvAdvanced: RecyclerView
    private lateinit var rvExpert: RecyclerView

    private lateinit var rvBeginnerAdapter: ListMateriAdapter
    private lateinit var rvIntermediateAdapter: ListMateriAdapter
    private lateinit var rvAdvancedAdapter: ListMateriAdapter
    private lateinit var rvExpertAdapter: ListMateriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (AppUtils.getLesson(requireContext()) != "") setRecyclerView(AppUtils.getLesson(requireContext())!!)
        if (AppUtils.isNetworkStatusAvialable(requireContext())) getData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rvBeginnerAdapter = ListMateriAdapter(this, requireContext())
        rvIntermediateAdapter = ListMateriAdapter(this, requireContext())
        rvAdvancedAdapter = ListMateriAdapter(this, requireContext())
        rvExpertAdapter = ListMateriAdapter(this, requireContext())

        val root: View = inflater.inflate(R.layout.fragment_home, container, false)

        initViews(root)

        initObjects()

        return root
    }

    override fun onResume() {
        super.onResume()
        if (AppUtils.isNetworkStatusAvialable(requireContext())) getData()
    }

    private fun initViews(root: View) {
        toolbar = root.findViewById(R.id.toolbar)
        appCompatActivity = activity as AppCompatActivity
        progressBar = root.findViewById(R.id.progressBar)

        rvBeginner = root.findViewById(R.id.rv_beginner)
        rvIntermediate = root.findViewById(R.id.rv_intermediate)
        rvAdvanced = root.findViewById(R.id.rv_advanced)
        rvExpert = root.findViewById(R.id.rv_expert)
    }

    private fun initObjects(){
        toolbar.title = getString(R.string.hello) + " " + getName(requireContext())
        appCompatActivity.setSupportActionBar(toolbar)

        rvBeginner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvBeginner.adapter = rvBeginnerAdapter

        rvIntermediate.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvIntermediate.adapter = rvIntermediateAdapter

        rvAdvanced.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvAdvanced.adapter = rvAdvancedAdapter

        rvExpert.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvExpert.adapter = rvExpertAdapter
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.POST, Config.LIST_MATERI_URL,
                Response.Listener { response ->
                    setRecyclerView(response)
                    val sharedPreferences = requireContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Config.LESSON_SHARED_PREF, response)
                    editor.apply()
                    editor.commit()
                    progressBar.visibility = View.INVISIBLE
                },
                Response.ErrorListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_USER_SHARED_PREF] = getIdUser(requireContext()).toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setRecyclerView(response: String){
        val beginnerList: List<MateriModel> = MateriJSONParser.parseData(response, Config.BEGINNER)
        rvBeginnerAdapter.updateWith(beginnerList)

        val intermediateList: List<MateriModel> = MateriJSONParser.parseData(response, Config.INTERMEDIATE)
        rvIntermediateAdapter.updateWith(intermediateList)

        val advancedList: List<MateriModel> = MateriJSONParser.parseData(response, Config.ADVANCED)
        rvAdvancedAdapter.updateWith(advancedList)

        val expertList: List<MateriModel> = MateriJSONParser.parseData(response, Config.EXPERT)
        rvExpertAdapter.updateWith(expertList)
    }

    override fun clickOnItem(data: MateriModel) {
        val lesson = Intent(activity, LessonActivity::class.java)
        lesson.putExtra(Config.EXTRA_LESSON, data)
        startActivity(lesson)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_account, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package edu.upi.ttsGisel.ui.leaderboard

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
import edu.upi.ttsGisel.utils.AppUtils
import edu.upi.ttsGisel.utils.AppUtils.Companion.getIdUser
import edu.upi.ttsGisel.utils.Config
import java.util.*

class LeaderBoardFragment : Fragment() {

    private lateinit var rvLeads: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var toolbar: Toolbar
    private lateinit var appCompatActivity: AppCompatActivity

    private lateinit var rvLeadAdapter: ListLeadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (AppUtils.isNetworkStatusAvialable(requireContext())) getData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rvLeadAdapter = ListLeadAdapter()
        val root: View = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        initViews(root)

        initObjects()

        return root
    }

    private fun initViews(root: View) {
        toolbar = root.findViewById(R.id.tab_toolbar)
        appCompatActivity = activity as AppCompatActivity

        progressBar = root.findViewById(R.id.progressBar)
        rvLeads = root.findViewById(R.id.rv_leaderboard)
    }

    private fun initObjects(){
        toolbar.title = getString(R.string.top_rank)
        appCompatActivity.setSupportActionBar(toolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvLeads.setHasFixedSize(true)
        rvLeads.layoutManager = LinearLayoutManager(activity)
        rvLeads.adapter = rvLeadAdapter
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.POST, Config.LIST_LEADER_BOARD_URL,
                Response.Listener { response ->
                    setRecyclerView(response)
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
        val leadList: List<LeadModel> = LeadJSONParser.parseData(response)
        rvLeadAdapter.updateWith(leadList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_account, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> appCompatActivity.onBackPressed()
            R.id.account -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
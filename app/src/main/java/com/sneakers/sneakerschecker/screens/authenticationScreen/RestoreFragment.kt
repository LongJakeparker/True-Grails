package com.sneakers.sneakerschecker.screens.authenticationScreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.sneakers.sneakerschecker.MainActivity
import com.sneakers.sneakerschecker.R
import com.sneakers.sneakerschecker.api.AuthenticationApi
import com.sneakers.sneakerschecker.constant.Constant
import com.sneakers.sneakerschecker.model.CheckPrivateKeyResultModel
import com.sneakers.sneakerschecker.model.RetrofitClientInstance
import com.sneakers.sneakerschecker.model.SharedPref
import com.sneakers.sneakerschecker.model.SignIn
import kotlinx.android.synthetic.main.fragment_restore.*
import org.web3j.crypto.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RestoreFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters

    private var fragmentView: View? = null
    private lateinit var credentials: Credentials

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var service: Retrofit

    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_restore, container, false)

        sharedPref = context?.let { SharedPref(it) }!!

        //Get instant retrofit
        service = RetrofitClientInstance().getRetrofitInstance()!!

        builder = AlertDialog.Builder(context)
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnScanPrivateKey.setOnClickListener(this)
        btnNextRestore.setOnClickListener(this)
        btnCheckPrivateKey.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScanPrivateKey -> goToScan()

            R.id.btnNextRestore -> {
                if (etUserNameRestore.text.toString().isNotEmpty() &&
                    etPasswordRestore.text.toString().isNotEmpty()
                ) {
                    RequestLogIn()
                }
            }

            R.id.btnCheckPrivateKey -> checkPrivateKey()
        }
    }

    private fun checkPrivateKey() {
        dialog.show()
        credentials = Credentials.create(etPrivateKey.text.toString().trim())
        var data = HashMap<String, String>()
        data["registrationToken"] = sharedPref.getString(Constant.FCM_TOKEN)
        val call = service.create(AuthenticationApi::class.java)
            .restoration(
                credentials.address,
                data
            )
        call.enqueue(object : Callback<CheckPrivateKeyResultModel> {

            override fun onResponse(
                call: Call<CheckPrivateKeyResultModel>,
                response: Response<CheckPrivateKeyResultModel>
            ) {
                dialog.dismiss()
                if (response.code() == 200) {
                    etUserNameRestore.setText(response.body()!!.email)
                    cartEmail.visibility = VISIBLE
                    cartPassword.visibility = VISIBLE
                    btnNextRestore.visibility = VISIBLE

                } else {

                    Toast.makeText(context, "onResponse - Status : " + response.errorBody()!!.string(), Toast.LENGTH_LONG).show()
                    Log.d("TAG", "onResponse - Status : " + response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<CheckPrivateKeyResultModel>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(context, "Something went wrong when login", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun RequestLogIn() {
        dialog.show()
        val authToken = okhttp3.Credentials.basic(Constant.AUTH_TOKEN_USERNAME, Constant.AUTH_TOKEN_PASSWORD)
        val call = service.create(AuthenticationApi::class.java)
            .signInApi(
                authToken,
                Constant.GRANT_TYPE_PASSWORD,
                etUserNameRestore.text.toString().trim(),
                etPasswordRestore.text.toString().trim()
            )
        call.enqueue(object : Callback<SignIn> {

            override fun onResponse(call: Call<SignIn>, response: Response<SignIn>) {
                if (response.code() == 200) {
                    sharedPref.setUser(response.body()!!, Constant.WALLET_USER)
                    importPrivateKey()

                } else if (response.code() == 400) {
                    dialog.dismiss()
                    Log.d("TAG", "onResponse - Status : " + response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<SignIn>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(context, "Something went wrong when login", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun importPrivateKey() {
        sharedPref.setCredentials(credentials, Constant.USER_CREDENTIALS)

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }

    private fun goToScan() {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                etPrivateKey.setText(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

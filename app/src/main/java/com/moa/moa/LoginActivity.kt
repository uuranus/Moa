package com.moa.moa

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.oauth.view.NidOAuthLoginButton
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    private val naverLogin: NidOAuthLoginButton by lazy {
        findViewById(R.id.naverLogin)
    }

    private val googleLogin:SignInButton by lazy{
        findViewById(R.id.googleLogin)
    }

    private val OAUTH_CLIENT_ID:String by lazy {
        resources.getString(R.string.naver_client_id)
    }
    private val OAUTH_CLIENT_SECRET:String by lazy {
        resources.getString(R.string.naver_client_secret)
    }
    private val OAUTH_CLIENT_NAME:String by lazy {
        resources.getString(R.string.naver_client_name)
    }

    private lateinit var gso:GoogleSignInOptions
    private lateinit var mGoogleSignInClient:GoogleSignInClient

    private val RC_SIGN_IN=100


    private val logoutButton: Button by lazy{
        findViewById(R.id.logoutButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init(){

        NaverIdLoginSDK.initialize(this,OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME)
        naverLogin.setOAuthLoginCallback(oauthLoginCallback)

        gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        googleLogin.setSize(SignInButton.SIZE_STANDARD)
        googleLogin.setOnClickListener {
            val signInIntent=mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }

        logoutButton.setOnClickListener {
            NaverIdLoginSDK.logout()
            mGoogleSignInClient.signOut()
        }
    }

    private val oauthLoginCallback=object: OAuthLoginCallback {
        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode,message)

        }

        override fun onFailure(httpStatus: Int, message: String) {
            Toast.makeText(this@LoginActivity,"로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
        }

        override fun onSuccess() {
            Toast.makeText(this@LoginActivity,"로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

            NidOAuthLogin().callProfileApi(object:NidProfileCallback<NidProfileResponse>{
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode,message)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    Toast.makeText(this@LoginActivity,"프로필 정보를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()


                }

                override fun onSuccess(result: NidProfileResponse) {
                    val intent=Intent(this@LoginActivity,RegisterActivity::class.java)
                    intent.putExtra("userEmail",result.profile?.email)
                    startActivity(intent)

                }

            })

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RC_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask:Task<GoogleSignInAccount> ){
        try{
            val account=completedTask.getResult(ApiException::class.java)

            //로그인 성공
            //회원가입 화면으로 이동

            val intent=Intent(this@LoginActivity,HomeActivity::class.java)
            intent.putExtra("userEmail",account.email)
            startActivity(intent)
        }
        catch(e:ApiException){
            Log.i("exception",e.toString())
            Toast.makeText(this,"로그인에 실패했습니다",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("start","onstart")
        //구글 로그인이 되어있는지 확인
        val account=GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            val sharedPreferences=getSharedPreferences("Register", Context.MODE_PRIVATE)
            if(sharedPreferences.getBoolean("isRegister",false)){
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }


        }

        //네이버 로그인이 되어있는지 확인
        if(NidOAuthLoginState.OK.equals(NaverIdLoginSDK.getState())){
            val sharedPreferences=getSharedPreferences("Register", Context.MODE_PRIVATE)
            if(sharedPreferences.getBoolean("isRegister",false)){
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }


        }
    }

}
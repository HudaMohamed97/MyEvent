package com.example.myapplication.LoginFragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.login_fragment.*


class LoginFragment : Fragment(), LoginInterface {
    private lateinit var root: View
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var email: TextInputLayout
    private lateinit var passwordEt: TextInputLayout
    private var dialog: ProgressDialog? = null
    private lateinit var loginPreferences: SharedPreferences
    private lateinit var loginPrefsEditor: SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.login_fragment, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        //listenToLoading()
    }

    private fun listenToLoading() {
        loginViewModel.isLoading.observe(this, Observer {
            if (it) {
                Log.i("hhhh", "showw")
                showLoader()
            } else {
                Log.i("hhhh", "hideee")
                hideLoader()
            }
        })
    }

    private fun hideLoader() {
        dialog?.dismiss()

    }

    private fun showLoader() {
        dialog = ProgressDialog(activity)
        dialog?.setMessage("Please, Wait")
        dialog?.setCancelable(false)
        dialog?.show()
    }


    override fun setClickListeners() {
        val mainLayout = root.findViewById(R.id.mainLayout) as View
        val button = root.findViewById(R.id.btn_login) as Button
        email = root.findViewById(R.id.email) as TextInputLayout
        passwordEt = root.findViewById(R.id.password) as TextInputLayout

        mainLayout.setOnClickListener {
            hideKeyboard()
        }

        loginPreferences = activity!!.getSharedPreferences("loginPrefs", MODE_PRIVATE)
        loginPrefsEditor = loginPreferences.edit()
        val saveLogin = loginPreferences.getBoolean("saveLogin", false)

        if (saveLogin) {
            emailText.setText(loginPreferences.getString("username", ""))
            passText.setText(loginPreferences.getString("password", ""))
            chckRemember.isChecked = true
        }




        button.setOnClickListener {
            /*checkErrorEnabled()
            hideKeyboard()
            if (loginViewModel.validateLoginInfo(
                    email.editText?.text.toString(),
                    passwordEt.editText?.text.toString()
                )
            ) {
                email.isErrorEnabled = false
                passwordEt.isErrorEnabled = false
                callLoginRequest()
            }*/
            findNavController().navigate(R.id.action_LoginFragment_to_ScannerFragment)
            saveUserData()
        }
        register_login.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_Home)
        }
    }

    private fun callLoginRequest() {
        showLoader()
        loginViewModel.login(
            email.editText?.text.toString(),
            passwordEt.editText?.text.toString()
        )
        loginViewModel.getData().observe(this, Observer {
            hideLoader()


        })

    }

    private fun checkErrorEnabled() {
        if (!loginViewModel.validate(email.editText?.text.toString())) {
            email.isErrorEnabled = true
            email.error = "empty email please fill it"
        } else {
            email.isErrorEnabled = false
        }
        if (!loginViewModel.validate(passwordEt.editText?.text.toString())) {
            passwordEt.isErrorEnabled = true
            passwordEt.error = "empty password please fill it"
        } else {
            passwordEt.isErrorEnabled = false
        }
    }


    private fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm =
                context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun saveUserData() {
        if (chckRemember.isChecked) {
            loginPrefsEditor.putBoolean("saveLogin", true)
            loginPrefsEditor.putString("username", "test")
            loginPrefsEditor.putString("password", "12345")
            loginPrefsEditor.commit()
        } else {
            loginPrefsEditor.clear()
            loginPrefsEditor.commit()
        }
    }

}
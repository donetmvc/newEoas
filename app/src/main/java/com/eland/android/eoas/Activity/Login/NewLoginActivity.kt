package com.eland.android.eoas.Activity.Login

import android.app.Dialog
import android.os.Bundle
import butterknife.OnClick
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.ProgressUtil
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */

class NewLoginActivity: DaggerAppCompatActivity(), LoginView{

    private lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: NewLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.loadLogin()

        dialog = ProgressUtil.loading(this@NewLoginActivity)
    }

    @OnClick(R.id.btn_login)
    fun login() {


    }


    override fun showLoading() {
        if(dialog.isShowing) dialog.hide() else dialog.show()
    }

    override fun onLoginViewLoad() {

    }
}
package com.sneakers.sneakerschecker.screens.fragment.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sneakers.sneakerschecker.R
import com.sneakers.sneakerschecker.constant.Constant
import com.sneakers.sneakerschecker.utils.CommonUtils


class InputPasswordDialogFragment : DialogFragment() {
    private lateinit var etInputCode: EditText
    private lateinit var listIvPassCode: Array<ImageView>
    private var dialogTitle: String? = ""
    private var dialogMessage: String? = ""
    private var mListener: PasscodeResultInterface? = null

    companion object {
        fun show(fragment: Fragment, fragmentManager: FragmentManager) {
            show(
                fragment,
                fragmentManager,
                "",
                ""
            )
        }

        fun show(
            fragment: Fragment,
            fragmentManager: FragmentManager,
            title: String,
            message: String
        ) {
            val dialog =
                InputPasswordDialogFragment()
            val bundle = Bundle()
            bundle.putString(Constant.EXTRA_DIALOG_TITLE, title)
            bundle.putString(Constant.EXTRA_DIALOG_MESSAGE, message)
            dialog.arguments = bundle
            dialog.setTargetFragment(fragment, Constant.DIALOG_REQUEST_CODE)
            dialog.show(fragmentManager, InputPasswordDialogFragment::class.java.simpleName)
        }

        fun show(
            fragmentManager: FragmentManager,
            title: String,
            message: String,
            callback: PasscodeResultInterface
        ) {
            val dialog =
                InputPasswordDialogFragment()
            val bundle = Bundle()
            bundle.putString(Constant.EXTRA_DIALOG_TITLE, title)
            bundle.putString(Constant.EXTRA_DIALOG_MESSAGE, message)
            dialog.arguments = bundle
            dialog.mListener = callback
            dialog.show(fragmentManager, InputPasswordDialogFragment::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_input_password, null, false)

        dialogTitle = arguments?.getString(Constant.EXTRA_DIALOG_TITLE, "")
        dialogMessage = arguments?.getString(Constant.EXTRA_DIALOG_MESSAGE, "")

        if (dialogTitle!!.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tvTitle).text = dialogTitle
        }

        if (dialogMessage!!.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tvMessage).text = dialogMessage
        }

        etInputCode = view.findViewById(R.id.etInputCode)

        listIvPassCode = arrayOf(
            view.findViewById(R.id.tvNumber1),
            view.findViewById(R.id.tvNumber2),
            view.findViewById(R.id.tvNumber3),
            view.findViewById(R.id.tvNumber4),
            view.findViewById(R.id.tvNumber5),
            view.findViewById(R.id.tvNumber6)
        )
//
        etInputCode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    listIvPassCode[s.length - 1].setImageResource(R.drawable.ic_passcode)
                    if (s.length < 6) {
                        listIvPassCode[s.length].setImageResource(R.drawable.ic_passcode_empty)
                    } else {
                        Handler().postDelayed({
                            if (targetFragment != null) {
                                returnPasscode()
                            } else {
                                mListener?.onReceivePasscode(etInputCode.text.toString().trim())
                                dismiss()
                            }
                        }, 200)
                    }
                } else {
                    listIvPassCode[0].setImageResource(R.drawable.ic_passcode_empty)
                }
            }
        })

        etInputCode.requestFocus()

        view.findViewById<ImageView>(R.id.btnClose).setOnClickListener { dismiss() }

        builder.setView(view)

        val dialog: Dialog = builder.create()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.window.setBackgroundDrawableResource(R.drawable.drawable_bg_dialog_intput_passcode)
        return dialog
    }

    private fun returnPasscode() {
        val bundle = Bundle()
        bundle.putString(Constant.EXTRA_USER_PASSWORD, etInputCode.text.toString().trim())

        val intent = Intent().putExtras(bundle)

        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        dismiss()
    }

    interface PasscodeResultInterface {
        fun onReceivePasscode(passcode: String)
    }
}
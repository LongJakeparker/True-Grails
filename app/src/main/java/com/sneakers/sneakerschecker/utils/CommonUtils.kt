package com.sneakers.sneakerschecker.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sneakers.sneakerschecker.R
import com.sneakers.sneakerschecker.constant.Constant
import com.sneakers.sneakerschecker.model.SharedPref
import com.sneakers.sneakerschecker.model.SignIn
import java.text.SimpleDateFormat
import java.util.*


class CommonUtils {
    companion object {

        fun toggleLoading(loadingParent: View?, show: Boolean) {
            if (loadingParent == null) {
                return
            }
            val v = loadingParent.findViewById<View>(R.id.pb_loading)
            if (v != null)
                v.visibility = if (show) View.VISIBLE else View.GONE
        }

        fun toggleLoadingFullBg(loadingParent: View?, show: Boolean) {
            if (loadingParent == null) {
                return
            }
            val v = loadingParent.findViewById<View>(R.id.pb_loading_full)
            if (v != null)
                v.visibility = if (show) View.VISIBLE else View.GONE
        }

        fun copyToClipboard(activity: Activity?, text: String) {
            val clipboard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", text)
            clipboard.primaryClip = clip
            Toast.makeText(activity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        fun generateQrCode(activity: Activity, widthPer: Double, itemToken: String): Bitmap? {
            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width = size.x

            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(
                    itemToken, BarcodeFormat.QR_CODE,
                    (width * widthPer).toInt(),
                    (width * widthPer).toInt()
                )
                val barcodeEncoder = BarcodeEncoder()
                return barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * function hide/close soft keyboard
         *
         * @param activity
         */
        fun hideKeyboard(activity: Activity?) {
            if (activity == null) {
                return
            }
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            view.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun setTextBold(textView: TextView?) {
            if (textView == null) {
                return
            }

            textView.setTypeface(null, Typeface.BOLD)
        }

        fun isNonLoginUser(context: Context): Boolean {
            val sharedPref = SharedPref(context)

            if (sharedPref.getUser(Constant.LOGIN_USER) != null) {
                return false
            }
            return true
        }

        fun generateEOSAccountName(): String {
            var result = ""
            val length = 12
            val characters = "12345abcdefghijklmnopqrstuvwxyz"
            for (i in 1..length) {
                val randomIndex = Math.floor(Math.random() * length).toInt()
                result += characters[randomIndex]
            }
            return result
        }

        fun getCurrentUser(context: Context): SignIn? {
            val sharedPref = SharedPref(context)

            if (sharedPref.getUser(Constant.LOGIN_USER) != null) {
                return sharedPref.getUser(Constant.LOGIN_USER)
            }

            return null
        }

        fun getBrainTreeToken(context: Context): String {
            val sharedPref = SharedPref(context)
            return sharedPref.getString(Constant.BRAINTREE_TOKEN)
        }

        fun formatDate(date: Date): String {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            return format.format(date)
        }

        fun formatStringToDate(string: String): Date {
            val format =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            return format.parse(string)
        }

        fun formatDateToString(pattern: String, date: Date): String {
            val format = SimpleDateFormat(pattern, Locale.US)
            return format.format(date)
        }

        fun getBrandLogo(brand: String): Int {
            return when (brand) {
                "adidas" -> R.drawable.logo_adidas

                "converse" -> R.drawable.logo_converse

                "vans" -> R.drawable.logo_vans

                "nike" -> R.drawable.logo_nike

                "jordan" -> R.drawable.logo_jordan

                "puma" -> R.drawable.logo_puma

                "balenciaga" -> R.drawable.logo_balenciaga

                "gucci" -> R.drawable.logo_gucci

                else -> R.drawable.logo_adidas
            }
        }
    }
}
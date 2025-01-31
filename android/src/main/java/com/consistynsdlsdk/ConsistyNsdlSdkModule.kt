package com.consistynsdlsdk

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.nsdl.assistedsdk.view.activity.NsdlAssistedActivity

class ConsistyNsdlSdkModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ActivityEventListener {

  private var transactionPromise: Promise? = null
  private val REQUEST_CODE = 1


  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "ConsistyNsdlSdk"
  }

  init {
    reactContext.addActivityEventListener(this) //Register this native module as Activity result listener
  }

  override fun onNewIntent(p0: Intent?) {

  }

  override fun onActivityResult(p0: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE) {
      val promise = transactionPromise ?: return
      transactionPromise = null

      if (resultCode == Activity.RESULT_OK && data != null) {
        val status = data.getStringExtra("status")
        if (status.equals("SUCCESS", ignoreCase = true)) {

          val custName = data.getStringExtra("custName")
          val custId = data.getStringExtra("custId")
          val custAccNo = data.getStringExtra("custAccNo")

          val result = Arguments.createMap().apply {
            putString("statusCode", "00")
            putString("message", "Account Created: $custName ($custId)")
            putString("customerId", custId)
            putString("customerAccountNo", custAccNo)
            putString("customerName", custName)
          }
          promise.resolve(result)
        } else {
          promise.reject("01", "Account creation was cancelled or failed")
        }

      }
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    fun initiateAccountOpening(
      bcAgentId: String,
      agentMobileNo: String,
      channelId: String,
      agentPanNo: String,
      bcId: String,
      appId: String,
      partnerId: String,
      orderID: String,
      extraOne: String,
      promise: Promise
    ) {
      transactionPromise = promise
      // promise.resolve(a * b)
      val currentActivity = currentActivity ?: run {
        promise.reject("NO_ACTIVITY", "No activity available")
        return
      }

      Log.i("initiateAccountOpening BcAgentID : ", bcAgentId.toString())
      try {

        val sdkInstance = SDKClass.initialize(
          context = currentActivity,
          agentMobileNo = agentMobileNo,
          agentPanNo = agentPanNo,
          bcId = bcId,
          bcAgentId = bcAgentId,
          transactionId = orderID,
          channelId = channelId,
          appId = appId,
          partnerId = partnerId,
          extraOne = extraOne
        )

        // Prepare Intent with required extras to send to the SDK Activity
        val intent = Intent(currentActivity, NsdlAssistedActivity::class.java).apply {
          putExtra("AgentMobileNo", agentMobileNo)
          putExtra("AgentPanNo", agentPanNo)
          putExtra("BcId", bcId)
          putExtra("BcAgentId", bcAgentId)
          putExtra("TransactionId", orderID)
          putExtra("ChannelId", channelId)
          putExtra("AppId", appId)
          putExtra("PartnerId", partnerId)
          putExtra("ExtraOne", extraOne)
        }

        // Use the ActivityResultLauncher to launch the activity
        currentActivity.startActivityForResult(intent, REQUEST_CODE)

        // Optional: Handle SDK response
        sdkInstance.setSDKCallback(object : SDKCallback {
          override fun onSuccess(result: SDKResult) {
            // Handle successful SDK call
            Toast.makeText(currentActivity, "SDK Success", Toast.LENGTH_SHORT).show()
            Log.d("SDKIntegration", "SDK Success: $result")
          }

          override fun onError(error: SDKError) {
            // Handle SDK error
            Toast.makeText(currentActivity, "SDK Error: ${error.message}", Toast.LENGTH_SHORT)
              .show()
            Log.e("SDKIntegration", "SDK Error: ${error.message}")
          }
        })

      } catch (e: Exception) {
        // Handle any initialization errors
        Log.e("SDKIntegration", "SDK Initialization Failed", e)
        Toast.makeText(currentActivity, "SDK Initialization Failed", Toast.LENGTH_SHORT).show()
      }
    }


  }
}

interface SDKCallback {
  fun onSuccess(result: SDKResult)
  fun onError(error: SDKError)
}

data class SDKResult(val data: Map<String, Any>)
data class SDKError(val code: Int, val message: String)

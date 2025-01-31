package com.consistynsdlsdk

import android.content.Context

class SDKClass private constructor(
  val context: Context,
  val agentMobileNo: String,
  val agentPanNo: String,
  val bcId: String,
  val bcAgentId: String,
  val transactionId: String,
  val channelId: String,
  val appId: String,
  val partnerId: String,
  val extraOne: String
) {
  companion object {
    private var instance: SDKClass? = null

    fun initialize(
      context: Context,
      agentMobileNo: String,
      agentPanNo: String,
      bcId: String,
      bcAgentId: String,
      transactionId: String,
      channelId: String,
      appId: String,
      partnerId: String,
      extraOne: String
    ): SDKClass {
      if (instance == null) {
        instance = SDKClass(
          context,
          agentMobileNo,
          agentPanNo,
          bcId,
          bcAgentId,
          transactionId,
          channelId,
          appId,
          partnerId,
          extraOne
        )
      }
      return instance!!
    }
  }

  fun setSDKCallback(callback: SDKCallback) {
    // Simulate SDK process and invoke callbacks
    // In a real SDK, replace this with actual logic
    callback.onSuccess(SDKResult(data = mapOf("status" to "success")))
  }
}

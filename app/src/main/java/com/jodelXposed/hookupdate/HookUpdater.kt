package com.jodelXposed.hookupdate

import android.app.AndroidAppHelper
import android.widget.Toast
import com.jodelXposed.hooks.helper.Activity
import com.jodelXposed.hooks.helper.Log
import com.jodelXposed.models.HookValues
import com.jodelXposed.utils.Options
import es.dmoral.prefs.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Admin on 21.01.2017.
 */
class HookUpdater {
    fun updateHooks(oldHooks: HookValues, installedVersionCode: Int, api: JodelXposedAPI = RetrofitProvider.JXAPI) {
        getHooks(oldHooks, installedVersionCode, api)
    }

    fun getBetaHooks(oldHooks: HookValues, installedVersionCode: Int, api: JodelXposedAPI = RetrofitProvider.JXAPI) {
        api.getDevHooks(installedVersionCode).enqueue(object : Callback<HookValues> {
            override fun onResponse(call: Call<HookValues>, response: Response<HookValues>) {
                try {
                    val repoHooks = response.body()
                    if (repoHooks.versionCode > oldHooks.versionCode || (repoHooks.versionCode == oldHooks.versionCode && repoHooks.version > oldHooks.version)) {
                        Log.dlog("Replacing local hooks with repo hooks")
                        Options.hooks = repoHooks
                        Options.save()
                        Prefs.with(Activity.getMain()).writeBoolean("displayJXchangelog", true)
                        Toast.makeText(AndroidAppHelper.currentApplication(), "Updated hooks, please force restart Jodel", Toast.LENGTH_LONG).show()
                    } else {
                        Log.dlog("++++++++++++ Repo hooks are of the same or older version. Not updating. ++++++++++++")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<HookValues>, t: Throwable) {
                Log.xlog("Failed fetching new hooks", t)
                Toast.makeText(AndroidAppHelper.currentApplication(), "Failed updating hooks, " + t.message + " !", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getHooks(oldHooks: HookValues, installedVersionCode: Int, api: JodelXposedAPI = RetrofitProvider.JXAPI) {
        api.getHooks(installedVersionCode).enqueue(object : Callback<HookValues> {
            override fun onResponse(call: Call<HookValues>, response: Response<HookValues>) {
                try {
                    val repoHooks = response.body()
                    if (repoHooks.versionCode > oldHooks.versionCode || (repoHooks.versionCode == oldHooks.versionCode && repoHooks.version > oldHooks.version)) {
                        Log.dlog("Replacing local hooks with repo hooks")
                        Options.hooks = repoHooks
                        Options.save()
                        Prefs.with(Activity.getMain()).writeBoolean("displayJXchangelog", true)
                        Toast.makeText(Activity.getMain(), "Updated hooks, please force restart Jodel", Toast.LENGTH_LONG).show()

                    } else {
                        Log.dlog("Repo hooks are of the same or older version. Not updating.")
                    }
                } catch (e: Exception) {
                    Log.xlog("Your Jodel version is not supported by JodelXposed yet")
                    Toast.makeText(AndroidAppHelper.currentApplication(), "Your Jodel version isnt supported by JodelXposed yet.", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<HookValues>, t: Throwable) {
                Log.xlog("Failed fetching new hooks", t)
                Toast.makeText(AndroidAppHelper.currentApplication(), "Failed updating hooks, " + t.message + " !", Toast.LENGTH_LONG).show()
            }
        })
    }
}
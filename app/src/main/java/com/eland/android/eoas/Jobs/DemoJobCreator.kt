package com.eland.android.eoas.Jobs

import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import com.eland.android.eoas.Receiver.AutoReceiver
import com.evernote.android.job.*
import java.util.concurrent.TimeUnit
import com.evernote.android.job.util.JobLogger
import com.orhanobut.logger.Logger
import timber.log.Timber

/**
 * Created by liuwenbin on 2018/1/4.
 * 虽然青春不在，但不能自我放逐.
 */
class DemoJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        return when (tag) {
            DemoSyncJob.TAG -> DemoSyncJob()
            MyDailyJob.TAG -> MyDailyJob()
            else -> null
        }
    }

}

class DemoSyncJob : Job() {
    @NonNull
    override fun onRunJob(params: Job.Params): Job.Result {
        // start my service
        val intent = Intent(context, AutoReceiver::class.java)
        intent.action = "REG_AUTO"
        context.sendBroadcast(intent)
        Timber.i("The job have started!")
        return Job.Result.SUCCESS
    }

    companion object {
        val TAG = "job_demo_tag"
        fun scheduleJob(): Int {
            return  JobRequest.Builder(DemoSyncJob.TAG)
                    .setExecutionWindow(30_000L, 40_000L)
                    .build()
                    .schedule()
        }
    }
}

class MyDailyJob : DailyJob() {

    override fun onRunDailyJob(params: Params): DailyJobResult {

        val intent = Intent(context, AutoReceiver::class.java)
        intent.action = "REG_AUTO"
        context.sendBroadcast(intent)
        Logger.i("The job have started!!!")
        return DailyJobResult.SUCCESS
    }

    override fun onReschedule(newJobId: Int) {
        super.onReschedule(newJobId)
    }

    companion object {
        val TAG = "MyDailyJob"


        fun schedule(): Int {
            val builder = JobRequest.Builder(TAG)
            return DailyJob.schedule(builder, TimeUnit.HOURS.toMillis(7), TimeUnit.HOURS.toMillis(8))
        }
    }
}

class MyLogger : JobLogger {
    override fun log(priority: Int, tag: String, message: String, @Nullable t: Throwable?) {
        // log
        Logger.log(priority, tag, message, t)
    }
}
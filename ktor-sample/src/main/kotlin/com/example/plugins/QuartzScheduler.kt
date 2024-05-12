package com.example.plugins

import com.example.quartz.jobs.DataFetchJob
import io.ktor.server.application.*
import org.quartz.JobBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory


fun Application.configureQuartzScheduler() {
   val scheduler = StdSchedulerFactory.getDefaultScheduler()
   scheduler.start()

   val jobDetail = JobBuilder.newJob(DataFetchJob::class.java)
      .withIdentity("dataFetchJob", "group1")
      .build()

   val trigger = TriggerBuilder.newTrigger()
      .withIdentity("dataFetchTrigger", "group1")
      .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(60))
      .build()

   scheduler.scheduleJob(jobDetail, trigger)
}
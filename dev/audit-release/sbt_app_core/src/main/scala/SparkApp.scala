/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main.scala

import scala.util.Try

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "input.txt"
    val sc = new SparkContext("local", "Simple App")
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    if (numAs != 2 || numBs != 2) {
      println("Failed to parse log files with Spark")
      System.exit(-1)
    }

    // Regression test for SPARK-1167: Remove metrics-ganglia from default build due to LGPL issue
    val foundConsole = Try(Class.forName("org.apache.spark.metrics.sink.ConsoleSink")).isSuccess
    val foundGanglia = Try(Class.forName("org.apache.spark.metrics.sink.GangliaSink")).isSuccess
    if (!foundConsole) {
      println("Console sink not loaded via spark-core")
      System.exit(-1)
    }
    if (foundGanglia) {
      println("Ganglia sink was loaded via spark-core")
      System.exit(-1)
    }
  }
}

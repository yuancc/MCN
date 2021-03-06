/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.performance.test.mcn.utils;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.net.TrafficStats;
import android.util.Log;

/**
 * information of network traffic
 * 
 */
public class TrafficInfo {

	private static final String LOG_TAG = "MCN-" + TrafficInfo.class.getSimpleName();
	private static final int UNSUPPORTED = -1;

	private String uid;

	private long upStream;
	private long downStream;
	public TrafficInfo(String uid) {
		this.uid = uid;
	}

	/**
	 * get total network traffic, which is the sum of upload and download
	 * traffic.
	 * 
	 * @return total traffic include received and send traffic
	 */
	public long getTrafficInfo() {
		Log.i(LOG_TAG, "get traffic information");
		Log.d(LOG_TAG, "uid = " + uid);
		long traffic = trafficFromApi();
		return traffic <= 0 ? trafficFromFiles() : traffic;
	}
	public long getUpStream() {
		return upStream;
	}

	public long getDownStream() {
		return downStream;
	}
	/**
	 * Use TrafficStats getUidRxBytes and getUidTxBytes to get network
	 * traffic,these API return both tcp and udp usage
	 * 
	 * @return
	 */
	private long trafficFromApi() {
		long rcvTraffic = UNSUPPORTED, sndTraffic = UNSUPPORTED;
		rcvTraffic = TrafficStats.getUidRxBytes(Integer.parseInt(uid));
		downStream = rcvTraffic;
		sndTraffic = TrafficStats.getUidTxBytes(Integer.parseInt(uid));
		upStream = sndTraffic;
		return rcvTraffic + sndTraffic < 0 ? UNSUPPORTED : rcvTraffic + sndTraffic;
	}

	/**
	 * read files in uid_stat to get traffic info
	 * 
	 * @return
	 */
	private long trafficFromFiles() {
		RandomAccessFile rafRcv = null, rafSnd = null;
		long rcvTraffic = UNSUPPORTED, sndTraffic = UNSUPPORTED;
		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
		try {
			rafRcv = new RandomAccessFile(rcvPath, "r");
			rafSnd = new RandomAccessFile(sndPath, "r");
			rcvTraffic = Long.parseLong(rafRcv.readLine());
			downStream = rcvTraffic;
			sndTraffic = Long.parseLong(rafSnd.readLine());
			upStream = sndTraffic;
			Log.d(LOG_TAG, String.format("rcvTraffic, sndTraffic = %s, %s", rcvTraffic, sndTraffic));
		} catch (Exception e) {
		} 
		finally {
			try {
				if (rafRcv != null) {
					rafRcv.close();
				}
				if (rafSnd != null)
					rafSnd.close();
			} catch (IOException e) {}
		}
		return rcvTraffic + sndTraffic < 0 ? UNSUPPORTED : rcvTraffic + sndTraffic;
	}

}

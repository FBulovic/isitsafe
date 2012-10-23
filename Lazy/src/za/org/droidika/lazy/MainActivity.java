package za.org.droidika.lazy;

/* Test is adaptation of org.apache.http.examples.client.ClientMultiThreadedExecution
 * so the same license is applicable:
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	DefaultHttpClient singleton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton = SafeLazy.getInstance();
        System.out.println("we will now assign singleton");
        if(singleton!=SafeLazy.getInstance())
        	System.out.println("looks different!");
        else
        	System.out.println("it is the same!");
        ClientConnectionManager manager = singleton.getConnectionManager();
        
        String[] urisToGet = {
                "http://hc.apache.org/",
                "http://hc.apache.org/httpcomponents-core-ga/",
                "http://grumpyoldprogrammer.blogspot.com/2009/07/pascals-triangle-perl-and-homework.html",
                "http://grumpyoldprogrammer.blogspot.com/2012/06/who-you-follow-but-he-or-she-does-not.html"
            };
        try{
        	// create a thread for each URI
            GetThread[] threads = new GetThread[urisToGet.length];
            for (int i = 0; i < threads.length; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                threads[i] = new GetThread(httpget, i + 1);
            }

            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }

        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
			manager.shutdown();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    static class GetThread extends Thread {

        private final HttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private final int id;

        public GetThread(HttpGet httpget, int id) {
            this.httpClient = SafeLazy.getInstance();
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.id = id;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run() {
        	System.out.println("HttpClient hashCode is "+httpClient.hashCode());
            System.out.println(id + " - about to get something from " + httpget.getURI());

            try {

                // execute the method
                HttpResponse response = httpClient.execute(httpget, context);

                System.out.println(id + " - get executed");
                // get the response body as an array of bytes
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    System.out.println(id + " - " + bytes.length + " bytes read");
                }

            } catch (Exception e) {
                httpget.abort();
                System.out.println(id + " - error: " + e);
            }
        }

    }
}

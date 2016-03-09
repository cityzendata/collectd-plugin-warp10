//
//   Copyright 2016  Cityzen Data
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package org.collectd.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

import org.collectd.api.Collectd;
import org.collectd.api.ValueList;
import org.collectd.api.DataSource;
import org.collectd.api.CollectdConfigInterface;
import org.collectd.api.CollectdFlushInterface;
import org.collectd.api.CollectdInitInterface;
import org.collectd.api.CollectdWriteInterface;
import org.collectd.api.OConfigValue;
import org.collectd.api.OConfigItem;


public class WriteWarp10 implements CollectdWriteInterface,
        CollectdInitInterface,
        CollectdConfigInterface,
        CollectdFlushInterface
{
    private String      url = "http://localhost:4242";
    private String      token   = "token";
    private String      className   = "prefix";
    private StringBuffer sbuffer = new StringBuffer();
    private Number bufferSize = 100;
    private int counter = 0;
    //buffer

    public WriteWarp10 ()
    {
        Collectd.registerInit   ("WriteWarp10", this);
        Collectd.registerWrite  ("WriteWarp10", this);
        Collectd.registerConfig ("WriteWarp10", this);
    	Collectd.registerFlush  ("WriteWarp10", this);
    }

    public int init ()
    {
	    Collectd.logInfo ("Url : " + url + ", TOKEN: " + token);
        /*
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
        */

        return(0);
    }
    
    public int flush (Number timeout, String identifier)
    {
    	try {
			sendPost(sbuffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(0);
    }
    
    public int write (ValueList vl)
    {
        List<DataSource> ds = vl.getDataSet().getDataSources();
        List<Number> values = vl.getValues();
        int size            = values.size();
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<size; i++) {
            // Buffer
            sb.setLength(0);

            // Metric name
            String    name, pointName,
                    plugin, pluginInstance,
                    type, typeInstance;
            ArrayList<String> parts = new ArrayList<String>();
            ArrayList<String> tags = new ArrayList<String>();

            plugin         = vl.getPlugin();
            pluginInstance = vl.getPluginInstance();
            type           = vl.getType();
            typeInstance   = vl.getTypeInstance();

            // Collectd.logInfo("plugin: " + plugin + " pluginInstance: " + pluginInstance + " type: " + type + " typeInstance: " + typeInstance);

            // FIXME: refactor to switch?
            if ( plugin != null && !plugin.isEmpty() ) {
                parts.add(plugin);
                if ( pluginInstance != null && !pluginInstance.isEmpty() ) {
                    tags.add(plugin + "_instance=" + pluginInstance);
                }
                if ( type != null && !type.isEmpty()) {
                    tags.add(plugin + "_type=" + type);
                }
                if ( typeInstance != null && !typeInstance.isEmpty() ) {
                    tags.add(plugin + "_type_instance=" + typeInstance);
                }

                pointName = ds.get(i).getName();
                if (!pointName.equals("value")) {
                    // Collectd.logInfo("pointName: " + pointName);
                    tags.add(plugin + "_point=" + pointName);
                }
            }

            name = join(parts, ".");

            // Time
            long time = vl.getTime() * 1000;
            sb.append(time).append("// ");
            
            // ClassName
            sb.append("collectd");
            if(className != null)
            {
            	sb.append('.').append(className);
            }
            if(name != null)
            {
            	sb.append('.').append(name);
            }
            
            sb.append('{');

            // Host
            String host = vl.getHost();
            sb.append("host=").append(host).append(",");

            // Meta
            sb.append("source=collectd");
            if(tags!=null)
            {
            	sb.append(',');
            }
            sb.append(join(tags, ","));
            
            sb.append('}').append(' ');

            // Value
            Number val = values.get(i);
            sb.append(val).append("\n");
            
            sbuffer.append(sb.toString());
            counter++;
            if( counter >= bufferSize.intValue())
            {
            	try {
					sendPost(sbuffer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // System.out.println(output);
           // _out.println(output);
        }

        return(0);
    }

    public static String join(Collection<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    public int config (OConfigItem ci) /* {{{ */
    {
        List<OConfigItem> children;
        int i;

        //Collectd.logDebug ("Warp plugin: config: ci = " + ci + ";");

        children = ci.getChildren ();
        for (i = 0; i < children.size (); i++)
        {
            List<OConfigValue> values;
            OConfigItem child;
            String key;

            child = children.get (i);
            key   = child.getKey ();
            if (key.equalsIgnoreCase ("Server"))
            {
                values = child.getValues();
                if (values.size () != 4)
                {
                    Collectd.logError ("Open warp plugin: " + key +
                            "configuration option needs exactly two arguments: url + token");
                    return (1);
                } else {
                    url = values.get(0).toString();
                    token   = values.get(1).toString();
                    className   = values.get(2).toString();
                    bufferSize   = values.get(3).getNumber();
                }
            }
            else
            {
                Collectd.logError ("Open warp plugin: Unknown config option: " + key);
            }
        } /* for (i = 0; i < children.size (); i++) */

        return (0);
        
    } /* }}} int config */
    
 // HTTP POST request
 	private void sendPost(StringBuffer s) throws Exception {

 		String url = this.url;
 		URL obj = new URL(url);
        HttpURLConnection con = null;
 		if(url.startsWith("https")) {
            con = (HttpsURLConnection) obj.openConnection();
        } else {
            con = (HttpURLConnection) obj.openConnection();
        }
       
 		//add request header
 		con.setDoOutput(true);
 		con.setDoInput(true);
 		con.setRequestMethod("POST");
 		con.setRequestProperty("Host", this.className);
 		con.setRequestProperty("X-Warp10-Token", token);
 		con.setRequestProperty("Content-Type", "application/gzip");
 		con.setChunkedStreamingMode(16384);
 		//OutputStream os = con.getOutputStream();
        //os.write(s.getBytes());
        //os.flush();
        //os.close();
 		con.connect();
 		
	    OutputStream os = con.getOutputStream();
	    GZIPOutputStream out = new GZIPOutputStream(os);
 		PrintWriter pw = new PrintWriter(out);  
 		StringBuilder sb = new StringBuilder(s);
 		pw.println(sb.toString());
 		pw.close();

 		
 		if (200 != con.getResponseCode()) {
 			Collectd.logError("Code " + con.getResponseCode());
 			Collectd.logError(con.getResponseMessage());
 		} else
 		{
 			sbuffer = new StringBuffer();
 			counter = 0;
 		}
 		     
 		int responseCode = con.getResponseCode();
 		Collectd.logInfo("\nSending 'POST' request to URL : " + url);
 		//Collectd.logInfo(" : " + s);
 		Collectd.logInfo("Response Code : " + responseCode);

 		BufferedReader in = new BufferedReader(
 		        new InputStreamReader(con.getInputStream()));
 		String inputLine;
 		StringBuffer response = new StringBuffer();

 		while ((inputLine = in.readLine()) != null) {
 			response.append(inputLine);
 		}
 		in.close();
		con.disconnect();
 		//print result
 		// System.out.println(response.toString());
 	}
}

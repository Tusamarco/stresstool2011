package net.tc.utils.elastic;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.*;
import org.json.simple.JSONObject;

import net.tc.*;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.file.FileDataWriter;
import net.tc.utils.file.FileHandler;

public class elasticProvider {
	
public static Node getClientNode(){
	
	
	Node node = NodeBuilder.nodeBuilder().local(true).node();
//	Node client = node.client();
	
	return node;
	
 }	

 public static Client getClientTransporter(Map conf){
	
	 String[] URL = ((String)conf.get("URL")).split(":");
	 
	 Settings settings = ImmutableSettings.settingsBuilder()
			 				.put("cluster.name", "ps_elasticsearch").build();
	 Client client =    new TransportClient(settings)
	 					.addTransportAddress(new InetSocketTransportAddress(URL[0], Integer.parseInt(URL[1])));

	 return client;
	 
	 
 }

 public static IndexRequestBuilder fillClientFromMap(IndexRequestBuilder request,Map data){
	 
	    JSONObject source = new JSONObject();
	    Iterator it =  data.keySet().iterator();
	    String key;
	    String value;
	    while (it.hasNext()){
	    	key = (String)it.next();
	    	value = (String)String.valueOf(data.get(key)).toString();
	    	source.put(key, value);
	    	
	    }
	    
	    
	    
	 	request.setSource(source.toJSONString());
	 
	 return request;
 }

}

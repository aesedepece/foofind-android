package foofind.kadede.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.*;
import java.io.*;

//XML parsing libraries
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class search extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			String query = extras.getString("query");
			TextView search_results_query = (TextView) findViewById(R.id.search_results_query);
			search_results_query.setText(" \"" + query.toString() + "\"");
			String result = getSearch(query);
			showResults(result);
		}
	}

	private void showResults(String result) {
		try {
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(new InputSource(new StringReader(result)));
			  doc.getDocumentElement().normalize();
			  //Log.d(TAG, "Root element " + doc.getDocumentElement().getNodeName());
			  Node mainNode = doc.getElementsByTagName("getSearch").item(0);
			  //Log.d(TAG, "Main element " + mainNode.getNodeName());
			  if(mainNode.hasChildNodes()){
				  LinearLayout wrapper = (LinearLayout) findViewById(R.id.results_list);
				  LinearLayout inflatedView;
				  NodeList keyLst = mainNode.getChildNodes();
				  int amount = keyLst.getLength()-1;
				  TextView no_results = (TextView) findViewById(R.id.no_results);
				  if(doc.getElementsByTagName("response").getLength()>0){
					  amount--;
					  no_results.setText(R.string.no_results);
					  no_results.setPadding(10, 10, 10, 10);
				  }else{
					  no_results.setText("");
					  no_results.setPadding(0, -20, 0, 0);
					  TextView search_results_query = (TextView) findViewById(R.id.search_results_amount);
					  search_results_query.setText(" (" + amount + ")");
					  for(int i=0; i<amount; i++){
						  Node keyNode = keyLst.item(i);
						  if(keyNode.getNodeType() == Node.ELEMENT_NODE){
							  Element keyElmnt = (Element) keyNode;
							  NodeList keyElmntLst;
							  	keyElmntLst= keyElmnt.getElementsByTagName("size");
							  	long r_size = 0;
							  	if(keyElmntLst.item(0).hasChildNodes()){
							  		r_size = Long.parseLong(keyElmntLst.item(0).getChildNodes().item(0).getNodeValue());
							  	}
							  	keyElmntLst = keyElmnt.getElementsByTagName("type");
							  	String r_type = keyElmntLst.item(0).getChildNodes().item(0).getNodeValue();
							  	keyElmntLst = keyElmnt.getElementsByTagName("dlink");
							  	String r_dlink = keyElmntLst.item(0).getChildNodes().item(0).getNodeValue();
							  inflatedView = (LinearLayout) View.inflate(this, R.layout.singleresult, null);
							  inflatedView.setOnClickListener(this);
							  ((TextView) inflatedView.findViewById(R.id.sr_ffdp)).setText(dlinkFix(r_dlink));
							  ((TextView) inflatedView.findViewById(R.id.sr_filename)).setText(nameFix(r_dlink));
							  if(r_size == 0){
								  ((TextView) inflatedView.findViewById(R.id.sr_size)).setPadding(0, -20, 0, 0);
							  }else{
								  ((TextView) inflatedView.findViewById(R.id.sr_size)).setText(sizeFix(r_size));
							  }
							  if(keyElmnt.getElementsByTagName("md").getLength() > 0){
								  Node mdNode = keyElmnt.getElementsByTagName("md").item(0);
								  ((TextView) inflatedView.findViewById(R.id.sr_description)).setText(descript(r_type, mdNode));
							  }else{
								  ((TextView) inflatedView.findViewById(R.id.sr_description)).setPadding(0, -20, 0, 0);
							  }
							  wrapper.addView(inflatedView);
							  //Log.d(TAG, dlinkFix(r_dlink));
						  }
					  }
				  }
			  }
		} catch (Exception e) {
		    Log.d(TAG, "XML ERROR:" + e.toString() );
		}
	}

	private String descript(String rType, Node mdNode) {
		String description = "";
		if(((Element) mdNode).getElementsByTagName("artist").getLength() > 0){
			description += "Artist: " + ((Element) mdNode).getElementsByTagName("artist").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(((Element) mdNode).getElementsByTagName("title").getLength() > 0){
			description += "Title: " + ((Element) mdNode).getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(((Element) mdNode).getElementsByTagName("album").getLength() > 0){
			description += "Album: " + ((Element) mdNode).getElementsByTagName("album").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(((Element) mdNode).getElementsByTagName("genre").getLength() > 0){
			description += "Genre: " + ((Element) mdNode).getElementsByTagName("genre").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(((Element) mdNode).getElementsByTagName("length").getLength() > 0){
			description += "Length: " + ((Element) mdNode).getElementsByTagName("length").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(((Element) mdNode).getElementsByTagName("quality").getLength() > 0){
			description += "Quality: " + ((Element) mdNode).getElementsByTagName("quality").item(0).getChildNodes().item(0).getNodeValue() + ". ";
		}
		if(description.equals(""))description = "No description";
		//Log.d(TAG, rType + " " + description);
		return description;
	}

	private String sizeFix(long rSize) {
		double fixed = Math.floor(rSize*100/1024/1024)/100;
		return fixed + "MB";
	}

	private String nameFix(String dlink) {
		String[] segments = dlink.split("/");
		String fixed = segments[segments.length-1];
		return fixed.replace(".html]]>", "");
	}
	
	private String dlinkFix(String dlink) {
		String fixed = dlink.replace("<![CDATA[", "");
		return fixed.replace("]]>", "");
	}

	@Override
	public void onClick(final View v) {
		String ffdp = ((TextView) v.findViewById(R.id.sr_ffdp)).getText().toString();
		Log.d(TAG, ffdp);
		String rd = find_rd(ffdp);
		//v.setBackgroundColor(0xFF666666);
		final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
		final Intent i = new Intent("android.intent.action.VIEW", Uri.parse(rd));
        new Thread() {
            public void run() {
                 try{
             		startActivity(i);
                 } catch (Exception e) {  }
                 //v.setBackgroundColor(0xFF333333);
                 pd.dismiss();
            }
       }.start(); 
	}
	
	private String find_rd(String ffdp) {
		String dphtml = download(ffdp);
		String dpsplit[] = dphtml.split("awesome\" href=\"");
		String rd[] = dpsplit[1].split(">");
		Log.d(TAG, rd[0]);
		return rd[0];
	}

	private static final String TAG = "foofind";

	private String getSearch(String query){	
		SharedPreferences fooSettings = PreferenceManager.getDefaultSharedPreferences(this);
		String type = fooSettings.getString("filetype", "all");
		String results = fooSettings.getString("max_results", "20");
		String src = "";
		if(fooSettings.getBoolean("all", true)){
			src = "wftge";
		}else{
			if(fooSettings.getBoolean("dd", true)){
				src += "wf";
			}
			if(fooSettings.getBoolean("torrents", true)){
				src += "t";
			}
			if(fooSettings.getBoolean("gnutella", true)){
				src += "g";
			}
			if(fooSettings.getBoolean("ed2k", true)){
				src += "e";
			}
		}
		
		Log.d(TAG, "Searching for " + query + " in " + type + " from " + src);
		String uri = uriFix("http://foofind.com/api/?method=getSearch&q=" + query + "&lang=en&src=" + src + "&opt=&type=" + type + "&size=&year=&brate=&results=" + results );
		Log.d(TAG, uri);
		String result = download(uri);
		//Log.d(TAG, result);
		return result;
	}
		
	private static String uriFix(String uri) {
		uri = uri.replace(' ', '_');
		return uri;
	}

	public static String download(String uri){
	       String result = "", nextLine;
	       URL url = null;
	       URLConnection urlConn = null;
	       InputStreamReader  inStream = null;
	       BufferedReader buff = null;
	       try{
	          url  = new URL(uri);
	          urlConn = url.openConnection();
	          inStream = new InputStreamReader(urlConn.getInputStream());
	          buff = new BufferedReader(inStream);
		      while (true){
		            nextLine = buff.readLine();  
		            if (nextLine != null){
		                result += nextLine.toString(); 
		            }
		            else{
		               break;
		            } 
		        }
	       } catch(MalformedURLException e){
	    	   Log.d(TAG, "Please check the URL:" + 
	                                           e.toString() );
	     } catch(IOException  e1){
	    	 Log.d(TAG, "Can't read  from the Internet: "+ 
	                                          e1.toString() ); 
	  }
		return result;
	 }
	
}

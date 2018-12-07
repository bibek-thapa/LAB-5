package edu.gvsu.restapi.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;



public class ChatClient {
	
	static ServerSocket serverSocket =null;
	ServerThread serverThread;
	static RegistrationInfo info;
//	public static final String APPLICATION_URI = "http://localhost:8080";
	public static final String APPLICATION_URI = "http://lab5-224521.appspot.com/";
	static RegistrationInfo info1;
	public ChatClient() {
				
			try {
				serverSocket = new ServerSocket(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 System.out.println("Error: Couldn't allocate local socket endpoint.");
		            System.exit(-1);
				e.printStackTrace();
			}
			
			Thread t1 = new Thread(new ServerThread());
			t1.start();
			
			
	
	}

	public  void sendMessage(String name,String msg) 
	{
		
		
	
		try {
			String widgetsResourceURL = APPLICATION_URI + "/users/"+name;
			Request request = new Request(Method.GET,widgetsResourceURL);
    	    
    	    request.getClientInfo().getAcceptedMediaTypes().
            add(new Preference(MediaType.APPLICATION_JSON));   
    	    
    	     Response resp = new Client(Protocol.HTTP).handle(request);
    	     
    	     System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
    			resp = new Client(Protocol.HTTP).handle(request);

    			// Let's see what we got!
    			System.out.println("Inside the sendMessage ");
    			System.out.println(resp.getStatus());
    			if(resp.getStatus().equals(Status.SUCCESS_OK)) {
    			Representation responseData = resp.getEntity();
    				System.out.println("Status = " + resp.getStatus());
    				try {
    					String jsonString = responseData.getText().toString();
    					System.out.println("result text=" + jsonString);
    					JSONObject jObj = new JSONObject(jsonString);   					
    					if(jObj!=null && jObj.getBoolean("status")!=false) {
    		                // open a socket connection remote user's client and send message.
    		                Socket skt = new Socket(jObj.getString("host"),jObj.getInt("port"));
    		                String completeMsg = "Message from " + info.getUserName() + ": " + msg + "\n";
    		                skt.getOutputStream().write(completeMsg.getBytes());
    		                skt.close();
    					}
    					
    					else 
    					{
    						System.out.println("The client is not active. Try messaging later");
    					}
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (JSONException je) {
    					je.printStackTrace();
    				}
    			}
    	     
    	     
    	     
  
              
            } catch (Exception e) {
            	
            	e.printStackTrace();
                
                
            }
     
		
		
		
	}
	
	
	class ServerThread implements Runnable
	{
		
		boolean done = false;
		
		
		public void run() {
			// TODO Auto-generated method stub
		while(!done) {
			Socket client_socket = null;			
			try {
				client_socket = serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Inside the thread");
				e.printStackTrace();
			}
			
			 byte buf[] = new byte[2048];
             try {
                 int cnt = client_socket.getInputStream().read(buf,0,2048);
                 String msg = new String(buf,0,cnt);

                 // We'll refresh the prompt, lest the chimp on the console
                 // get's confused.
                 System.out.println(msg);
                 client_socket.close();

             } catch (IOException ie) {
            	 
            	 System.out.println("Inside the thread 1");
             }
		}
             try {
				ChatClient.serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 System.out.println("Inside the thread 2");
				e.printStackTrace();
			}
		
			
		}
		
		
		
		
		
	}
	
     
		
		
		
	
	
	
	
	public static void main(String[] args) {

		ChatClient myClient = new ChatClient();
	
		try {

			Vector<RegistrationInfo> clients;
			Scanner s = new Scanner(System.in);
			System.out.println("Enter Your name and press Enter:");
			String name_1 = s.nextLine().trim().toLowerCase();

		
			 int port = serverSocket.getLocalPort();
			 String host = "localhost";
			 info = new RegistrationInfo(name_1, host, port, true);
			 
			 Form form = new Form();
			    form.add("userInput",name_1);
			    form.add("host",host);
			    form.add("port",Integer.toString(port));
			    // construct request to create a new user resource
			    String clientsResourceURL = APPLICATION_URI + "/users";
			    Request request = new Request(Method.POST,clientsResourceURL);	   
			    request.setEntity(form.getWebRepresentation());		
			    Response resp = new Client(Protocol.HTTP).handle(request);			
			    Representation responseData = resp.getEntity();
			    try {
					System.out.println(responseData.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			 
			 
			
			 
			
			String input;						
			System.out.println("Please enter one of the following commands:");
			System.out.println("friends");
			System.out.println("chat {username} {message}");
			System.out.println("broadcast {message}");
			System.out.println("busy");
			System.out.println("available");
			System.out.println("clear");
			System.out.println("exit");
			System.out.println("------------------------------------");
			
			boolean active =true;
			while(active){
				Scanner s_1=new Scanner(System.in);
				
				String name_2=s_1.nextLine();
				String[] nameArray = name_2.trim().split(" "); 
				
				
				String a = nameArray[0].toLowerCase();
				switch(a) 
				{
				case "friends":
					String widgetsResourceURL = APPLICATION_URI + "/users";
					request = new Request(Method.GET,widgetsResourceURL);
            	    
            	    request.getClientInfo().getAcceptedMediaTypes().
                    add(new Preference(MediaType.APPLICATION_JSON));           	 
            	     resp = new Client(Protocol.HTTP).handle(request);
            		
            		if(resp.getStatus().equals(Status.SUCCESS_OK)) {
            			 responseData = resp.getEntity();
            			System.out.println("Status = " + resp.getStatus());
            			try {
            				String jsonString= responseData.getText().toString();            			
            				JSONArray jsonArray = new JSONArray(jsonString);
            				for (int i = 0; i < jsonArray.length(); i++) {
            					JSONObject jObj = jsonArray.getJSONObject(i);
            					System.out.println("name=" + jObj.getString("userName") + " status=" + jObj.getBoolean("status"));
            				}
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (JSONException jsonex) {
            				jsonex.printStackTrace();
            			}
            		}


				
					
					
					break;
				case "broadcast":
					
					String msg1="";
					for(int i=1 ; i<nameArray.length;i++) 
					{
						msg1 = msg1 +" "+nameArray[i];
					}
					
					widgetsResourceURL = APPLICATION_URI + "/users";
					request = new Request(Method.GET,widgetsResourceURL);
            	    
            	    request.getClientInfo().getAcceptedMediaTypes().
                    add(new Preference(MediaType.APPLICATION_JSON));           	 
            	     resp = new Client(Protocol.HTTP).handle(request);
            		
            		if(resp.getStatus().equals(Status.SUCCESS_OK)) {
            			 responseData = resp.getEntity();
            			 System.out.println("Inside the broadcast switch case");
            			System.out.println("Status = " + resp.getStatus());
            			try {
            				String jsonString= responseData.getText().toString();            			
            				JSONArray jsonArray = new JSONArray(jsonString);
            				for (int i = 0; i < jsonArray.length(); i++) {
            					JSONObject jObj = jsonArray.getJSONObject(i);
//            					System.out.println("name=" + jObj.getString("userName") + " status=" + jObj.getBoolean("status"));
            					
            					String name_broad = jObj.getString("userName");
            					
            					if(!myClient.info.getUserName().equals(name_broad)) {
            					myClient.sendMessage(jObj.getString("userName"), msg1);
            					
//    							
            					}
            				
            				
            				}
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (JSONException jsonex) {
            				jsonex.printStackTrace();
            			}
            		}

													
					
					
				break;
					
				case "chat":
					
					String msg="";
					for(int i=2 ; i<nameArray.length;i++) 
					{
						msg = msg +" "+nameArray[i];
					}
					
					if(!info.getUserName().equals(nameArray[1])) {
					myClient.sendMessage(nameArray[1], msg);
					}
					else 
					{
						System.out.println("You cannot chat with yourself");
					}
				
					
					break;
					
				case "busy":
					 Form form_a = new Form();
              	    	form_a.add("userInput","busy");
              	    	
              	    	widgetsResourceURL = APPLICATION_URI + "/users/" +info.getUserName();
              	    	Request request_1 = new Request(Method.PUT,widgetsResourceURL);
         
              	    	request_1.setEntity(form.getWebRepresentation());
              	    
              	    	Response resp_1 = new Client(Protocol.HTTP).handle(request_1);
              	    	Representation responseData_1 = resp_1.getEntity();
              	    	 try {
                  			System.out.println(responseData_1.getText());
                  		} catch (IOException e) {
                  			// TODO Auto-generated catch block
                  			e.printStackTrace();
                  		}
					break;
					
				case "available":
					Form form_b = new Form();
          	    	form_b.add("userInput","busy");
          	    	
          	    	widgetsResourceURL = APPLICATION_URI + "/users/" +info.getUserName();
          	    	Request request_2 = new Request(Method.PUT,widgetsResourceURL);
     
          	    	request_2.setEntity(form.getWebRepresentation());
          	    
          	    	Response resp_2 = new Client(Protocol.HTTP).handle(request_2);
          	    	Representation responseData_2 = resp_2.getEntity();
          	    	 try {
              			System.out.println(responseData_2.getText());
              		} catch (IOException e) {
              			// TODO Auto-generated catch block
              			e.printStackTrace();
              		}
								
					
					break;
				
					
				case "exit":
					
					 widgetsResourceURL = APPLICATION_URI + "/users/"+name_1;
            	    request = new Request(Method.DELETE,widgetsResourceURL);
            	    
            	    request.getClientInfo().getAcceptedMediaTypes().
                    add(new Preference(MediaType.APPLICATION_JSON));
            
            	    // Now we do the HTTP GET
            	    System.out.println("Sending an HTTP DELETE to " + widgetsResourceURL + ".");
            		 resp = new Client(Protocol.HTTP).handle(request);
					
					System.exit(0);
											
				}		
					
														
				}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unsuccessfull");
			e.printStackTrace();
		}

	}
}

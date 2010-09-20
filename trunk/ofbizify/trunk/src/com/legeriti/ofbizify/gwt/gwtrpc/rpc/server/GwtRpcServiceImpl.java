/*
 * Copyright 2010 Abdullah Shaikh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */


package com.legeriti.ofbizify.gwt.gwtrpc.rpc.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.AbstractRemoteServiceServlet;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RPCServletUtils;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;
import com.legeriti.ofbizify.gwt.gwtrpc.rpc.client.GwtRpcService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GwtRpcServiceImpl extends AbstractRemoteServiceServlet implements
		GwtRpcService, SerializationPolicyProvider {
	
	public static final String module = GwtRpcServiceImpl.class.getName();

	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
		      HttpServletRequest request, String moduleBaseURL, String strongName) {
		    // The request can tell you the path of the web app relative to the
	    // container root.
	    String contextPath = request.getContextPath();
	
	    String modulePath = null;
	    if (moduleBaseURL != null) {
	      try {
	        modulePath = new URL(moduleBaseURL).getPath();
	      } catch (MalformedURLException ex) {
	        // log the information, we will default
	        servlet.log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
	      }
	    }
	
	    SerializationPolicy serializationPolicy = null;
	
	    /*
	     * Check that the module path must be in the same web app as the servlet
	     * itself. If you need to implement a scheme different than this, override
	     * this method.
	     */
	    if (modulePath == null || !modulePath.startsWith(contextPath)) {
	      String message = "ERROR: The module path requested, "
	          + modulePath
	          + ", is not in the same web application as this servlet, "
	          + contextPath
	          + ".  Your module may not be properly configured or your client and server code maybe out of date.";
	      servlet.log(message, null);
	    } else {
	      // Strip off the context path from the module base URL. It should be a
	      // strict prefix.
	      String contextRelativePath = modulePath.substring(contextPath.length());
	
	      String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(contextRelativePath
	          + strongName);
	
	      // Open the RPC resource file and read its contents.
	      InputStream is = servlet.getServletContext().getResourceAsStream(
	          serializationPolicyFilePath);
	      try {
	        if (is != null) {
	          try {
	            serializationPolicy = SerializationPolicyLoader.loadFromStream(is,
	                null);
	          } catch (ParseException e) {
	            servlet.log("ERROR: Failed to parse the policy file '"
	                + serializationPolicyFilePath + "'", e);
	          } catch (IOException e) {
	            servlet.log("ERROR: Could not read the policy file '"
	                + serializationPolicyFilePath + "'", e);
	          }
	        } else {
	          String message = "ERROR: The serialization policy file '"
	              + serializationPolicyFilePath
	              + "' was not found; did you forget to include it in this deployment?";
	          servlet.log(message);
	        }
	      } finally {
	        if (is != null) {
	          try {
	            is.close();
	          } catch (IOException e) {
	            // Ignore this error
	          }
	        }
	      }
	    }
	
	    return serializationPolicy;
	}
	
	private final Map<String, SerializationPolicy> serializationPolicyCache = new HashMap<String, SerializationPolicy>();
	
	public final SerializationPolicy getSerializationPolicy(String moduleBaseURL,
		      String strongName) {
	
	    SerializationPolicy serializationPolicy = getCachedSerializationPolicy(
	        moduleBaseURL, strongName);
	    if (serializationPolicy != null) {
	      return serializationPolicy;
	    }

	    serializationPolicy = doGetSerializationPolicy(getThreadLocalRequest(),
	        moduleBaseURL, strongName);

	    if (serializationPolicy == null) {
	      // Failed to get the requested serialization policy; use the default
	      log(
	          "WARNING: Failed to get the SerializationPolicy '"
	              + strongName
	              + "' for module '"
	              + moduleBaseURL
	              + "'; a legacy, 1.3.3 compatible, serialization policy will be used.  You may experience SerializationExceptions as a result.",
	          null);
	      serializationPolicy = RPC.getDefaultSerializationPolicy();
	    }
	
	    // This could cache null or an actual instance. Either way we will not
	    // attempt to lookup the policy again.
	    putCachedSerializationPolicy(moduleBaseURL, strongName, serializationPolicy);
	
	    return serializationPolicy;
	}

	public String processCall(String payload) throws SerializationException {
	    try {
	      RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);
	      onAfterRequestDeserialized(rpcRequest);
	      return RPC.invokeAndEncodeResponse(this, rpcRequest.getMethod(),
	          rpcRequest.getParameters(), rpcRequest.getSerializationPolicy(),
	          rpcRequest.getFlags());
	    } catch (IncompatibleRemoteServiceException ex) {
	      log(
	          "An IncompatibleRemoteServiceException was thrown while processing this call.",
          ex);
	      return RPC.encodeResponseForFailure(null, ex);
	    }
	}

	protected SerializationPolicy doGetSerializationPolicy(
		      HttpServletRequest request, String moduleBaseURL, String strongName) {
		return loadSerializationPolicy(this, request, moduleBaseURL, strongName);
	}
	
	protected void onAfterResponseSerialized(String serializedResponse) {
	}
	
	protected void onBeforeRequestDeserialized(String serializedRequest) {
	}
	
	protected boolean shouldCompressResponse(HttpServletRequest request,
	      HttpServletResponse response, String responsePayload) {
	    return RPCServletUtils.exceedsUncompressedContentLengthLimit(responsePayload);
	}

	private SerializationPolicy getCachedSerializationPolicy(
      String moduleBaseURL, String strongName) {
		synchronized (serializationPolicyCache) {
			return serializationPolicyCache.get(moduleBaseURL + strongName);
		}
	}

	private void putCachedSerializationPolicy(String moduleBaseURL,
		      String strongName, SerializationPolicy serializationPolicy) {
	    synchronized (serializationPolicyCache) {
	      serializationPolicyCache.put(moduleBaseURL + strongName,
	          serializationPolicy);
	    }
	}

	private void writeResponse(HttpServletRequest request,
		      HttpServletResponse response, String responsePayload) throws IOException {
		    boolean gzipEncode = RPCServletUtils.acceptsGzipEncoding(request)
		        && shouldCompressResponse(request, response, responsePayload);

		    RPCServletUtils.writeResponse(getServletContext(), response,
	        responsePayload, gzipEncode);
	}	

	@Override
	public final void processPost(HttpServletRequest request,
	      HttpServletResponse response) throws IOException, ServletException,
	      SerializationException {
	    // Read the request fully.
	    //
		
		//get the requestPayload from the request attribute instead of reading it from request 
	    //String requestPayload = readContent(request);
		String requestPayload = (String)request.getAttribute("requestPayload");

		 // Let subclasses see the serialized request.
	    //
	    onBeforeRequestDeserialized(requestPayload);
	
	    // Invoke the core dispatching logic, which returns the serialized
	    // result.
	    //
	    String responsePayload = processCall(requestPayload);
	
	    // Let subclasses see the serialized response.
	    //
	    onAfterResponseSerialized(responsePayload);
	
	    // Write the response.
	    //
	    writeResponse(request, response, responsePayload);
	}
	
	/*public HashMap<String, List<String>> processRequestListString(HashMap<String, String> parameters) {
	}

	public HashMap<String, List<HashMap<String, String>>> processRequestListMap(HashMap<String, String> parameters) {
		return new HashMap<String, List<HashMap<String,String>>>();
	}
	 */

	public HashMap<String, String> convertToStringMap(Map<String, Object> inMap) {

		HashMap<String, String> outMap = new HashMap<String, String>();

		Iterator<String> keys = inMap.keySet().iterator();

		while(keys.hasNext()) {
			String key = keys.next();
			outMap.put(key, (String)inMap.get(key));
		}

		return outMap;
	}

	public HashMap<String, String> convertToStringMap(GenericValue gv, String fields) {

		HashMap<String, String> outMap = new HashMap<String, String>();
		
		String[] fieldsArray = null;

		if(fields != null) {
    		fieldsArray = fields.split(",");
    	}

		if(fieldsArray != null) {

			for(int i=0; i<fieldsArray.length; i++) {
				outMap.put(fieldsArray[i], gv.getString(fieldsArray[i]));
			}
			
		} else {

			Map<String, Object> gvMap = gv.getAllFields();

			Iterator<String> keys = gvMap.keySet().iterator();

			while(keys.hasNext()) {
				
				String key = keys.next();
				outMap.put(key, gvMap.get(key).toString());
			}
		}
		
		return outMap;
	}

	public List<HashMap<String, String>> convertToStringMapList(List<GenericValue> gvList, String fields) {
		
		List<HashMap<String, String>> outList = new ArrayList<HashMap<String, String>>();
		
		String[] fieldsArray = null;

    	if(fields != null) {
    		fieldsArray = fields.split(",");
    	}

    	for (GenericValue genericValue : gvList) {

    		HashMap<String, String> gv = new HashMap<String, String>();
    		
    		if(fieldsArray != null) {

    			for(int i=0; i<fieldsArray.length; i++) {
    				gv.put(fieldsArray[i], genericValue.getString(fieldsArray[i]));
    			}
    			
    		} else {
    			
    			Map<String, Object> gvMap = genericValue.getAllFields();
    			
    			Iterator<String> keys = gvMap.keySet().iterator();
    			
    			while(keys.hasNext()) {
    				
    				String key = keys.next();
    				gv.put(key, gvMap.get(key).toString());
    			}
    			
    		}

    		outList.add(gv);
    	}

    	return outList;
	}

	public HashMap<String, Object> processRequest(HashMap<String, String> parameters) {

		//HashMap<String, Object> returnMap = new HashMap<String, Object>();
		HashMap<String, Object> result = null;
		
		if(Debug.infoOn()) {
			Debug.logInfo("In processRequest : parameters " + parameters, module);
		}

		HttpServletRequest request = getThreadLocalRequest();
    	//HttpServletResponse response = getThreadLocalResponse();

		Object ofbizPayLoad = request.getAttribute("ofbizPayLoad");
    	if(Debug.infoOn()) {
			Debug.logInfo("ofbizPayLoad : " + ofbizPayLoad, module);
		}

    	result = (HashMap<String, Object>)ofbizPayLoad;
    	if(Debug.infoOn()) {
			Debug.logInfo("result : " + result, module);
		}
    	
    	//returnMap.put(ModelService.RESPONSE_MESSAGE, result.get(ModelService.RESPONSE_MESSAGE));
    	
    	//String responseMessage = 
    	Object resultValue = result.get("payload");
    	if(Debug.infoOn()) {
			Debug.logInfo("resultValue : " + resultValue, module);
		}

    	if(resultValue != null) {

    		if(resultValue instanceof String) {
    			
    			if(Debug.infoOn()) {
    				Debug.logInfo("resultValue is String", module);
    			}
    			result.put("payload", resultValue);
	    	}
			else
    		if(resultValue instanceof Map<?, ?>) {
    			
    			if(Debug.infoOn()) {
    				Debug.logInfo("resultValue is Map", module);
    			}

    			String className = resultValue.getClass().getName();
    			if(Debug.infoOn()) {
    				Debug.logInfo("resultValue class : " + className, module);
    			}

    			HashMap<String, String> servResult = null;
    			
    			if("javolution.util.FastMap".equals(className) || "java.util.HashMap".equals(className)) {
    				servResult = convertToStringMap((Map<String, Object>)resultValue);
    			}
    			else
    			if("org.ofbiz.entity.GenericValue".equals(className)) {

    				String fields = parameters.get("fields");
    				if(Debug.infoOn()) {
        				Debug.logInfo("fields : " + fields, module);
        			}
    				
    				servResult = convertToStringMap((GenericValue)resultValue, fields);
    			}

    			result.put("payload", servResult);
    		}
	    	else
	    	if(resultValue instanceof List<?>) {

	    		if(Debug.infoOn()) {
    				Debug.logInfo("resultValue is List", module);
    			}
	    		
	    		String fields = parameters.get("fields");
	    		if(Debug.infoOn()) {
    				Debug.logInfo("fields : " + fields, module);
    			}

	    		List<HashMap<String, String>> servResult = convertToStringMapList((List<GenericValue>)resultValue, fields);

	    		result.put("payload", servResult);
	        }
    		
    		if(Debug.infoOn()) {
				Debug.logInfo("result : " + result, module);
			}

    	}

    	//String mode = parameters.get("mode");

    	//return returnMap;
    	return result;
	}

}

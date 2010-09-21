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


package com.legeriti.ofbizify.gwt.gwtrpc.event;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.event.EventHandler;
import org.ofbiz.webapp.event.EventHandlerException;

import com.legeriti.ofbizify.gwt.gwtrpc.util.GwtRpcUtil;

public class GwtRpcServiceEventHandler implements EventHandler {

    public static final String module = GwtRpcServiceEventHandler.class.getName();

    public void init(ServletContext context) throws EventHandlerException {
    	//need to put the variables initialization here for eg. implicitVariables
    }

    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {

    	if(Debug.infoOn()) {
    		Debug.logInfo("In GwtRpcServiceEventHandler", module);
    	}

    	String requestPayload = null;

    	try {
    		
    		requestPayload = GwtRpcUtil.getRequestPayload(request);
    		
    	} catch(IOException ioe) {
    		throw new EventHandlerException("Exception while getting requestPayload",ioe);
    	} catch(ServletException se) {
    		throw new EventHandlerException("Exception while getting requestPayload",se);
    	}

    	HashMap<String, String> gwtParameters = GwtRpcUtil.getParameters(requestPayload);
    	if(Debug.infoOn()) {
    		Debug.logInfo("gwtParameters : " + gwtParameters, module);
    	}
    	
    	if(null != gwtParameters) {
    		
    		String serviceName = event.invoke;
    		if(Debug.infoOn()) {
    			Debug.logInfo("serviceName : " + serviceName, module);
    		}
    		
    		Set<String> keys = gwtParameters.keySet();
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				request.setAttribute(key, gwtParameters.get(key));
			}
			
			RequestHandler requestHandler = (RequestHandler) request.getAttribute("_REQUEST_HANDLER_");
			EventHandler serviceEventHandler = requestHandler.getEventFactory().getEventHandler("service");

			String eventResult = null;
			try {
				
				eventResult = serviceEventHandler.invoke(new Event("service", "", event.invoke, true), null, request, response);
				if(Debug.infoOn()) {
	    			Debug.logInfo("eventResult : " + eventResult, module);
	    		}
				
			} catch(EventHandlerException ehe) {
				throw new EventHandlerException("Exception while executing service event : ",ehe);
			}

			if(Debug.infoOn()) {

				Enumeration<String> attrNames = request.getAttributeNames();
				while(attrNames.hasMoreElements()) {
					String attrName = attrNames.nextElement();
					Debug.logInfo("attrName : " + attrName + " - attrValue : " + request.getAttribute(attrName), module);
				}
			}

			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            if (dispatcher == null) {
            	throw new EventHandlerException("local dispatcher is null");
            }
            
            DispatchContext dctx = dispatcher.getDispatchContext();
            if (dctx == null) {
            	throw new EventHandlerException("dispatch context is null");
            }
            
            ModelService model = null;
            
            try {
            	model = dctx.getModelService(serviceName);
            	if(Debug.infoOn()) {
            		Debug.logInfo("model : " + model, module);
            	}
            } catch (GenericServiceException gse) {
            	throw new EventHandlerException("exception getting service model", gse);
            }

            if (null == model) {
            	throw new EventHandlerException("service model is null");
            }
            
            /*List<String> implicitVariables = new ArrayList<String>();
            implicitVariables.add("userLogin");
            implicitVariables.add("timeZone");
            implicitVariables.add("locale");*/
            //implicitVariables.add("responseMessage");
            //implicitVariables.add("successMessage");
            //implicitVariables.add("successMessageList");
            //implicitVariables.add("errorMessage");
            //implicitVariables.add("errorMessageList");
            
            //Set<String> outParams = model.getOutParamNames();
            //Iterator<String> iterOP = outParams.iterator();

            /*Map<String, Object> serviceResult = new HashMap<String, Object>();

            while(iterOP.hasNext()) {
            	
            	String opName = iterOP.next();

            	if(!implicitVariables.contains(opName)) {
            		
                	Object opValue = request.getAttribute(opName);

                	serviceResult.put(opName, opValue);
                	request.removeAttribute(opName);

                	if(Debug.infoOn()) {
            			Debug.logInfo("opName : " + opName + " - opValue : " + opValue, module);
            		}
                	
                	//request.setAttribute("result", opValue);
            	}
            }*/

            /*if(serviceResult.size() == 0) {
            	request.setAttribute("ofbizPayLoad", null);
            } else {
            	
            	if(serviceResult.size() == 1) {
            		
            		Iterator<String> iter1 = serviceResult.keySet().iterator();
            		
            		String key = null;
            		
            		if(iter1.hasNext()) {
            			key = iter1.next();
            		}
            		
            		request.setAttribute("ofbizPayLoad", serviceResult.get(key));

            	} else {
            		
            		request.setAttribute("ofbizPayLoad", serviceResult);
            	}
            }*/
            
            Map<String, Object> resultMap = (Map<String, Object>) request.getAttribute("result");
            System.err.println("service resultMap -> " + resultMap);

            ServletContext sc = (ServletContext)request.getAttribute("servletContext");
        	RequestDispatcher rd = sc.getRequestDispatcher("/gwtrpc");

        	request.setAttribute("ofbizPayLoad", resultMap);
        	request.setAttribute("requestPayload", requestPayload);

        	try {
        		rd.forward(request, response);
        	} catch(IOException ioe) {
            	throw new EventHandlerException("IO Exception while forwarding request to GWT RPC servlet  : ",ioe);
            } catch(ServletException se) {
            	throw new EventHandlerException("Servlet Exception while forwarding request to GWT RPC servlet  : ",se);
            }
        	

    	} else {
    		throw new EventHandlerException("GWT parameters are null  : " + gwtParameters);
    	}
    	
    	return "success";
    }
}

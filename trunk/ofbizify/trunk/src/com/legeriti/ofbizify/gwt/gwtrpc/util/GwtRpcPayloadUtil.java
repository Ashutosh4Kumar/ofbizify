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

package com.legeriti.ofbizify.gwt.gwtrpc.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class GwtRpcPayloadUtil extends ServiceUtil {

    public static final String module = GwtRpcPayloadUtil.class.getName();
    public static final String PAYLOAD = "payload";

    public static Map<String, Object> returnSuccess() {
        return returnMessage(ModelService.RESPOND_SUCCESS, null);
    }
    
    public static Map<String, Object> returnSuccess(String successMessage) {
        return returnMessage(ModelService.RESPOND_SUCCESS, successMessage);
    }

    public static Map<String, Object> returnSuccess(List<String> successMessageList) {
        Map<String, Object> result = returnMessage(ModelService.RESPOND_SUCCESS, null);
        result.put(ModelService.SUCCESS_MESSAGE_LIST, successMessageList);
        return result;
    }
    
    public static Map<String, Object> returnSuccessWithPayload(Object payload, String successMessage) {
    	Map<String, Object> result = returnSuccess(successMessage);
    	result.put(PAYLOAD, payload);
        return result;
    }
    
    public static Map<String, Object> returnSuccessWithPayload(Object payload) {
    	System.err.println("in GWT returnSuccess -> " + payload);
    	Map<String, Object> result = returnSuccess();
    	System.err.println("in GWT returnSuccess -> result " + result);
    	result.put(PAYLOAD, payload);
    	System.err.println("in GWT returnSuccess -> payload " + result);
        return result;
    }
    
    public static Map<String, Object> returnSuccessWithPayload(Object payload, List<String> successMessageList) {
    	Map<String, Object> result = returnSuccess(successMessageList);
    	result.put(PAYLOAD, payload);
        return result;
    }
    
    public static Map<String, Object> returnMessage(String code, String message) {
    	
    	System.err.println("in GWT returnMessage (overrided)");
    	
        //Map<String, Object> result = FastMap.newInstance();
    	Map<String, Object> result = new HashMap<String, Object>();
        if (code != null) result.put(ModelService.RESPONSE_MESSAGE, code);
        if (message != null) result.put(ModelService.SUCCESS_MESSAGE, message);
        return result;
    }
    
    public static Map<String, Object> returnProblem(String returnType, String errorMessage, List<? extends Object> errorMessageList, Map<String, ? extends Object> errorMessageMap, Map<String, ? extends Object> nestedResult) {
        //Map<String, Object> result = FastMap.newInstance();
    	Map<String, Object> result = new HashMap<String, Object>();
        result.put(ModelService.RESPONSE_MESSAGE, returnType);
        if (errorMessage != null) {
            result.put(ModelService.ERROR_MESSAGE, errorMessage);
        }

        List<Object> errorList = new LinkedList<Object>();
        if (errorMessageList != null) {
            errorList.addAll(errorMessageList);
        }

        Map<String, Object> errorMap = FastMap.newInstance();
        if (errorMessageMap != null) {
            errorMap.putAll(errorMessageMap);
        }

        if (nestedResult != null) {
            if (nestedResult.get(ModelService.ERROR_MESSAGE) != null) {
                errorList.add(nestedResult.get(ModelService.ERROR_MESSAGE));
            }
            if (nestedResult.get(ModelService.ERROR_MESSAGE_LIST) != null) {
                errorList.addAll(UtilGenerics.checkList(nestedResult.get(ModelService.ERROR_MESSAGE_LIST)));
            }
            if (nestedResult.get(ModelService.ERROR_MESSAGE_MAP) != null) {
                errorMap.putAll(UtilGenerics.<String, Object>checkMap(nestedResult.get(ModelService.ERROR_MESSAGE_MAP)));
            }
        }

        if (errorList.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorList);
        }
        if (errorMap.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMap);
        }
        return result;
    }

}

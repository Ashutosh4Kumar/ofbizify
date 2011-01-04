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

package com.ofbizify.gwt.gwtrpc.rpc.client.util;

import java.util.HashMap;

public class ServiceUtil {

	private static final String RESPONSE_MESSAGE = "responseMessage";
	
	private static final String RESPOND_SUCCESS = "success";
	private static final String RESPOND_ERROR = "error";

	public static boolean isSuccess(HashMap<String, Object> result) {
		
		if(result == null || result.get(RESPONSE_MESSAGE) == null) {
			return false;
		}

		String responseMessage = (String) result.get("responseMessage");

		return RESPOND_SUCCESS.equals(responseMessage);
	}
	
	public static boolean isError(HashMap<String, Object> result) {

		if(result == null || result.get(RESPONSE_MESSAGE) == null) {
			return false;
		}

		String responseMessage = (String) result.get("responseMessage");
		return RESPOND_ERROR.equals(responseMessage);
	}

}

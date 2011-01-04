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

package com.ofbizify.gwt.gwtrpc.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The client side stub for the RPC service.
 */
public interface GwtRpcService extends RemoteService {

	HashMap<String, Object> processRequest(HashMap<String, String> parameters);
	
	//dummy method to get the generation of rpc file properly
	//String dummy1(HashMap<String, String> parameters);
	//HashMap<String, String> dummy2(HashMap<String, String> parameters);
	//ArrayList<HashMap<String, String>> dummy3(HashMap<String, String> parameters);
	//ArrayList<HashMap<String, Object>> dummy4(HashMap<String, String> parameters);
}
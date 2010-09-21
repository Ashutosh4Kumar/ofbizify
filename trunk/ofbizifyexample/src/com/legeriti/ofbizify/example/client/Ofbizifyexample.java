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

package com.legeriti.ofbizify.example.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.legeriti.ofbizify.gwt.gwtrpc.rpc.client.GwtRpcService;
import com.legeriti.ofbizify.gwt.gwtrpc.rpc.client.GwtRpcServiceAsync;
import com.legeriti.ofbizify.gwt.gwtrpc.rpc.client.util.ServiceUtil;

public class Ofbizifyexample implements EntryPoint {
	
	int GROOVY = 1;
	int JAVA = 2;
	int SERVICE = 3;

	GwtRpcServiceAsync gwtrcpService = GWT.create(GwtRpcService.class);
	ServiceDefTarget endpoint = (ServiceDefTarget) gwtrcpService;
	
	String CONTEXT_URL = "/ofbizifyexample/control/";
	
	//for groovy services
	String GROOVY_CREATE_USER_DETAILS = CONTEXT_URL + "groovyCreateUserDetails";
	String GROOVY_GET_USER_DETAILS = CONTEXT_URL + "groovyGetUserDetails";
	
	String JAVA_CREATE_USER_DETAILS = CONTEXT_URL + "javaCreateUserDetails";
	String JAVA_GET_USER_DETAILS = CONTEXT_URL + "javaGetUserDetails";
	
	String SERVICE_CREATE_USER_DETAILS = CONTEXT_URL + "serviceCreateUserDetails";
	String SERVICE_GET_USER_DETAILS = CONTEXT_URL + "serviceGetUserDetails";

	private DockPanel mainPanel;
	private DecoratedTabPanel tabPanel = new DecoratedTabPanel();

	private FlexTable dataTable = new FlexTable();
	
	//for groovy
	TextBox gFirstName = new TextBox();
	TextBox gLastName = new TextBox();
	TextBox gAddress = new TextBox();

	//for java
	TextBox jFirstName = new TextBox();
	TextBox jLastName = new TextBox();
	TextBox jAddress = new TextBox();

	//for services
	TextBox sFirstName = new TextBox();
	TextBox sLastName = new TextBox();
	TextBox sAddress = new TextBox();

	public Ofbizifyexample() {
		
		mainPanel = new DockPanel();
		mainPanel.setStyleName("mainPanel");
		
		mainPanel.setWidth("100%");
		mainPanel.setHeight(Window.getClientHeight()+"px");

		HorizontalPanel headerPanel = new HorizontalPanel();
		mainPanel.add(headerPanel, DockPanel.NORTH);

		headerPanel.setStyleName("fullSize");
		Widget title = createTitle("ofbizify - Ajaxifying OFBiz, using GWT", false);
		headerPanel.add(title);

		VerticalPanel footerPanel = new VerticalPanel();
		//footerPanel.setStyleName("centerPanel");
		mainPanel.add(footerPanel, DockPanel.SOUTH);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.setStyleName("centerPanel");
		mainPanel.add(centerPanel, DockPanel.CENTER);

		centerPanel.add(tabPanel);
		
		createTabs();
		
		VerticalPanel dataPanel = new VerticalPanel();
		dataPanel.setSpacing(20);

		ScrollPanel scroll = new ScrollPanel(dataPanel);
		centerPanel.add(scroll);
		
		dataPanel.add(dataTable);
		
		HTML hr = new HTML("<hr/>");
		dataPanel.add(hr);

		createDataTable();

		//--
		//footerPanel.add(createTable());
		//--
		
		mainPanel.setCellWidth(headerPanel, "100%");
		mainPanel.setCellHeight(headerPanel, "5%");

		mainPanel.setCellWidth(centerPanel, "100%");
		mainPanel.setCellHeight(centerPanel, "90%");
		
		mainPanel.setCellWidth(footerPanel, "100%");
		mainPanel.setCellHeight(footerPanel, "5%");
		
		getUserDetails(GROOVY);

	}

	private void createTabs() {

		tabPanel.setStyleName("fullSize");
	    tabPanel.setAnimationEnabled(true);

	    VerticalPanel groovyPanel = new VerticalPanel();
	    tabPanel.add(groovyPanel, "Groovy");
	    
	    groovyPanel.add(createGroovyForm());
	    

	    VerticalPanel javaPanel = new VerticalPanel();
	    tabPanel.add(javaPanel, "Java");
	    
	    javaPanel.add(createJavaForm());
	    
	    VerticalPanel servicePanel = new VerticalPanel();
	    tabPanel.add(servicePanel, "Service");

	    servicePanel.add(createServiceForm());
	    
	    tabPanel.selectTab(0);
	}
	
	private void createDataTable() {
		
		dataTable.setHTML(0, 0, "<b>First Name</b>");
		dataTable.setHTML(0, 1, "&nbsp;&nbsp;&nbsp;&nbsp;");
		dataTable.setHTML(0, 2, "<b>Last Name</b>");
		dataTable.setHTML(0, 3, "&nbsp;&nbsp;&nbsp;&nbsp;");
		dataTable.setHTML(0, 4, "<b>Address</b>");
	}

	private boolean validateForm(int type) {

		String strFirstName = null;
		String strLastName = null;
		String strAddress = null;

		if(type == 1) {
			strFirstName = gFirstName.getText().trim();
			strLastName = gLastName.getText().trim();
			strAddress = gAddress.getText().trim();
		} else if(type == 2) {
			strFirstName = jFirstName.getText().trim();
			strLastName = jLastName.getText().trim();
			strAddress = jAddress.getText().trim();
		} else if(type == 3) {
			strFirstName = sFirstName.getText().trim();
			strLastName = sLastName.getText().trim();
			strAddress = sAddress.getText().trim();
		}

		if(strFirstName.length() == 0) {
			Window.alert("Please enter First Name");
			return false;
		}
		
		if(strLastName.trim().length() == 0) {
			Window.alert("Please enter Last Name");
			return false;
		}
		
		if(strAddress.trim().length() == 0) {
			Window.alert("Please enter Address");
			return false;
		}
		
		return true;
	}
	
	private Widget createGroovyForm() {
		
		FlexTable table = new FlexTable();
		table.setStyleName("fullSize");

		FlexTable formTable = new FlexTable();
		formTable.setText(0, 0, "First Name");
		formTable.setText(1, 0, "Last Name");
		formTable.setText(2, 0, "Address");

		formTable.setWidget(0, 1, gFirstName);
		formTable.setWidget(1, 1, gLastName);
		formTable.setWidget(2, 1, gAddress);
		
		Button btnSubmit = new Button("<b>Create</b>");
		//btnSubmit.setStyleName("saveChangesButtonStyle");
		
		Button btnClear = new Button("<b>Clear</b>");
		//btnClear.setStyleName("backButtonStyle");
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				gFirstName.setText("");
				gLastName.setText("");
				gAddress.setText("");
			}
		});

		formTable.setWidget(3, 0, btnSubmit);
		formTable.setWidget(3, 1, btnClear);
		
		btnSubmit.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				
				if(validateForm(GROOVY)) {
					
					//start - saving form data
					showLoadingMessage("[GROOVY] saving user details ....");
				
					HashMap<String, String> parameters = getGroovyFormData();

					AsyncCallback<HashMap<String, Object>> callback = new AsyncCallback<HashMap<String, Object>>() {
					
						public void onFailure(Throwable caught) {
							Window.alert("SERVER_ERROR " + caught.toString());
						}
					
						public void onSuccess(HashMap<String, Object> result) {

							if(ServiceUtil.isSuccess(result)) {
								
								gFirstName.setText("");
								gLastName.setText("");
								gAddress.setText("");

								hideLoadingMessage();
								Window.alert("User created");
								getUserDetails(GROOVY);

							} else {
								Window.alert("Error calling groovy : " + result);
							}
						}
					};

					endpoint.setServiceEntryPoint(GROOVY_CREATE_USER_DETAILS);
					gwtrcpService.processRequest(parameters, callback);
					//end - saving form data

				}
			}
		});

		table.setWidget(0, 0, formTable);

		return table;
	}

	private Widget createJavaForm() {
		
		FlexTable table = new FlexTable();
		table.setStyleName("fullSize");

		FlexTable formTable = new FlexTable();
		formTable.setText(0, 0, "First Name");
		formTable.setText(1, 0, "Last Name");
		formTable.setText(2, 0, "Address");

		formTable.setWidget(0, 1, jFirstName);
		formTable.setWidget(1, 1, jLastName);
		formTable.setWidget(2, 1, jAddress);
		
		Button btnSubmit = new Button("<b>Create</b>");
		//btnSubmit.setStyleName("saveChangesButtonStyle");
		
		Button btnClear = new Button("<b>Clear</b>");
		//btnClear.setStyleName("backButtonStyle");
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				jFirstName.setText("");
				jLastName.setText("");
				jAddress.setText("");
			}
		});

		formTable.setWidget(3, 0, btnSubmit);
		formTable.setWidget(3, 1, btnClear);
		
		btnSubmit.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				
				if(validateForm(JAVA)) {
					
					//start - saving form data
					showLoadingMessage("[JAVA] saving user details ....");
				
					HashMap<String, String> parameters = getJavaFormData();

					AsyncCallback<HashMap<String, Object>> callback = new AsyncCallback<HashMap<String, Object>>() {
					
						public void onFailure(Throwable caught) {
							Window.alert("SERVER_ERROR " + caught.toString());
						}
					
						public void onSuccess(HashMap<String, Object> result) {

							if(ServiceUtil.isSuccess(result)) {

								jFirstName.setText("");
								jLastName.setText("");
								jAddress.setText("");
								
								hideLoadingMessage();
								Window.alert("User created");
								getUserDetails(JAVA);

							} else {
								Window.alert("Error calling java event : " + result);
							}
						}
					};

					endpoint.setServiceEntryPoint(JAVA_CREATE_USER_DETAILS);
					gwtrcpService.processRequest(parameters, callback);
					//end - saving form data
				}
			}
		});

		table.setWidget(0, 0, formTable);
		
		table.setHTML(1, 0, "<hr/>");

		return table;
	}
	
	private Widget createServiceForm() {
		
		FlexTable table = new FlexTable();
		table.setStyleName("fullSize");

		FlexTable formTable = new FlexTable();
		formTable.setText(0, 0, "First Name");
		formTable.setText(1, 0, "Last Name");
		formTable.setText(2, 0, "Address");

		formTable.setWidget(0, 1, sFirstName);
		formTable.setWidget(1, 1, sLastName);
		formTable.setWidget(2, 1, sAddress);
		
		Button btnSubmit = new Button("<b>Create</b>");
		//btnSubmit.setStyleName("saveChangesButtonStyle");
		
		Button btnClear = new Button("<b>Clear</b>");
		//btnClear.setStyleName("backButtonStyle");
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				sFirstName.setText("");
				sLastName.setText("");
				sAddress.setText("");
			}
		});

		formTable.setWidget(3, 0, btnSubmit);
		formTable.setWidget(3, 1, btnClear);
		
		btnSubmit.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				
				if(validateForm(SERVICE)) {
					
					//start - saving form data
					showLoadingMessage("[SERVICE] saving user details ....");
				
					HashMap<String, String> parameters = getServiceFormData();

					AsyncCallback<HashMap<String, Object>> callback = new AsyncCallback<HashMap<String, Object>>() {
					
						public void onFailure(Throwable caught) {
							Window.alert("SERVER_ERROR " + caught.toString());
						}
					
						public void onSuccess(HashMap<String, Object> result) {

							if(ServiceUtil.isSuccess(result)) {

								sFirstName.setText("");
								sLastName.setText("");
								sAddress.setText("");
								
								hideLoadingMessage();
								Window.alert("User created");
								getUserDetails(SERVICE);

							} else {
								Window.alert("Error calling service : " + result);
							}
						}
					};

					endpoint.setServiceEntryPoint(SERVICE_CREATE_USER_DETAILS);
					gwtrcpService.processRequest(parameters, callback);
					//end - saving form data
				}
			}
		});

		table.setWidget(0, 0, formTable);
		
		table.setHTML(1, 0, "<hr/>");

		return table;
	}

	public void onModuleLoad() {
		RootPanel.get().add(mainPanel);
	}
	
	public Widget createTitle(String text, boolean logo) {
		
		//Image titleImg = new Image("images/user_reg_4.jpeg");
		//titleImg.setStyleName("imgStyle");
		//titleImg.setSize("30px", "30px");

		Label title = new Label(text);
		title.setStyleName("formTitle");

		FlexTable table = new FlexTable();
		table.setStyleName("fpStyle");
		table.setWidth("100%");

		if(logo) {
			//table.getFlexCellFormatter().setWidth(0, 0, "20px");
			//table.setWidget(0, 0, titleImg);
		}
		
		//table.setWidget(0, 1, title);
		
		table.setWidget(0, 0, title);
		table.setBorderWidth(0);
		
		return table; 
	}
	
	//start - util methods

	private PopupPanel popup = new PopupPanel(false, true);
	
	public void showLoadingMessage(String msg) {

		popup.clear();

		Image loadingImg = new Image("images/data-loader.gif");
		Label lbl = new Label(msg);

		FlowPanel fp = new FlowPanel();
		fp.add(loadingImg);
		fp.add(lbl);
		
		popup.add(fp);
		popup.center();
	}
	
	public void hideLoadingMessage() {
		popup.clear();
		popup.hide();
	}
	
	public HashMap<String, String> getGroovyFormData() {

		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("firstName", gFirstName.getValue());
		formData.put("lastName", gLastName.getValue());
		formData.put("address", gAddress.getValue());

		return formData;
	}
	
	public HashMap<String, String> getJavaFormData() {

		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("firstName", jFirstName.getValue());
		formData.put("lastName", jLastName.getValue());
		formData.put("address", jAddress.getValue());

		return formData;
	}
	
	public HashMap<String, String> getServiceFormData() {

		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("firstName", sFirstName.getValue());
		formData.put("lastName", sLastName.getValue());
		formData.put("address", sAddress.getValue());

		return formData;
	}
	//end - util methods
	
	//start - code for gwtrpc calls
	private void getUserDetails(int type) {

		if(type == 1) {
			showLoadingMessage("[GROOVY] loading user details ....");
		} else if(type == 2) {
			showLoadingMessage("[JAVA] loading user details ....");
		} else if(type == 3) {
			showLoadingMessage("[SERVICE] loading user details ....");
		}

		//start - get user details
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("fields", "userId,firstName,lastName,address");

		AsyncCallback<HashMap<String, Object>> callback = new AsyncCallback<HashMap<String, Object>>() {
			
			public void onFailure(Throwable caught) {
				Window.alert("SERVER_ERROR " + caught.toString());
			}
			
			public void onSuccess(HashMap<String, Object> result) {

				if(ServiceUtil.isSuccess(result)) {

					//HashMap<String, String> users = (HashMap<String, String>)result.get("result");
					ArrayList<HashMap<String, String>> users = (ArrayList<HashMap<String, String>>)result.get("payload");

					hideLoadingMessage();

					fillDataTable(users);
				}
			}
		};

		if(type == 1) {
			endpoint.setServiceEntryPoint(GROOVY_GET_USER_DETAILS);	
		} else if(type == 2) {
			endpoint.setServiceEntryPoint(JAVA_GET_USER_DETAILS);
		} else if(type == 3) {
			endpoint.setServiceEntryPoint(SERVICE_GET_USER_DETAILS);
		}

		gwtrcpService.processRequest(parameters, callback);
		//end - get contact details
	}
	
	private void fillDataTable(ArrayList<HashMap<String, String>> users) {
		
		dataTable.clear();
		
		for(int i=0; i<users.size(); i++) {
			HashMap<String, String> user = users.get(i);
			
			String userId = user.get("userId");
			String firstName = user.get("firstName");
			String lastName = user.get("lastName");
			String address = user.get("address");
			
			dataTable.setHTML(i+1, 0, firstName);
			dataTable.setHTML(i+1, 2, lastName);
			dataTable.setHTML(i+1, 4, address);
		}
	}

	//end - code for gwtrpc calls
}

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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

public class WidgetUtil {

	public static void fillMonths(ListBox listBox) {

		listBox.clear();

		listBox.addItem("January", "Jan");
		listBox.addItem("February", "Feb");
		listBox.addItem("March", "Mar");
		listBox.addItem("April", "Apr");
		listBox.addItem("May", "May");
		listBox.addItem("June", "Jun");
		listBox.addItem("July", "Jul");
		listBox.addItem("August", "Aug");
		listBox.addItem("September", "Sep");
		listBox.addItem("October", "Oct");
		listBox.addItem("November", "Nov");
		listBox.addItem("December", "Dec");
	}

	public static void setListBoxSelectedIndex(ListBox listBox, String value) {

		String val = null;
		
		for(int i=0; i<listBox.getItemCount(); i++) {
			val = listBox.getValue(i);
			if(val.equals(value)) {
				listBox.setSelectedIndex(i);
				break;
			}
		}
	}

	public static void fillListBox(ListBox listBox, String key, String value, List<HashMap<String, String>> list) {

		listBox.clear();
		listBox.addItem("select", "select");
		
		Iterator<HashMap<String, String>> iter = list.iterator();
		while(iter.hasNext()) {

			HashMap<String, String> geo = iter.next();
			listBox.addItem(geo.get(value), geo.get(key));
		}

	}

	public static void fillListBox(ListBox listBox, String defaultValue, String key, String value, List<HashMap<String, String>> list) {

		listBox.clear();
		listBox.addItem("select", "select");
		
		Iterator<HashMap<String, String>> iter = list.iterator();
		while(iter.hasNext()) {

			HashMap<String, String> geo = iter.next();
			listBox.addItem(geo.get(value), geo.get(key));
		}
		
		setListBoxSelectedIndex(listBox, defaultValue);
	}
}

This project aims to ajaxify **Apache Ofbiz**, which is an open source enterprise automation software, using Google GWT

To know more about :
Apache OFBiz - http://ofbiz.apache.org/
Google Web Toolkit - http://code.google.com/webtoolkit/

Using ofbizify, you can create rich interfaces using GWT and let the GWT web application interact with OFBiz using the native GWT-RPC protocol. GWT-RPC protocol is much easier to use as we use java objects in communication instead of xml or json.

The best thing using ofbizify is, the ofbiz component structure stays the same, you put all your request in controller file, invoke your Java Events, Groovy scripts, Services as you use to and get back the response in gwt app.

And yes, apart from building pure gwt application you can also embed gwt widget/component into existing screens/ftl pages.
<%--
//
// (c) 2012 DS Data Systems UK Ltd, All rights reserved.
//
// DS Data Systems and KonaKart and their respective logos, are 
// trademarks of DS Data Systems UK Ltd. All rights reserved.
//
// The information in this document is free software; you can redistribute 
// it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This software is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
--%>
<%@include file="Taglibs.jsp" %>
<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");  %>
 <%if (kkEng != null) {%>
 	<tiles:useAttribute id="contentClass" name="contentClass" classname="java.lang.String" ignore="true"/>
    <%kkEng.setContentClass(contentClass);%>
    <% if (kkEng.getProductMgr().getCurrentCategoriesLength() == 0 && contentClass.equalsIgnoreCase("narrow")){%>
    	<%kkEng.setContentClass("wide");%>
    <% } %>
	<html>
		<head>
			<title><%=kkEng.getPageTitle()%></title>
			<meta name="keywords" content="<%=kkEng.getMetaKeywords()%>" />
			<meta name="description" content="<%=kkEng.getMetaDescription()%>" />
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" />
			<link type="text/css" rel="stylesheet" href="<%=kkEng.getStyleBase()%>/jquery.selectboxit.css" />
			<link type="text/css" rel="stylesheet" href="<%=kkEng.getStyleBase()%>/kk-style.css" />
			
	 		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>  
			<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.validate.min.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.json-2.3.min.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.tools.min.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.selectboxit.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.ui.datepicker-en.js"></script>			 
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.ui.datepicker-de.js"></script>			 
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.ui.datepicker-pt.js"></script>			 
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.ui.datepicker-es.js"></script>			 
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/ddpowerzoomer.js">
			/***********************************************
			* Image Power Zoomer- (c) Dynamic Drive DHTML code library (www.dynamicdrive.com)
			* This notice MUST stay intact for legal use
			* Visit Dynamic Drive at http://www.dynamicdrive.com/ for this script and 100s more
			***********************************************/
			</script>			 
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.jcarousel.min.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/jquery.cookie.js"></script>
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/kk.js"></script>	
			<script type="text/javascript" src="<%=kkEng.getScriptBase()%>/kk.validation.js"></script>	
						
			<%
			// Set the base
			String scheme = pageContext.getRequest().getScheme()+"://";
			String server = pageContext.getRequest().getServerName();
			int port = pageContext.getRequest().getServerPort();
			String path = session.getServletContext().getContextPath();
			String base = scheme+server+((port==80)?"":":"+port)+path+"/";
			%>
			<base href="<%=base%>"/>
									
			<script language="JavaScript" type="text/javascript">
				if (top.location != location) {
  					top.location.href = document.location.href ;
					}
 					
 				// Set jquery validation messages
 				jQuery.validator.messages = {
 						state: '<%=kkEng.getMsg("jquery.validator.required")%>',
 						country: '<%=kkEng.getMsg("jquery.validator.required")%>',
 						required: '<%=kkEng.getMsg("jquery.validator.required")%>',
 						creditcard: '<%=kkEng.getMsg("jquery.validator.creditcard")%>',
 						digits: '<%=kkEng.getMsg("jquery.validator.digits")%>',
 						maxlength: jQuery.validator.format('<%=kkEng.getMsg("jquery.validator.maxlength")%> {0} <%=kkEng.getMsg("jquery.validator.maxlength1")%>'),
 						minlength: jQuery.validator.format('<%=kkEng.getMsg("jquery.validator.minlength")%> {0} <%=kkEng.getMsg("jquery.validator.minlength1")%>'),
 						expirydate: '<%=kkEng.getMsg("jquery.validator.expiryMMYY")%>',
 						email:	'<%=kkEng.getMsg("jquery.validator.email")%>',
 						url:	'<%=kkEng.getMsg("jquery.validator.url")%>',
 						number:	'<%=kkEng.getMsg("jquery.validator.number")%>',
 	 					equalTo: '<%=kkEng.getMsg("jquery.validator.equalTo")%>'
				};	
 				
 
			</script>			
			<!--- KonaKart v6.5.1.0.9307 -->
		</head>
		<body>
			<tiles:insertAttribute name="header1" />
			<tiles:insertAttribute name="header2" />
    		<div id="page-container">
    			<div id="page" class="<tiles:insertAttribute name="pageClass"/>">
					<tiles:insertAttribute name="header3" />
					<tiles:insertAttribute name="header4" />
					<tiles:insertAttribute name="left1" />
					<div id="content" class="<%=kkEng.getContentClass()%>">
						<tiles:insertAttribute name="body1" /> 
				 		<tiles:insertAttribute name="body2" /> 
						<tiles:insertAttribute name="body3" />
						<tiles:insertAttribute name="body4" />
						<tiles:insertAttribute name="body5" />
						<tiles:insertAttribute name="body6" />
			    	</div>
					<tiles:insertAttribute name="footer1" />
	    		</div>
    		</div>
			<tiles:insertAttribute name="footer2" />
		</body>
	</html>
<%}%>

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

<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");  %>
<% com.konakart.al.CustomerMgr customerMgr = kkEng.getCustomerMgr();%>
<% com.konakart.appif.ZoneIf[] zoneArray = customerMgr.getSelectedZones();%>

<script type="text/javascript">
	$(function() {
		$.datepicker.setDefaults($.datepicker.regional['<%=kkEng.getLocale().substring(0,2)%>']);
		$('#datepicker').datepicker({changeMonth: true, changeYear: true, dateFormat: '<%=kkEng.getMsg("datepicker.date.format")%>', yearRange: "-120:-10", minDate: '-120y', maxDate: '-10y'});
	});
	
</script>	

    			<s:set scope="request" var="allowNoRegisterStr" value="allowNoRegisterStr"/> 
				<%String allowNoRegisterStr = (String)(request.getAttribute("allowNoRegisterStr")); %>
				<%boolean allowNoRegister = (allowNoRegisterStr == null || !allowNoRegisterStr.equalsIgnoreCase("true")) ? false : true; %>
				<%if (!allowNoRegister){ %>
					<h1 id="page-title"><kk:msg  key="register.customer.body.new.account"/></h1>
				<% } else { %>
					<h1 id="page-title"><kk:msg  key="register.customer.body.delivery.details"/></h1>
				<% } %>
    			
	    		<div class="content-area rounded-corners">
		    		<div>
		    			<s:if test="hasActionErrors()">
						   <div class="messageStackError">  
						        <s:iterator value="actionErrors">  
						            <s:property escape="false"/>
						        </s:iterator>  
			    			</div>  
						</s:if>		    		    		
			    		<form action="CustomerRegistrationSubmit.action" id="form1" method="post">
							<div class="form-section">
								<div>
									<h3><kk:msg  key="register.customer.body.personal.details"/><span class="required-text"><img src="<%=kkEng.getImageBase()%>/icons/required-blue.png">&nbsp;<kk:msg  key="common.required.fields"/></span></h3>							
								</div>
								<div class="form-section-fields">
									<div class="form-section-divider"></div>
									<%if (kkEng.getConfigAsBoolean("ACCOUNT_GENDER",false)) { %>
										<div class="form-input radio-buttons">
											<label><kk:msg  key="register.customer.body.gender"/></label>
											<s:set scope="request" var="req_gender" value="gender"/> 
											<%String g = (String)(request.getAttribute("req_gender")); %>
											<span class="radio-button"><input type="radio" name="gender" value="m" <%=(g!=null&&g.equals("m"))?"checked":"" %>> <kk:msg  key="register.customer.body.male"/></span> 
											<span class="radio-button"><input type="radio" name="gender" value="f" <%=(g!=null&&g.equals("f"))?"checked":"" %>> <kk:msg  key="register.customer.body.female"/></span>	
											<span class="required-icon required-blue"></span>	
											<span class="validation-msg"></span>							
										</div>
									<% } else { %>
										<input type="hidden" name="gender" value="-"/>
									<% } %>
									
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.first.name"/></label>
										<input type="text" value="<s:property value="firstName" />" name="firstName"/>
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.last.name"/></label>
										<input type="text" value="<s:property value="lastName" />" name="lastName"/>
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<%if (kkEng.getConfigAsBoolean("ACCOUNT_DOB",false)) { %>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.dob"/></label>
											<input id="datepicker" type="text" value="<s:property value="birthDateString" />" name="birthDateString"/>
											<span class="required-icon required-blue"></span>
											<span class="validation-msg"></span>
										</div>
									<% } else { %>
										<input type="hidden" name="birthDateString" value="<%=kkEng.getNowAsString()%>"/>
									<% } %>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.email"/></label>
										<input type="text" value="<s:property value="emailAddr" />"  name="emailAddr"/>
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
								</div>
							</div>
							<%if (kkEng.getConfigAsBoolean("ACCOUNT_COMPANY",false)) { %>
								<div class="form-section">
									<h3><kk:msg  key="register.customer.body.company.details"/></h3>
									<div class="form-section-fields">
										<div class="form-section-divider"></div>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.company.name"/></label>
											<input type="text" value="<s:property value="company" />" name="company"/>
											<span class="validation-msg"></span>
										</div>
									</div>
								</div>
							<% } else { %>
								<input type="hidden" name="company" value=""/>
							<% } %>
							<div class="form-section">
								<h3><kk:msg  key="register.customer.body.addr"/></h3>
								<div class="form-section-fields">
									<div class="form-section-divider"></div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.street.addr"/></label>
										<input type="text" value="<s:property value="streetAddress" />" name="streetAddress"/>
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<%if (kkEng.getConfigAsBoolean("ACCOUNT_STREET_ADDRESS_1",false)) { %>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.street.addr1"/></label>
											<input type="text" value="<s:property value="streetAddress1" />" name="streetAddress1" />
											<span class="required-icon required-blue"></span>
											<span class="validation-msg"></span>
										</div>
									<% } else { %>
										<input type="hidden" name="streetAddress1"  value=""/>
									<% } %>
									<%if (kkEng.getConfigAsBoolean("ACCOUNT_SUBURB",false)) { %>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.suburb"/></label>
											<input  type="text" value="<s:property value="suburb" />" name="suburb"/>
											<span class="validation-msg"></span>
										</div>
									<% } else { %>
										<input type="hidden" name="suburb" value=""/>
									<% } %>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.postcode"/></label>
										<input type="text" value="<s:property value="postcode" />" name="postcode" />
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.city"/></label>
										<input type="text" value="<s:property value="city" />" name="city" />
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<%if (kkEng.getConfigAsBoolean("ACCOUNT_STATE",false)) { %>
										<%if (zoneArray != null && zoneArray.length > 0){ %>	
											<div class="form-input">
												<label><kk:msg  key="register.customer.body.state"/></label>
												<select name="state" class="state">
													<option value="-1"><kk:msg  key="register.customer.body.select"/></option>
													<s:set scope="request" var="state"  value="state"/> 						
													<% String state = ((String)request.getAttribute("state"));%> 
													<% for (int i = 0; i < zoneArray.length; i++){ %>
														<% com.konakart.appif.ZoneIf zone = zoneArray[i];%>
														<%if (state != null && state.equals(zone.getZoneName())){ %>
															<option  value="<%=zone.getZoneName()%>" selected="selected"><%=zone.getZoneName()%></option>
														<% } else { %>
															<option  value="<%=zone.getZoneName()%>"><%=zone.getZoneName()%></option>
														<% } %>
													<% } %>
												</select>
												<span class="required-icon required-blue"></span>
												<span class="validation-msg"></span>
											</div>
										<% } else {%>
											<div class="form-input">
												<label><kk:msg  key="register.customer.body.state"/></label>
												<input type="text" name="state" />
												<span class="required-icon required-blue"></span>
												<span class="validation-msg"></span>
											</div>
										<% } %>
									<% } else { %>
										<input type="hidden" name="state" value="-----"/>
									<% } %>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.country"/></label>
											<select name="countryId" class="country" onchange="javascript:changeCountry();">
												<option value="-1"><kk:msg  key="register.customer.body.select"/></option>
													<%com.konakart.appif.CountryIf selectedCountry = kkEng.getCustomerMgr().getSelectedCountry(); %>
													<% com.konakart.appif.CountryIf[] countries = kkEng.getAllCountries();%>
													<%if (countries != null){%>
													    <%for ( int i = 0; i < countries.length; i++)	{%>
													        <%com.konakart.appif.CountryIf country = countries[i];%>
															<%if (selectedCountry != null && country.getId() == selectedCountry.getId()){ %>
																<option selected="selected" value="<%=Integer.toString(country.getId())%>"><%=country.getName()%></option>
															<% } else { %>
																<option value="<%=Integer.toString(country.getId())%>"><%=country.getName()%></option>
															<% } %>														                 
														<% } %>           
													<% } %>																
											</select>
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
								</div>
							</div>
							<div class="form-section">
								<h3><kk:msg  key="register.customer.body.contact.info"/></h3>
								<div class="form-section-fields">
									<div class="form-section-divider"></div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.tel.number"/></label>
										<input type="text" value="<s:property value="telephoneNumber" />" name="telephoneNumber" />
										<span class="required-icon required-blue"></span>
										<span class="validation-msg"></span>
									</div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.tel.number1"/></label>
										<input type="text" value="<s:property value="telephoneNumber1" />" name="telephoneNumber1" />
										<span class="validation-msg"></span>
									</div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.fax.number"/></label>
										<input type="text" value="<s:property value="faxNumber" />" name="faxNumber" />
										<span class="validation-msg"></span>
									</div>
								</div>
							</div>
							<div class="form-section">
								<h3><kk:msg  key="register.customer.body.subscriptions"/></h3>
								<div class="form-section-fields">
									<div class="form-section-divider"></div>
									<div class="form-input">
										<label><kk:msg  key="register.customer.body.newsletter"/></label>
										<s:checkbox name="newsletterBool" theme="simple"/>
									</div>
								</div>
							</div>
							<%if (!allowNoRegister){ %>
								<div class="form-section">
									<h3><kk:msg  key="register.customer.body.password"/></h3>
									<div class="form-section-fields">
										<div class="form-section-divider"></div>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.password"/></label>
											<input type="password" name="password" id="password" autocomplete="off" value=""/>
											<span class="required-icon required-blue"></span>
											<span class="validation-msg"></span>
										</div>
										<div class="form-input">
											<label><kk:msg  key="register.customer.body.confirm.password"/></label>
											<input type="password" name="passwordConfirmation" autocomplete="off" value=""/>
											<span class="required-icon required-blue"></span>
											<span class="validation-msg"></span>
										</div>
									</div>
								</div>
							<% } %>
							<div class="form-buttons">
								<a onclick="javascript:formValidate('form1', 'continue-button');" id="continue-button" class="button small-rounded-corners"><span><kk:msg  key="common.continue"/></span></a>
							</div>
							<input type="hidden" id="countryChange" name="countryChange" value="0"/>
						</form>
			    	</div>
	    		</div>



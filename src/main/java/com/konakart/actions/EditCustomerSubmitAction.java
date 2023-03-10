//
// (c) 2006 DS Data Systems UK Ltd, All rights reserved.
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

package com.konakart.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.app.Customer;
import com.konakart.app.CustomerTag;
import com.konakart.appif.CustomerIf;

/**
 * Gets called after submitting the edit customer page.
 */
public class EditCustomerSubmitAction extends BaseAction
{

    private static final long serialVersionUID = 1L;

    private String gender;

    private String firstName;

    private String lastName;

    private String birthDateString;

    private String emailAddr;

    private String telephoneNumber;

    private String telephoneNumber1;

    private String faxNumber;

    private String customerCustom1;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {
            int custId;

            KKAppEng kkAppEng = this.getKKAppEng(request, response);

            custId = this.loggedIn(request, response, kkAppEng, "EditCustomer");

            // Check to see whether the user is logged in
            if (custId < 0)
            {
                return KKLOGIN;
            }

            // Ensure we are using the correct protocol. Redirect if not.
            String redirForward = checkSSL(kkAppEng, request, custId, /* forceSSL */false);
            if (redirForward != null)
            {
                setupResponseForSSLRedirect(response, redirForward);
                return null;
            }

            // Copy the inputs from the form to a customer object
            CustomerIf cust = new Customer();
            cust.setGender(getGender());
            cust.setFirstName(getFirstName());
            cust.setLastName(getLastName());
            cust.setEmailAddr(getEmailAddr());
            cust.setFaxNumber(getFaxNumber());
            cust.setTelephoneNumber(getTelephoneNumber());
            cust.setTelephoneNumber1(getTelephoneNumber1());
            cust.setId(kkAppEng.getCustomerMgr().getCurrentCustomer().getId());
            cust.setCustom1(getCustomerCustom1());

            CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
            if (currentCustomer != null)
            {
                cust.setType(currentCustomer.getType());
            }

            // Set the date
            if (getBirthDateString() != null && !getBirthDateString().equals(""))
            {
                SimpleDateFormat sdf = new SimpleDateFormat(kkAppEng.getMsg("date.format"));
                Date d = sdf.parse(getBirthDateString());
                if (d != null)
                {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(d);
                    cust.setBirthDate(gc);

                    // Set the customer tag
                    CustomerTag ct = new CustomerTag();
                    ct.setValueAsDate(d);
                    ct.setName(TAG_BIRTH_DATE);
                    kkAppEng.getCustomerTagMgr().insertCustomerTag(ct);
                }
            }

            // Set the IS_MALE customer tag for this customer
            CustomerTag ct = new CustomerTag();
            ct.setName(TAG_IS_MALE);
            ct.setValueAsBoolean(false);
            if (getGender() != null && getGender().equalsIgnoreCase("m"))
            {
                ct.setValueAsBoolean(true);
            }
            kkAppEng.getCustomerTagMgr().insertCustomerTag(ct);

            // Call the engine registration method
            try
            {
                kkAppEng.getCustomerMgr().editCustomer(cust);
            } catch (Exception e)
            {
                addActionError(kkAppEng.getMsg("edit.customer.body.user.exists"));
                return "ApplicationError";
            }

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }
    }

    /**
     * @return the gender
     */
    public String getGender()
    {
        return gender;
    }

    /**
     * @param gender
     *            the gender to set
     */
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the birthDateString
     */
    public String getBirthDateString()
    {
        return birthDateString;
    }

    /**
     * @param birthDateString
     *            the birthDateString to set
     */
    public void setBirthDateString(String birthDateString)
    {
        this.birthDateString = birthDateString;
    }

    /**
     * @return the emailAddr
     */
    public String getEmailAddr()
    {
        return emailAddr;
    }

    /**
     * @param emailAddr
     *            the emailAddr to set
     */
    public void setEmailAddr(String emailAddr)
    {
        this.emailAddr = emailAddr;
    }

    /**
     * @return the telephoneNumber
     */
    public String getTelephoneNumber()
    {
        return telephoneNumber;
    }

    /**
     * @param telephoneNumber
     *            the telephoneNumber to set
     */
    public void setTelephoneNumber(String telephoneNumber)
    {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * @return the telephoneNumber1
     */
    public String getTelephoneNumber1()
    {
        return telephoneNumber1;
    }

    /**
     * @param telephoneNumber1
     *            the telephoneNumber1 to set
     */
    public void setTelephoneNumber1(String telephoneNumber1)
    {
        this.telephoneNumber1 = telephoneNumber1;
    }

    /**
     * @return the faxNumber
     */
    public String getFaxNumber()
    {
        return faxNumber;
    }

    /**
     * @param faxNumber
     *            the faxNumber to set
     */
    public void setFaxNumber(String faxNumber)
    {
        this.faxNumber = faxNumber;
    }

    /**
     * @return the customerCustom1
     */
    public String getCustomerCustom1()
    {
        return customerCustom1;
    }

    /**
     * @param customerCustom1
     *            the customerCustom1 to set
     */
    public void setCustomerCustom1(String customerCustom1)
    {
        this.customerCustom1 = customerCustom1;
    }

}

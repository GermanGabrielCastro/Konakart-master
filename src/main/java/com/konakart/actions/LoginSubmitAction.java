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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.CustomerTag;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.CustomerTagIf;

/**
 * Gets called after submitting the login page.
 */
public class LoginSubmitAction extends BaseAction
{
    private static final long serialVersionUID = 1L;

    private String emailAddr;

    private String password;
    
    private String action;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {
            int custId;

            KKAppEng kkAppEng = this.getKKAppEng(request, response);

            custId = this.loggedIn(request, response, kkAppEng, null);

            // Ensure we are using the correct protocol. Redirect if not.
            String redirForward = checkSSL(kkAppEng, request, custId, /* forceSSL */true);
            if (redirForward != null)
            {
                setupResponseForSSLRedirect(response, redirForward);
                return null;
            }

            /*
             * Don't log in if we are already logged in. However, do update the UI with the
             * customer's basket, orders etc. The KonaKart application doesn't call this action if
             * the user is already logged in. However in the case where products are put in the
             * basket using the API and then forwarded to the application to do the checkout, the
             * customer may already be logged in and so we don't want to log in twice but we do want
             * to update the basket.
             */
            if (custId >= 0)
            {
                log.debug("User already logged in");

                // Refresh the data relevant to the customer such as his basked and recent orders.
                // The refreshCustomerCachedData() method calls the following methods :
                // this.kkAppEng.getBasketMgr().getBasketItemsPerCustomer();
                // this.kkAppEng.getOrderMgr().populateCustomerOrders();
                // this.kkAppEng.getProductMgr().fetchOrderHistoryArray();
                kkAppEng.getCustomerMgr().refreshCustomerCachedData();

                return SUCCESS;
            }

            // Get recently viewed products before logging in
            CustomerTagIf prodsViewedTagGuest = kkAppEng.getCustomerTagMgr().getCustomerTag(
                    TAG_PRODUCTS_VIEWED);

            String sessionId = kkAppEng.getCustomerMgr().login(getEmailAddr(), getPassword());
            if (sessionId == null)
            {
                addActionError(kkAppEng.getMsg("login.body.login.error"));
                return "LoginSubmitError";
            }
            kkAppEng.getNav().set(kkAppEng.getMsg("header.my.account"), request);

            /*
             * Manage Cookies
             */
            manageCookies(request, response, kkAppEng);

            // Insert event
            insertCustomerEvent(kkAppEng, ACTION_CUSTOMER_LOGIN);

            // Set last login customer tag
            CustomerTag ct = new CustomerTag();
            ct.setValueAsDate(new Date());
            ct.setName(TAG_LOGIN_DATE);
            kkAppEng.getCustomerTagMgr().insertCustomerTag(ct);

            // Set recently viewed products for the logged in customer if changed as guest
            CustomerTagIf prodsViewedTagCust = kkAppEng.getCustomerTagMgr().getCustomerTag(
                    TAG_PRODUCTS_VIEWED);
            updateRecentlyViewedProducts(kkAppEng, prodsViewedTagGuest, prodsViewedTagCust);
            
            // Ensure that the current customer has his addresses populated
            kkAppEng.getCustomerMgr().populateCurrentCustomerAddresses(/* force */false);

            /*
             * If we had to logon because we needed to in order to do something else such as to
             * write a review; then we need to go back to the page to let us write the review.
             */
            if (kkAppEng.getForwardAfterLogin() != null)
            {
                this.action = kkAppEng.getForwardAfterLogin();
                kkAppEng.setForwardAfterLogin(null);
                return "redirect";
            }

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }

    }

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     * 
     * @param request
     * @param response
     * @param kkAppEng
     * @throws KKException
     * @throws KKAppException
     */
    private void manageCookies(HttpServletRequest request, HttpServletResponse response,
            KKAppEng kkAppEng) throws KKException, KKAppException
    {
        if (!kkAppEng.isKkCookieEnabled())
        {
            return;
        }

        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer != null)
        {
            setKKCookie(CUSTOMER_NAME,
                    currentCustomer.getFirstName() + " " + currentCustomer.getLastName(), request,
                    response, kkAppEng);
        }

        /*
         * Get customer preferences from customer tags. If the tag value exists, then set the
         * preference in the manager and set the cookie.
         */
        String prodPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(
                TAG_PROD_PAGE_SIZE);
        if (prodPageSizeStr != null && prodPageSizeStr.length() > 0)
        {
            int prodPageSize = Integer.parseInt(prodPageSizeStr);
            kkAppEng.getProductMgr().setMaxDisplaySearchResults(prodPageSize);
            setKKCookie(TAG_PROD_PAGE_SIZE, prodPageSizeStr, request, response, kkAppEng);
        }
        String orderPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(
                TAG_ORDER_PAGE_SIZE);
        if (orderPageSizeStr != null && orderPageSizeStr.length() > 0)
        {
            int orderPageSize = Integer.parseInt(orderPageSizeStr);
            kkAppEng.getOrderMgr().setPageSize(orderPageSize);
            setKKCookie(TAG_ORDER_PAGE_SIZE, orderPageSizeStr, request, response, kkAppEng);
        }
        String reviewPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(
                TAG_REVIEW_PAGE_SIZE);
        if (reviewPageSizeStr != null && reviewPageSizeStr.length() > 0)
        {
            int reviewPageSize = Integer.parseInt(reviewPageSizeStr);
            kkAppEng.getReviewMgr().setPageSize(reviewPageSize);
            setKKCookie(TAG_REVIEW_PAGE_SIZE, reviewPageSizeStr, request, response, kkAppEng);
        }

        /*
         * Call class where you can place custom code
         */
        CustomCookieAction cca = new CustomCookieAction();
        cca.manageCookiesAfterLogin(request, response, kkAppEng);

    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * @return the emailAddr
     */
    public String getEmailAddr()
    {
        return emailAddr;
    }

    /**
     * @param emailAddr the emailAddr to set
     */
    public void setEmailAddr(String emailAddr)
    {
        this.emailAddr = emailAddr;
    }
}

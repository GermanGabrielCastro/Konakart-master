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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.json.OptionJson;
import com.konakart.al.json.OrderProductJson;
import com.konakart.al.json.OrderTotalJson;
import com.konakart.appif.OptionIf;
import com.konakart.appif.OrderIf;
import com.konakart.appif.OrderProductIf;
import com.konakart.appif.OrderTotalIf;

/**
 * Gets called before viewing the checkout delivery page.
 */
public class CheckoutOnePageRefreshAction extends BaseAction
{
    private static final long serialVersionUID = 1L;

    private String shipping;

    private String payment;

    private String couponCode;

    private String giftCertCode;

    private String rewardPoints;

    private String deliveryAddrId;

    private String billingAddrId;

    private OrderTotalJson[] otArray;

    private OrderProductJson[] opArray;

    private String formattedDeliveryAddr;

    private String formattedBillingAddr;

    private String timeout;
    
    private boolean displayPriceWithTax;
    
    private String qtyMsg;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {
            int custId = -1;

            KKAppEng kkAppEng = this.getKKAppEng(request, response);

            custId = this.loggedIn(request, response, kkAppEng, "Checkout");
            if (custId < 0)
            {
                timeout = "true";
                return SUCCESS;
            }

            // Ensure we are using the correct protocol. Redirect if not.
            String redirForward = checkSSL(kkAppEng, request, custId, /* forceSSL */false);
            if (redirForward != null)
            {
                setupResponseForSSLRedirect(response, redirForward);
                return null;
            }

            OrderIf checkoutOrder = kkAppEng.getOrderMgr().getCheckoutOrder();
            if (checkoutOrder == null)
            {
                throw new KKAppException("A Checkout Order does not exist");
            }

            // Set the coupon code
            if (couponCode != null)
            {
                checkoutOrder.setCouponCode(couponCode);
                kkAppEng.getOrderMgr().setCouponCode(couponCode);
            }

            // Set the gift certificate code
            if (giftCertCode != null)
            {
                checkoutOrder.setGiftCertCode(giftCertCode);
                kkAppEng.getOrderMgr().setGiftCertCode(giftCertCode);
            }

            // Set the reward points
            if (kkAppEng.getRewardPointMgr().isEnabled())
            {
                int pointsAvailable = kkAppEng.getRewardPointMgr().pointsAvailable();
                if (rewardPoints != null && rewardPoints.length() > 0 && pointsAvailable > 0)
                {
                    try
                    {
                        checkoutOrder.setPointsRedeemed(Integer.parseInt(rewardPoints));
                        kkAppEng.getOrderMgr().setRewardPoints(Integer.parseInt(rewardPoints));
                    } catch (Exception e)
                    {
                    }
                }
            } else
            {
                checkoutOrder.setPointsRedeemed(0);
                kkAppEng.getOrderMgr().setRewardPoints(0);
            }

            // Attach the shipping quote to the order
            if (shipping != null)
            {
                kkAppEng.getOrderMgr().addShippingQuoteToOrder(shipping);
            }

            // Attach the payment detail to the order
            if (payment != null)
            {
                kkAppEng.getOrderMgr().addPaymentDetailsToOrder(payment);
            }

            // Change the delivery address
            if (deliveryAddrId != null)
            {
                int addrId = -1;
                try
                {
                    addrId = Integer.parseInt(deliveryAddrId);
                } catch (Exception e)
                {
                }
                kkAppEng.getOrderMgr().setCheckoutOrderShippingAddress(addrId);
            }

            // Change the billing address
            if (billingAddrId != null)
            {
                int addrId = -1;
                try
                {
                    addrId = Integer.parseInt(billingAddrId);
                } catch (Exception e)
                {
                }
                kkAppEng.getOrderMgr().setCheckoutOrderBillingAddress(addrId);
            }

            // Call the engine to get the Order Totals
            kkAppEng.getOrderMgr().populateCheckoutOrderWithOrderTotals();

            if (checkoutOrder.getOrderTotals() != null && checkoutOrder.getOrderTotals().length > 0)
            {
                otArray = new OrderTotalJson[checkoutOrder.getOrderTotals().length];
                for (int i = 0; i < checkoutOrder.getOrderTotals().length; i++)
                {
                    OrderTotalIf ot = checkoutOrder.getOrderTotals()[i];
                    OrderTotalJson otClone = new OrderTotalJson();
                    otArray[i] = otClone;
                    otClone.setClassName(ot.getClassName());
                    otClone.setTitle(ot.getTitle());
                    otClone.setText(ot.getText());
                    if ((ot.getClassName().equals("ot_reward_points"))
                            || (ot.getClassName().equals("ot_free_product")))
                    {
                        otClone.setValue(ot.getValue().toPlainString());
                    } else
                    {
                        otClone.setValue(kkAppEng.formatPrice(ot.getValue()));
                    }
                }
            }

            if (checkoutOrder.getOrderProducts() != null
                    && checkoutOrder.getOrderProducts().length > 0)
            {
                opArray = new OrderProductJson[checkoutOrder.getOrderProducts().length];
                for (int i = 0; i < checkoutOrder.getOrderProducts().length; i++)
                {
                    OrderProductIf op = checkoutOrder.getOrderProducts()[i];
                    OrderProductJson opClone = new OrderProductJson();
                    opArray[i] = opClone;
                    opClone.setFormattedFinalPriceExTax(kkAppEng.formatPrice(op
                            .getFinalPriceExTax()));
                    opClone.setFormattedFinalPriceIncTax(kkAppEng.formatPrice(op
                            .getFinalPriceIncTax()));
                    opClone.setFormattedTaxRate((op.getTaxRate().setScale(2,
                            java.math.BigDecimal.ROUND_HALF_UP)).toPlainString());
                    opClone.setName(op.getName());
                    opClone.setQuantity(op.getQuantity());
                    opClone.setProductId(op.getProductId());

                    if (op.getOpts() != null && op.getOpts().length > 0)
                    {
                        OptionJson[] optArray = new OptionJson[op.getOpts().length];
                        for (int j = 0; j < op.getOpts().length; j++)
                        {
                            OptionIf opt = op.getOpts()[j];
                            OptionJson optClone = new OptionJson();
                            optClone.setName(opt.getName());
                            optClone.setQuantity(opt.getQuantity());
                            optClone.setType(opt.getType());
                            optClone.setValue(opt.getValue());
                            optArray[j] = optClone;
                        }
                        opClone.setOpts(optArray);
                    }
                }
            }

            formattedDeliveryAddr = kkAppEng.removeCData(checkoutOrder
                    .getDeliveryFormattedAddress());
            formattedBillingAddr = kkAppEng.removeCData(checkoutOrder.getBillingFormattedAddress());
            
            displayPriceWithTax = kkAppEng.displayPriceWithTax();
            
            // Messages
            qtyMsg = kkAppEng.getMsg("common.quantity");

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }

    }

    /**
     * @return the shipping
     */
    public String getShipping()
    {
        return shipping;
    }

    /**
     * @param shipping
     *            the shipping to set
     */
    public void setShipping(String shipping)
    {
        this.shipping = shipping;
    }

    /**
     * @return the couponCode
     */
    public String getCouponCode()
    {
        return couponCode;
    }

    /**
     * @param couponCode
     *            the couponCode to set
     */
    public void setCouponCode(String couponCode)
    {
        this.couponCode = couponCode;
    }

    /**
     * @return the rewardPoints
     */
    public String getRewardPoints()
    {
        return rewardPoints;
    }

    /**
     * @param rewardPoints
     *            the rewardPoints to set
     */
    public void setRewardPoints(String rewardPoints)
    {
        this.rewardPoints = rewardPoints;
    }

    /**
     * @return the otArray
     */
    public OrderTotalJson[] getOtArray()
    {
        return otArray;
    }

    /**
     * @param otArray
     *            the otArray to set
     */
    public void setOtArray(OrderTotalJson[] otArray)
    {
        this.otArray = otArray;
    }

    /**
     * @return the giftCertCode
     */
    public String getGiftCertCode()
    {
        return giftCertCode;
    }

    /**
     * @param giftCertCode
     *            the giftCertCode to set
     */
    public void setGiftCertCode(String giftCertCode)
    {
        this.giftCertCode = giftCertCode;
    }

    /**
     * @return the payment
     */
    public String getPayment()
    {
        return payment;
    }

    /**
     * @param payment
     *            the payment to set
     */
    public void setPayment(String payment)
    {
        this.payment = payment;
    }

    /**
     * @return the deliveryAddrId
     */
    public String getDeliveryAddrId()
    {
        return deliveryAddrId;
    }

    /**
     * @param deliveryAddrId
     *            the deliveryAddrId to set
     */
    public void setDeliveryAddrId(String deliveryAddrId)
    {
        this.deliveryAddrId = deliveryAddrId;
    }

    /**
     * @return the billingAddrId
     */
    public String getBillingAddrId()
    {
        return billingAddrId;
    }

    /**
     * @param billingAddrId
     *            the billingAddrId to set
     */
    public void setBillingAddrId(String billingAddrId)
    {
        this.billingAddrId = billingAddrId;
    }

    /**
     * @return the formattedDeliveryAddr
     */
    public String getFormattedDeliveryAddr()
    {
        return formattedDeliveryAddr;
    }

    /**
     * @param formattedDeliveryAddr
     *            the formattedDeliveryAddr to set
     */
    public void setFormattedDeliveryAddr(String formattedDeliveryAddr)
    {
        this.formattedDeliveryAddr = formattedDeliveryAddr;
    }

    /**
     * @return the formattedBillingAddr
     */
    public String getFormattedBillingAddr()
    {
        return formattedBillingAddr;
    }

    /**
     * @param formattedBillingAddr
     *            the formattedBillingAddr to set
     */
    public void setFormattedBillingAddr(String formattedBillingAddr)
    {
        this.formattedBillingAddr = formattedBillingAddr;
    }

    /**
     * @return the timeout
     */
    public String getTimeout()
    {
        return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(String timeout)
    {
        this.timeout = timeout;
    }

    /**
     * @return the opArray
     */
    public OrderProductJson[] getOpArray()
    {
        return opArray;
    }

    /**
     * @param opArray
     *            the opArray to set
     */
    public void setOpArray(OrderProductJson[] opArray)
    {
        this.opArray = opArray;
    }

    /**
     * @return the displayPriceWithTax
     */
    public boolean isDisplayPriceWithTax()
    {
        return displayPriceWithTax;
    }

    /**
     * @param displayPriceWithTax the displayPriceWithTax to set
     */
    public void setDisplayPriceWithTax(boolean displayPriceWithTax)
    {
        this.displayPriceWithTax = displayPriceWithTax;
    }

    /**
     * @return the qtyMsg
     */
    public String getQtyMsg()
    {
        return qtyMsg;
    }

    /**
     * @param qtyMsg the qtyMsg to set
     */
    public void setQtyMsg(String qtyMsg)
    {
        this.qtyMsg = qtyMsg;
    }

}

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

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.al.WishListUIItem;
import com.konakart.appif.WishListIf;
import com.konakart.appif.WishListItemIf;

/**
 * Called to display the wish list items normally after searching for a gift registry
 */
public class ShowGiftRegistryItemsAction extends BaseAction
{

    private static final long serialVersionUID = 1L;

    private ArrayList<WishListUIItem> itemList = new ArrayList<WishListUIItem>();

    private BigDecimal finalPriceIncTax;

    private BigDecimal finalPriceExTax;

    private int id;

    private String listName;

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
            String redirForward = checkSSL(kkAppEng, request, custId,/* forceSSL */false);
            if (redirForward != null)
            {
                setupResponseForSSLRedirect(response, redirForward);
                return null;
            }

            /*
             * If there are items in the wish list, then we have to create a list of WishListUIItem
             * objects and populate them since these are the objects that we will use to display the
             * wish list items on the screen.
             * 
             * To find the correct wish list we look for a parameter and then an attribute
             */

            // Find the wish list
            int wishListIdInt = -1;
            String wishListIdStr = request.getParameter("wishListId");
            if (wishListIdStr != null)
            {
                try
                {
                    wishListIdInt = new Integer(wishListIdStr).intValue();
                } catch (Exception e)
                {
                    return WELCOME;
                }
            }

            if (wishListIdInt == -1)
            {
                Integer wishListIdIntg = null;
                if (runningInPortal(kkAppEng) && request.getSession() != null)
                {
                    wishListIdIntg = (Integer) request.getSession().getAttribute("wishListId");
                    request.getSession().removeAttribute("wishListId");
                } else
                {
                    wishListIdIntg = (Integer) request.getAttribute("wishListId");
                }

                if (wishListIdIntg != null)
                {
                    wishListIdInt = wishListIdIntg.intValue();
                }
            }

            WishListIf wishList = kkAppEng.getWishListMgr()
                    .fetchWishListWithoutItems(wishListIdInt);
            kkAppEng.getWishListMgr().navigateCurrentWishListItems(
                    kkAppEng.getWishListMgr().getNavStart());

            if (wishList != null && kkAppEng.getWishListMgr().getCurrentWishListItems() != null)
            {
                WishListItemIf[] items = kkAppEng.getWishListMgr().getCurrentWishListItems();
                int length = items.length;

                /*
                 * We need to make this check since we always fetch an extra item in order to decide
                 * whether to enable the next button or not.
                 */
                if (length > kkAppEng.getWishListMgr().getMaxItemRows())
                {
                    length = kkAppEng.getWishListMgr().getMaxItemRows();
                }

                for (int i = 0; i < length; i++)
                {
                    WishListItemIf wli = items[i];
                    if (wli != null && wli.getProduct() != null)
                    {
                        // Create a WishListUIItem
                        WishListUIItem item = new WishListUIItem(wli.getId(), wli.getProduct()
                                .getId(), wli.getProduct().getName(), kkAppEng.getProdImageBase(wli
                                .getProduct()) + "_1_small.jpg", wli.getFinalPriceExTax(),
                                wli.getFinalPriceIncTax(), wli.getPriority(),
                                wli.getQuantityDesired(), wli.getQuantityReceived(),
                                wli.getComments());
                        // Set the options of the new WishListUIItem
                        if (wli.getOpts() != null && wli.getOpts().length > 0)
                        {
                            String[] optNameArray = new String[wli.getOpts().length];
                            for (int j = 0; j < wli.getOpts().length; j++)
                            {
                                if (wli.getOpts()[j] != null)
                                {
                                    optNameArray[j] = wli.getOpts()[j].getName() + " "
                                            + wli.getOpts()[j].getValue();
                                } else
                                {
                                    optNameArray[j] = "";
                                }
                            }
                            item.setOptNameArray(optNameArray);
                        }

                        // Add the new item to the list
                        getItemList().add(item);
                    }
                }
                setFinalPriceExTax(wishList.getFinalPriceExTax());
                setFinalPriceIncTax(wishList.getFinalPriceIncTax());
                setId(wishList.getId());
                setListName(wishList.getName());
            }
            kkAppEng.getNav().set(kkAppEng.getMsg("header.weddinglist.contents"), request);

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }
    }

    /**
     * @return the itemList
     */
    public ArrayList<WishListUIItem> getItemList()
    {
        return itemList;
    }

    /**
     * @param itemList
     *            the itemList to set
     */
    public void setItemList(ArrayList<WishListUIItem> itemList)
    {
        this.itemList = itemList;
    }

    /**
     * @return the finalPriceIncTax
     */
    public BigDecimal getFinalPriceIncTax()
    {
        return finalPriceIncTax;
    }

    /**
     * @param finalPriceIncTax
     *            the finalPriceIncTax to set
     */
    public void setFinalPriceIncTax(BigDecimal finalPriceIncTax)
    {
        this.finalPriceIncTax = finalPriceIncTax;
    }

    /**
     * @return the finalPriceExTax
     */
    public BigDecimal getFinalPriceExTax()
    {
        return finalPriceExTax;
    }

    /**
     * @param finalPriceExTax
     *            the finalPriceExTax to set
     */
    public void setFinalPriceExTax(BigDecimal finalPriceExTax)
    {
        this.finalPriceExTax = finalPriceExTax;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return the listName
     */
    public String getListName()
    {
        return listName;
    }

    /**
     * @param listName
     *            the listName to set
     */
    public void setListName(String listName)
    {
        this.listName = listName;
    }
}

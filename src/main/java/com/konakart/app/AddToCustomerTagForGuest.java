package com.konakart.app;
//Edit for VERITA- 177
/**
 *  The KonaKart Custom Engine - AddToCustomerTagForGuest - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class AddToCustomerTagForGuest
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public AddToCustomerTagForGuest(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public void addToCustomerTagForGuest(int customerId, String tagName, int tagValue) throws KKException
     {
         kkEng.addToCustomerTagForGuest(customerId, tagName, tagValue);
     }
}
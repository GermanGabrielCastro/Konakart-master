package com.konakart.app;
//Edit for VERITA- 65
/**
 *  The KonaKart Custom Engine - AddToCustomerTag - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class AddToCustomerTag
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public AddToCustomerTag(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public void addToCustomerTag(String sessionId, String tagName, int tagValue) throws KKException
     {
         kkEng.addToCustomerTag(sessionId, tagName, tagValue);
     }
}

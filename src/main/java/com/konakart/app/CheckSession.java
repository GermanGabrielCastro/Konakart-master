package com.konakart.app;
//Edit for VERITA- 161
/**
 *  The KonaKart Custom Engine - CheckSession - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class CheckSession
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public CheckSession(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public int checkSession(String sessionId) throws KKException
     {
         return kkEng.checkSession(sessionId);
     }
}
package com.obelieve.frame;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneUtils {
    /**
     * 判断国家号码
     * @param countryCode
     * @param num
     * @return
     */
    public static boolean isMatched(int countryCode, CharSequence num) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse("+" + countryCode + num, String.valueOf(countryCode));
            return phoneUtil.isValidNumber(numberProto) && phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.MOBILE;
        } catch (Exception e) {
        }
        return false;
    }
}

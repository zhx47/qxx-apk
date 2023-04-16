package com.example.qde.utils;

public class HttpUtils {
    public static String regexMatch(String str, String str2, String str3) {
        int indexOf;
        int length = (str2 == null || str2.isEmpty() || (indexOf = str.indexOf(str2)) <= -1) ? 0 : str2.length() + indexOf;
        int indexOf2 = str.indexOf(str3, length);
        if (indexOf2 < 0 || str3 == null || str3.isEmpty()) {
            indexOf2 = str.length();
        }
        return str.substring(length, indexOf2);
    }

    public static String detachmentJavaScript(String str, String str2) {
        regexMatch(str, "name=\"jschl_vc\" value=\"", "\"");
        regexMatch(str, "name=\"pass\" value=\"", "\"");
        regexMatch(str, "name=\"r\" value=\"", "\"");
        String rg_n27284 = MyUtilitys.rg_n27284(str, String.format("id=\"%s\">(.*?)<", MyUtilitys.rg_n27233(str, "k = '", "';")), 1);
        String rg_n21218 = rg_wbxl.rg_n21218(rg_wbxl.rg_n21218(str, "\\(function\\(p\\)[\\s\\S]*?\\);", "t.charCodeAt(" + MyUtilitys.rg_n27284(str, "p.*?\\}\\(\\+\\((.*?\\))\\)", 1) + "));", false), "p =[\\s\\S]*?;", String.format("p=(%s);", rg_n27284), false);
        String rg_n272842 = MyUtilitys.rg_n27284(rg_n21218, "setTimeout.*?\n([\\s\\S]*?return[\\s\\S]*?\\};)", 1);
        String rg_n272843 = MyUtilitys.rg_n27284(rg_n21218, "challenge-form[\\s\\S]*?;([\\s\\S]*?)a.value", 1);
        String rg_n27233 = MyUtilitys.rg_n27233(rg_n21218, "a.value = ", "'");
        String format = String.format("t =%s;r = t.match(/https?:\\/\\//)[0]; t = t.substr(r.length); t = t.substr(0,t.length-1); ", "\"" + str2 + "\"");
        return "function btest(){" + String.format("%s\n%s\n%s\n%s", rg_n272842, format, rg_n272843, "return " + rg_n27233) + "}";
    }
}
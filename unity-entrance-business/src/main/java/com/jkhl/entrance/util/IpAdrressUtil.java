//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jkhl.entrance.util;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class IpAdrressUtil {
    public IpAdrressUtil() {
    }

    public static String getIpAdrress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            int index = XFor.indexOf(",");
            return index != -1 ? XFor.substring(0, index) : XFor;
        } else {
            XFor = Xip;
            if (StringUtils.isNotEmpty(Xip) && !"unKnown".equalsIgnoreCase(Xip)) {
                return Xip;
            } else {
                if (StringUtils.isBlank(Xip) || "unknown".equalsIgnoreCase(Xip)) {
                    XFor = request.getHeader("Proxy-Client-IP");
                }

                if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("WL-Proxy-Client-IP");
                }

                if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("HTTP_CLIENT_IP");
                }

                if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
                }

                if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                    XFor = request.getRemoteAddr();
                }

                return XFor;
            }
        }
    }
}

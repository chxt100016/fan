package com.chxt.client.router.model;

import com.chxt.domain.router.model.vo.DeviceSpeedVO;
import com.chxt.domain.utils.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RouterClient {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "31415926";
    private static final Pattern PARA_PATTERN = Pattern.compile("<ParaName>(.*?)</ParaName>\\s*<ParaValue>(.*?)</ParaValue>");

    /**
     * Step 1+2: Get tokens and login
     * @return SID cookie value for subsequent requests
     */
    public String login(String routerUrl) {
        // Step 1: Get login token
        String tokenUrl = routerUrl + "/?_type=loginsceneData&_tag=login_token_json";
        Http tokenHttp = Http.uri(tokenUrl)
                .header("Accept", "*/*")
                .header("Referer", routerUrl + "/")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36")
                .doGet();

        if (!tokenHttp.isSuccess()) {
            throw new RuntimeException("获取路由器 token 失败");
        }

        String tokenResult = tokenHttp.result();
        Map<String, String> tokenMap = parseSimpleJson(tokenResult);
        String logintoken = tokenMap.get("logintoken");
        String sessionToken = tokenMap.get("_sessionToken");

        if (logintoken == null || sessionToken == null) {
            throw new RuntimeException("解析路由器 token 失败: " + tokenResult);
        }

        // Step 2: Login with SHA256 encrypted password
        String encryptedPwd = sha256(PASSWORD + logintoken);
        String loginUrl = routerUrl + "/?_type=loginData&_tag=login_entry";

        Http loginHttp = Http.uri(loginUrl)
                .formEncoded()
                .entity("Username", USERNAME)
                .entity("Password", encryptedPwd)
                .entity("action", "login")
                .entity("Frm_Logintoken", "")
                .entity("captchaCode", "")
                .entity("_sessionTOKEN", sessionToken)
                .header("Accept", "*/*")
                .header("Origin", routerUrl)
                .header("Referer", routerUrl + "/")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0")
                .doPost();

        // Extract SID from Set-Cookie header
        String setCookie = loginHttp.getResponseHeader("Set-Cookie");
        if (setCookie == null) {
            // Try to get from response body if cookie not found
            throw new RuntimeException("登录失败，未找到 SID cookie");
        }

        String sid = extractSid(setCookie);
        if (sid == null) {
            throw new RuntimeException("登录失败，无法解析 SID: " + setCookie);
        }

        log.info("路由器登录成功, SID={}", sid);
        return sid;
    }

    /**
     * Step 3: Get all LAN devices
     * @return map of MAC -> DevName (DevName only exists in list interface)
     */
    public Map<String, String> getDeviceList(String routerUrl, String sid) {
        String url = routerUrl + "/?_type=vueData&_tag=localnet_lan_info_lua&_=1775655202251";
        String xml = doRouterGet(url, routerUrl, sid, null);

        Map<String, String> deviceNameMap = new HashMap<>();
        List<Map<String, String>> instances = parseXmlInstances(xml);
        for (Map<String, String> instance : instances) {
            String mac = instance.get("MACAddress");
            String devName = instance.get("HostName");
            if (mac != null && !mac.isEmpty()) {
                deviceNameMap.put(mac, devName != null ? devName : "");
            }
        }

        log.info("获取到 {} 个设备", deviceNameMap.size());
        return deviceNameMap;
    }

    /**
     * Step 4: Get device detail by MAC
     * @return DeviceSpeedVO with speed info
     */
    public DeviceSpeedVO getDeviceDetail(String routerUrl, String sid, String mac, String name) {
        String url = routerUrl + "/?_type=vueData&_tag=localnet_lan_detailinfo_lua_no_update_sess&MACAddress=" + mac;
        String xml = doRouterGet(url, routerUrl, sid, mac);

        // Parse device detail - response has OBJ_LANINFO_BYMAC block
        List<Map<String, String>> instances = parseXmlInstances(xml);
        Map<String, String> deviceMap = null;
        for (Map<String, String> instance : instances) {
            if (mac.equals(instance.get("MACAddress")) || mac.equals(instance.get("_InstID"))) {
                deviceMap = instance;
                break;
            }
        }

        // If not found by MAC, try the last instance (OBJ_LANINFO_BYMAC)
        if (deviceMap == null && !instances.isEmpty()) {
            deviceMap = instances.get(instances.size() - 1);
        }

        if (deviceMap == null) {
            log.warn("未找到设备 {} 的详情", mac);
            return null;
        }

        DeviceSpeedVO vo = new DeviceSpeedVO();
        vo.setMac(mac);
        vo.setIp(deviceMap.get("IPAddress"));
        vo.setName(name);
        vo.setDownloadSpeed(formatSpeed(parseLong(deviceMap.get("DownloadSpeed"))));
        vo.setUploadSpeed(formatSpeed(parseLong(deviceMap.get("UploadSpeed"))));
        vo.setBytesReceived(parseLong(deviceMap.get("BytesReceived")));
        vo.setBytesSend(parseLong(deviceMap.get("BytesSend")));
        return vo;
    }

    private String doRouterGet(String url, String routerUrl, String sid, String mac) {
        Http http = Http.uri(url)
                .header("Accept", "*/*")
                .header("Referer", routerUrl + "/")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36")
                .header("Cookie", "_TESTCOOKIESUPPORT=1; sidebarStatus=0; SID=" + sid)
                .doGet();

        if (!http.isSuccess()) {
            throw new RuntimeException("路由器请求失败: " + url + ", status=" + http.getStatusCode());
        }

        return http.result();
    }

    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        // Simple regex-based JSON parsing for flat key-value objects
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 加密失败", e);
        }
    }

    private String extractSid(String setCookie) {
        Pattern pattern = Pattern.compile("SID=([a-f0-9]+)");
        Matcher matcher = pattern.matcher(setCookie);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private List<Map<String, String>> parseXmlInstances(String xml) {
        List<Map<String, String>> instances = new ArrayList<>();
        // Split by Instance blocks
        Pattern instancePattern = Pattern.compile("<Instance>(.*?)</Instance>", Pattern.DOTALL);
        Matcher instanceMatcher = instancePattern.matcher(xml);
        while (instanceMatcher.find()) {
            String instanceContent = instanceMatcher.group(1);
            Map<String, String> params = new HashMap<>();
            Matcher paraMatcher = PARA_PATTERN.matcher(instanceContent);
            while (paraMatcher.find()) {
                String name = paraMatcher.group(1);
                String value = paraMatcher.group(2).replace("&amp;", "&")
                        .replace("&quot;", "\"")
                        .replace("&#32;", " ")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">");
                params.put(name, value);
            }
            if (!params.isEmpty()) {
                instances.add(params);
            }
        }
        return instances;
    }

    private Map<String, String> parseXmlParams(String xml) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = PARA_PATTERN.matcher(xml);
        while (matcher.find()) {
            params.put(matcher.group(1), matcher.group(2));
        }
        return params;
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) return 0L;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Kbps -> kb/s (除以 8), 返回带单位的字符串
     */
    private String formatSpeed(Long kbps) {
        if (kbps == null || kbps == 0) return "0 kb/s";
        return String.format("%.2f kb/s", kbps / 8.0);
    }
}

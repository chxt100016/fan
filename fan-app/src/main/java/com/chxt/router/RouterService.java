package com.chxt.router;

import com.chxt.client.router.model.RouterClient;
import com.chxt.domain.router.model.vo.DeviceSpeedVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RouterService {

    private static final String ROUTER_URL = "http://192.168.1.1";

    @Resource
    private RouterClient routerClient;

    public List<DeviceSpeedVO> getDeviceSpeeds() {
        String sid = routerClient.login(ROUTER_URL);

        Map<String, String> deviceMap = routerClient.getDeviceList(ROUTER_URL, sid);

        List<DeviceSpeedVO> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
            String mac = entry.getKey();
            String name = entry.getValue();
            DeviceSpeedVO detail = routerClient.getDeviceDetail(ROUTER_URL, sid, mac, name);
            if (detail != null) {
                result.add(detail);
            }
        }
        return result.stream()
                .sorted(Comparator.comparing(
                        item -> Double.parseDouble(item.getDownloadSpeed().split(" ")[0]) + Double.parseDouble(item.getUploadSpeed().split(" ")[0]),
                        Comparator.reverseOrder()
                ))
                .toList();
    }
}

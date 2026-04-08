package com.chxt.web.router;

import com.chxt.domain.router.model.vo.DeviceSpeedVO;
import com.chxt.router.RouterService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/router")
public class RouterController {

    @Resource
    private RouterService routerService;

    @GetMapping("/speed")
    public List<DeviceSpeedVO> getDeviceSpeed() {
        return routerService.getDeviceSpeeds();
    }
}

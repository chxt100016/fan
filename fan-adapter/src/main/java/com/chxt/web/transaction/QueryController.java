package com.chxt.web.transaction;

import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.transaction.QueryService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/transaction/query")
public class QueryController {

    @Resource
    private QueryService queryService;

    @PostMapping("/obsidian")
    @SneakyThrows
    public void obsidian(@RequestBody AnalysisParamVO param, HttpServletResponse response) {
        List<String> obsidian = queryService.getObsidian(param);
        String content = String.join(System.lineSeparator(), obsidian);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        String fileName = URLEncoder.encode("transactions.md", StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.md\"; filename*=UTF-8''" + fileName);
        response.setContentLength(bytes.length);

        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(bytes);
            out.flush();
        }
    }



}

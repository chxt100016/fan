package com.chxt.web.transaction;

import com.chxt.domain.obsidian.ListFormat;
import com.chxt.domain.transaction.model.vo.AnalysisParamVO;
import com.chxt.transaction.QueryService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/transaction/query")
public class QueryController {

    @Resource
    private QueryService queryService;

    @PostMapping("/obsidian")
    public void obsidian(@RequestBody AnalysisParamVO param, HttpServletResponse response) {
        List<String> obsidian = queryService.getObsidian(param);
        write(obsidian, response);

    }

    @GetMapping("/range/str")
    public List<String> getRangeStr(@RequestParam("userId") String userId) {
        return this.queryService.getRangeStr(userId);
    }

    @GetMapping("/datashboard")
    public void getDashBoard(@RequestParam("userId") String userId, HttpServletResponse response) {
        ListFormat dashboard = this.queryService.getDashboard(userId);

        this.write(dashboard, response);
    }

    @SneakyThrows
    private void write(List<String> lines, HttpServletResponse response) {
        String content = String.join(System.lineSeparator(), lines);
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

    @SneakyThrows
    private void write(ListFormat dashboard, HttpServletResponse response) {
        Map<String, List<String>> data = dashboard == null || dashboard.data == null
                ? Collections.emptyMap()
                : dashboard.data;

        String zipName = "dashboard.zip";
        String fileName = URLEncoder.encode(zipName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"; filename*=UTF-8''" + fileName);

        try (ZipOutputStream out = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                String entryName = entry.getKey();
                List<String> lines = entry.getValue() == null ? List.of() : entry.getValue();
                String content = String.join(System.lineSeparator(), lines);
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

                out.putNextEntry(new ZipEntry(entryName));
                out.write(bytes);
                out.closeEntry();
            }
            out.finish();
            out.flush();
        }
    }

}

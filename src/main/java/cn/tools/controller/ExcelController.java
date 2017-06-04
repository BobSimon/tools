package cn.tools.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.tools.common.exception.BusinessException;
import cn.tools.service.ExcelService;

@Controller
public class ExcelController {

    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

    @Autowired
    private ExcelService excelService;

    @RequestMapping(value = "import")
    public ResponseEntity<byte[]> upload(@RequestParam("exceFile") MultipartFile file, HttpServletResponse response, Model model) {
        response.setContentType("text/plain; charset=UTF-8");
        // Map<String, String> result = new HashMap<String, String>();
        if (file.isEmpty()) {
            return null;
        } else {
            log.warn("# originalFilename=[{}] , name=[{}] , size=[{}] , contentType=[{}] ", file.getOriginalFilename(), file.getName(), file.getSize(), file.getContentType());
            try {
                byte[] excelBytes = excelService.upload(file.getBytes());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", new String(("解析后_" + file.getOriginalFilename()).getBytes("UTF-8"), "ISO8859-1"));
                return new ResponseEntity<byte[]>(excelBytes, headers, HttpStatus.CREATED);
            } catch (BusinessException e) {
                log.error("# import fail . error message={}", e.getMessage());
                return null;
            } catch (Exception e) {
                log.error("# import fail . error message={}", e.getMessage());
                return null;
            }
        }
    }

}

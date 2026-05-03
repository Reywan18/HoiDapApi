package com.hoidap.hoidapdemo.service.port;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface AiServicePort {
    void processAndSavePdf(MultipartFile file) throws IOException;
    String testSearchDb(String question);
    String chatWithAi(String question);
}

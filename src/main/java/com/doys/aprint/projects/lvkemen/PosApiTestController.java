package com.doys.aprint.projects.lvkemen;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@RequestMapping("/lvkemen/pos_api")
public class PosApiTestController extends BaseController {
    @RequestMapping("/test1")
    public RestResult test1() {
        String responseString;
        // ------------------------------------------------
        try {
            responseString = queryProductCategoryPages();
            logger.info("queryProductCategoryPages = " + responseString);

            responseString = queryProductPages();
            logger.info("queryProductPages = " + responseString);

            responseString = queryProductImagesByProductUid();
            logger.info("queryProductImagesByProductUid = " + responseString);

            ok("result", "调用成功");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    private String queryProductCategoryPages() throws Exception {
        String returnString;
        String content, dataSignature;

        HashMap<String, String> post = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // ------------------------------------------------
        post.put("appId", LvkemenUtil.appId);

        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        dataSignature = LvkemenUtil.encryptToMd5String(content, LvkemenUtil.appKey);

        WebClient webClient = WebClient.create();
        Mono<String> mono = webClient.post()
            .uri(LvkemenUtil.urlQueryProductCategoryPages)
            .header("time-stamp", LvkemenUtil.getTimeStamp())
            .header("data-signature", dataSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(String.class);

        returnString = mono.block();
        return returnString;
    }
    private String queryProductPages() throws Exception {
        String returnString;
        String content, dataSignature;

        HashMap<String, String> post = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // ------------------------------------------------
        post.put("appId", LvkemenUtil.appId);
        post.put("categoryUid", "1596085500738382428");

        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        dataSignature = LvkemenUtil.encryptToMd5String(content, LvkemenUtil.appKey);

        WebClient webClient = WebClient.create();
        Mono<String> mono = webClient.post()
            .uri(LvkemenUtil.urlQueryProductPages)
            .header("time-stamp", LvkemenUtil.getTimeStamp())
            .header("data-signature", dataSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(String.class);

        returnString = mono.block();
        return returnString;
    }
    private String queryProductImagesByProductUid() throws Exception {
        String returnString;
        String content, dataSignature;

        HashMap<String, String> post = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // ------------------------------------------------
        post.put("appId", LvkemenUtil.appId);
        post.put("productUid", "827430132490320807");

        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        dataSignature = LvkemenUtil.encryptToMd5String(content, LvkemenUtil.appKey);

        WebClient webClient = WebClient.create();
        Mono<String> mono = webClient.post()
            .uri(LvkemenUtil.urlQueryProductImagesByProductUid)
            .header("time-stamp", LvkemenUtil.getTimeStamp())
            .header("data-signature", dataSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(String.class);

        returnString = mono.block();
        return returnString;
    }
}
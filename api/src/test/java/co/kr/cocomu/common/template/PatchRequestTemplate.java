package co.kr.cocomu.common.template;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class PatchRequestTemplate {

    public static ValidatableMockMvcResponse execute(String path) {
        return RestAssuredMockMvc
            .given().log().all()
            .header("Authorization", "Bearer token")
            .when().patch(path)
            .then().log().all();
    }

    public static <T> ValidatableMockMvcResponse executeWithBody(String path, T requestBody) {
        return RestAssuredMockMvc
            .given().log().all()
            .header("Authorization", "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .when().patch(path)
            .then().log().all();
    }

    public static ValidatableMockMvcResponse executeMultipartRequest(String path, MockMultipartFile file)
        throws IOException {
        return RestAssuredMockMvc
            .given().log().all()
            .header("Authorization", "Bearer token")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart(file.getName(), file.getOriginalFilename(), file.getBytes(), file.getContentType())
            .when().patch(path)
            .then().log().all();
    }

    public static ValidatableMockMvcResponse executeWithParams(String path, Map<String, Object> queryParams) {
        return RestAssuredMockMvc
            .given().log().all()
            .header("Authorization", "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .params(queryParams)
            .when().patch(path)
            .then().log().all();
    }

    public static ValidatableMockMvcResponse executeWithBodyAndParams(
        String path,
        Object requestBody,
        Map<String, Object> queryParams
    ) {
        return RestAssuredMockMvc
            .given().log().all()
            .header("Authorization", "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .params(queryParams)
            .when().patch(path)
            .then().log().all();
    }

}
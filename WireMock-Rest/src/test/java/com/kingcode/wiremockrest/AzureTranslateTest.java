package com.kingcode.wiremockrest;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class AzureTranslateTest {

    private static final String AZURE_PATH = "/api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=en";

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    @Test
    public void testTranslate() {
        //Given
        wireMock.stubFor(post(urlEqualTo(AZURE_PATH))
            .withHeader("Ocp-Apim-Subscription-Key", equalTo("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"))
            .withHeader("Ocp-Apim-Subscription-Region", equalTo("westeurope"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalToJson("[{'Text':'Funfzehn jahre alt'}]"))
            .willReturn(aResponse()
                .withHeader("content-type", "application/json")
                .withBody(
                    "[{\"detectedLanguage\":{\"language\":\"de\",\"score\":1.0},\"translations\":[{\"teeen " +
                        "years old\",\"to\":\"en\"}]}]")
            ));

        //When
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Ocp-Apim-Subscription-Key", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        headers.add("Ocp-Apim-Subscription-Region", "westeurope");
        headers.add("Content-Type", "application/json");
        ResponseEntity<String> response = rest.postForEntity(
            "http://localhost:" + wireMock.getRuntimeInfo().getHttpPort() + "/api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=en",
            new HttpEntity<>("[{'Text': 'Funfzehn jahre alt'}]", headers),
            String.class
        );


        //Then
        assertThat(response.getBody()).isEqualTo(
            "[{\"detectedLanguage\":{\"language\":\"de\",\"score\":1.0},\"translations\":[{\"teeen " +
                "years old\",\"to\":\"en\"}]}]");
    }
}
package com.kingcode.wiremockrest;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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
            .willReturn(aResponse()
                .withHeader("content-type", "application/json")
                .withBody(
                    "[{\"detectedLanguage\":{\"language\":\"de\",\"score\":1.0},\"translations\":[{\"teeen " +
                        "years old\",\"to\":\"en\"}]}]")
            ));

        //When
        RestTemplate rest = new RestTemplate();
        String response = rest.postForObject(
            "http://localhost:" + wireMock.getRuntimeInfo().getHttpPort() + "/api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=en",
            null,
            String.class
        );

        //Then
        assertThat(response).isEqualTo(
            "[{\"detectedLanguage\":{\"language\":\"de\",\"score\":1.0},\"translations\":[{\"teeen " +
                "years old\",\"to\":\"en\"}]}]");
    }
}
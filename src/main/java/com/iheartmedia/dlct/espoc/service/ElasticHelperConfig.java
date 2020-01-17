package com.iheartmedia.dlct.espoc.service;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "elastic-helper")
public class ElasticHelperConfig {

    private AwsConfig aws = new AwsConfig();
    private DomainConfig domain = new DomainConfig();

    @Bean
    public RestHighLevelClient getClient() {

        if (aws.key == null || aws.key.isEmpty()) {
            throw new IllegalStateException("No clientKey supplied (elastic-helper.aws.key)");
        }
        if (aws.secret == null || aws.secret.isEmpty()) {
            throw new IllegalStateException("No secret supplied (elastic-helper.aws.secret)");
        }

        BasicAWSCredentials creds = new BasicAWSCredentials(aws.key, aws.secret);
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(creds);
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(domain.serviceName);
        signer.setRegionName(domain.region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(domain.serviceName, signer, credentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(domain.endpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }

    @Getter
    @Setter
    private static class AwsConfig {
        private String key;
        private String secret;
    }

    @Getter
    @Setter
    private static class DomainConfig {
        private String serviceName;
        private String region;
        private String endpoint;
        private String type;
    }

}





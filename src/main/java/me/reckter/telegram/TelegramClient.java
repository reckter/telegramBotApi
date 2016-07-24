package me.reckter.telegram;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author Mischa Holz
 */
public class TelegramClient {
    private final String apiBase;


    public class RequestBuilder<OUT, IN> {

        private HttpMethod method;

        private String uri;

        private Class<OUT> forClass;

        private ParameterizedTypeReference<OUT> forType;

        private SuccessCallback<ResponseEntity<OUT>> successCallback;

        private FailureCallback failureCallback;

        private boolean isFileUpload = false;

        private IN payload;

        public RequestBuilder<OUT, IN> method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestBuilder<OUT, IN> uri(String uri) {
            this.uri = uri;
            return this;
        }

        public RequestBuilder<OUT, IN> forClass(Class<OUT> forClass) {
            this.forClass = forClass;
            return this;
        }

        public RequestBuilder<OUT, IN> forClass(ParameterizedTypeReference<OUT> forType) {
            this.forType = forType;
            return this;
        }

        public RequestBuilder<OUT, IN> success(SuccessCallback<ResponseEntity<OUT>> successCallback) {
            this.successCallback = successCallback;
            return this;
        }

        public RequestBuilder<OUT, IN> fileUpload() {
            this.isFileUpload = true;
            return this;
        }

        public RequestBuilder<OUT, IN> failure(FailureCallback failureCallback) {
            this.failureCallback = failureCallback;
            return this;
        }

        public RequestBuilder<OUT, IN> payload(IN payload) {
            this.payload = payload;
            return this;
        }

        public ResponseEntity<OUT> request() {
            if(payload != null) {
                if(forType != null) {
                    return makeRequest(method, uri, forType, payload, isFileUpload);
                } else {
                    return makeRequest(method, uri, forClass, payload, isFileUpload);
                }
            } else {
                if(forType != null) {
                    return makeRequest(method, uri, forType, isFileUpload);
                } else {
                    return makeRequest(method, uri, forClass, isFileUpload);
                }
            }
        }

        public void requestAsync() {
            if(payload != null) {
                if(failureCallback != null) {
                    if(forType != null) {
                        makeRequest(method, uri, forType, payload, successCallback, failureCallback, isFileUpload);
                    } else {
                        makeRequest(method, uri, forClass, payload, successCallback, failureCallback, isFileUpload);
                    }
                } else {
                    if(forType != null) {
                        makeRequest(method, uri, forType, payload, successCallback, isFileUpload);
                    } else {
                        makeRequest(method, uri, forClass, payload, successCallback, isFileUpload);
                    }
                }
            } else {
                if(failureCallback != null) {
                    if(forType != null) {
                        makeRequest(method, uri, forType, successCallback, failureCallback, isFileUpload);
                    } else {
                        makeRequest(method, uri, forClass, successCallback, failureCallback, isFileUpload);
                    }
                } else {
                    if(forType != null) {
                        makeRequest(method, uri, forType, successCallback, isFileUpload);
                    } else {
                        makeRequest(method, uri, forClass, successCallback, isFileUpload);
                    }
                }
            }
        }
    }

    public TelegramClient(String apiBase, String botToken) {
        this.apiBase = apiBase + botToken + "/";
    }

    public <OUT, IN> RequestBuilder<OUT, IN> request() {
        return new RequestBuilder<>();
    }

    public <OUT, IN> RequestBuilder<OUT, IN> requestFor(Class<OUT> forClass) {
        return (new RequestBuilder<OUT, IN>()).forClass(forClass);
    }

    public <OUT, IN> RequestBuilder<OUT, IN> requestForWith(Class<OUT> forClass, IN payload) {
        return (new RequestBuilder<OUT, IN>()).forClass(forClass).payload(payload);
    }

    public <OUT, IN> RequestBuilder<OUT, IN> requestFor(ParameterizedTypeReference<OUT> forClass) {
        return (new RequestBuilder<OUT, IN>()).forClass(forClass);
    }

    public <OUT, IN> RequestBuilder<OUT, IN> requestForWith(ParameterizedTypeReference<OUT> forClass, IN payload) {
        return (new RequestBuilder<OUT, IN>()).forClass(forClass).payload(payload);
    }

    private <T> ResponseEntity<T> makeRequest(HttpMethod method, String uri, Class<T> forClass, boolean isFileUpload) {
        return makeRequest(method, uri, forClass, (Object) null, isFileUpload);
    }

    private <T, U> ResponseEntity<T> makeRequest(HttpMethod method, String uri, Class<T> forClass, U payload, boolean isFileUpload) {
        String url = apiBase + (uri.startsWith("/") ? uri.substring(1) : uri);

        HttpEntity<U> entity;
        if(payload == null) {
            entity = prepare(url, method);
        } else {
            entity = prepare(url, method, payload, isFileUpload);
        }

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        if(isFileUpload) {
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        }

        restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));

        ResponseEntity<T> ret = restTemplate.exchange(url, method, entity, forClass);
        return ret;
    }

    private <T> ResponseEntity<T> makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, boolean isFileUpload) {
        return makeRequest(method, uri, forClass, (Object) null, isFileUpload);
    }

    private <T, U> ResponseEntity<T> makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, U payload, boolean isFileUpload) {
        String url = apiBase + (uri.startsWith("/") ? uri.substring(1) : uri);

        HttpEntity<U> entity;
        if(payload == null) {
            entity = prepare(url, method);
        } else {
            entity = prepare(url, method, payload, isFileUpload);
        }

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        if(isFileUpload) {
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        }
        restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));

        return restTemplate.exchange(url, method, entity, forClass);
    }

    private <T> void makeRequest(HttpMethod method, String uri, Class<T> forClass, SuccessCallback<? super ResponseEntity<T>> successCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, null, successCallback, null, isFileUpload);
    }

    private <T> void makeRequest(HttpMethod method, String uri, Class<T> forClass, SuccessCallback<? super ResponseEntity<T>> successCallback, FailureCallback failureCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, null, successCallback, failureCallback, isFileUpload);
    }

    private <T, U> void makeRequest(HttpMethod method, String uri, Class<T> forClass, U payload, SuccessCallback<? super ResponseEntity<T>> successCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, payload, successCallback, null, isFileUpload);
    }

    @SuppressWarnings("Duplicates")
    private <T, U> void makeRequest(HttpMethod method, String uri, Class<T> forClass, U payload, SuccessCallback<? super ResponseEntity<T>> successCallback, FailureCallback failureCallback, boolean isFileUpload) {
        String url = apiBase + (uri.startsWith("/") ? uri.substring(1) : uri);

        HttpEntity<U> entity;
        if(payload == null) {
            entity = prepare(url, method);
        } else {
            entity = prepare(url, method, payload, isFileUpload);
        }

        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        if(isFileUpload) {
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        }

        ListenableFuture<ResponseEntity<T>> future = restTemplate.exchange(url, method, entity, forClass);
        future.addCallback(successCallback, failureCallback);
    }

    private <T> void makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, SuccessCallback<? super ResponseEntity<T>> successCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, null, successCallback, null, isFileUpload);
    }

    private <T> void makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, SuccessCallback<? super ResponseEntity<T>> successCallback, FailureCallback failureCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, null, successCallback, failureCallback, isFileUpload);
    }

    private <T, U> void makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, U payload, SuccessCallback<? super ResponseEntity<T>> successCallback, boolean isFileUpload) {
        makeRequest(method, uri, forClass, payload, successCallback, null, isFileUpload);
    }

    @SuppressWarnings("Duplicates")
    private <T, U> void makeRequest(HttpMethod method, String uri, ParameterizedTypeReference<T> forClass, U payload, SuccessCallback<? super ResponseEntity<T>> successCallback, FailureCallback failureCallback, boolean isFileUpload) {
        String url = apiBase + (uri.startsWith("/") ? uri.substring(1) : uri);

        HttpEntity<U> entity;
        if(payload == null) {
            entity = prepare(url, method);
        } else {
            entity = prepare(url, method, payload, isFileUpload);
        }

        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        if(isFileUpload) {
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        }

        ListenableFuture<ResponseEntity<T>> future = restTemplate.exchange(url, method, entity, forClass);
        future.addCallback(successCallback, failureCallback);
    }


    private <T> HttpEntity<T> prepare(String requestURL, HttpMethod method, T payload, boolean isFileUpload) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if(isFileUpload) {
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        } else {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }


        return new HttpEntity<>(payload, httpHeaders);
    }



    private <T> HttpEntity<T> prepare(String requestURL, HttpMethod method) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(httpHeaders);
    }

}

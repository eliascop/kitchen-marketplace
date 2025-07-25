package br.com.kitchen.ordermonitoring.app.config;

import br.com.kitchen.ordermonitoring.app.interceptor.AuthInterceptor;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public Set<ServerEndpointConfig.Configurator> configurators(AuthInterceptor authInterceptor) {
        Set<ServerEndpointConfig.Configurator> configurators = new HashSet<>();
        ServerEndpointConfig.Configurator configurator = new ServerEndpointConfig.Configurator() {
            @Override
            public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {}
        };
        configurators.add(configurator);
        return configurators;
    }
}

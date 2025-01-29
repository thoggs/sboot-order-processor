package codesumn.sboot.order.processor.application.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.order.queue}")
    private String orderQueue;

    @Value("${spring.rabbitmq.processor.response.queue}")
    private String processorResponseQueue;

    @Value("${spring.rabbitmq.order.exchange}")
    private String orderExchange;

    @Value("${spring.rabbitmq.processor.response.exchange}")
    private String processorResponseExchange;

    @Value("${spring.rabbitmq.order.routing.key}")
    private String orderRoutingKey;

    @Value("${spring.rabbitmq.processor.response.routing.key}")
    private String processorResponseRoutingKey;

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueue, true);
    }

    @Bean
    public Queue processorResponseQueue() {
        return new Queue(processorResponseQueue, true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderExchange);
    }

    @Bean
    public DirectExchange processorResponseExchange() {
        return new DirectExchange(processorResponseExchange);
    }

    @Bean
    public Binding bindingOrder(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(orderRoutingKey);
    }

    @Bean
    public Binding bindingProcessorResponse(Queue processorResponseQueue, DirectExchange processorResponseExchange) {
        return BindingBuilder
                .bind(processorResponseQueue)
                .to(processorResponseExchange)
                .with(processorResponseRoutingKey);
    }
}
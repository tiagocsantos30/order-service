package br.com.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "orderExchange";
    public static final String QUEUE_RECEIVED = "orderReceivedQueue";
    public static final String QUEUE_PROCESSED = "orderProcessedQueue";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue receivedQueue() {
        return new Queue(QUEUE_RECEIVED, true);
    }

    @Bean
    Queue processedQueue() {
        return new Queue(QUEUE_PROCESSED, true);
    }

    @Bean
    Binding receivedBinding(Queue receivedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(receivedQueue).to(exchange).with("received");
    }

    @Bean
    Binding processedBinding(Queue processedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(processedQueue).to(exchange).with("processed");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("rabbitmq-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter,
                                                    ThreadPoolTaskExecutor taskExecutor) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_PROCESSED);
        container.setMessageListener(listenerAdapter);
        container.setTaskExecutor(taskExecutor);
        container.setConcurrentConsumers(20);
        container.setMaxConcurrentConsumers(500);
        container.setPrefetchCount(50);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(OrderMessageProcessor listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }
}
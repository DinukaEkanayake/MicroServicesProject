package com.university.OrderService.service;

import com.university.OrderService.dto.InventoryResponse;
import com.university.OrderService.dto.OrderLineItemsDto;
import com.university.OrderService.dto.OrderRequest;
import com.university.OrderService.model.Order;
import com.university.OrderService.model.OrderLineItems;
import com.university.OrderService.repository.OrderRepository;
import io.micrometer.observation.ObservationRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream().map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode) //.map(orderLineItem -> orderLineItem.getSkuCode())
                .toList();

        //to check the item in the stock or not,call inventory service
        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory/{sku-code}",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                //uri format= http://localhost:8082/api/inventory?skuCode=iphone-13&skuCode=iphone-15
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        //assert inventoryResponses != null;
        boolean allProductsInstock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        //inventoryResponse -> inventoryResponse.isInStock()
        if (allProductsInstock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock");
        }

    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}

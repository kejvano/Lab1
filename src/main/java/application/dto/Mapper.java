package application.dto;

import application.*;
import java.util.*;
import java.util.stream.Collectors;

public class Mapper {

    // Map User to UserDTO
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(user.getUid(), user.getName(), user.getPermissionLevel().toString());
    }

    // Map Product to ProductDTO
    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO(product.getId(), product.getName(), product.getStock(), product.getPrice(), product.getCategory().toString());
    }

    // Map Order to OrderDTO
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;

        // Map every Product in the Order to OrderProductDTO
        List<OrderProductDTO> productDTOs = order.getProductIds().entrySet().stream()
                .map(entry -> new OrderProductDTO(
                        entry.getKey().getName(),
                        entry.getKey().getCategory().toString(),
                        entry.getValue()))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getOrdered(),
                order.getPacked(),
                order.getShipped(),
                order.getUserId(),
                productDTOs
        );
    }

    // Map List<User> to List<UserDTO>
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream().map(Mapper::toDTO).collect(Collectors.toList());
    }

    // Map List<Product> to List<ProductDTO>
    public static List<ProductDTO> toDTOListProducts(List<Product> products) {
        if (products == null) return null;
        return products.stream().map(Mapper::toDTO).collect(Collectors.toList());
    }

    // Map List<Order> to List<OrderDTO>
    public static List<OrderDTO> toDTOListOrders(List<Order> orders) {
        if (orders == null) return null;
        return orders.stream().map(Mapper::toDTO).collect(Collectors.toList());
    }
}
package application.dto;

import java.sql.Timestamp;
import java.util.List;

public class OrderDTO {
    private int id;
    private Timestamp ordered;
    private Timestamp packed;
    private Timestamp shipped;
    private int userId;
    private List<OrderProductDTO> products;

    public OrderDTO(int id, Timestamp ordered, Timestamp packed, Timestamp shipped, int userId, List<OrderProductDTO> products) {
        this.id = id;
        this.ordered = ordered;
        this.packed = packed;
        this.shipped = shipped;
        this.userId = userId;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getOrdered() {
        return ordered;
    }

    public void setOrdered(Timestamp ordered) {
        this.ordered = ordered;
    }

    public Timestamp getPacked() {
        return packed;
    }

    public void setPacked(Timestamp packed) {
        this.packed = packed;
    }

    public Timestamp getShipped() {
        return shipped;
    }

    public void setShipped(Timestamp shipped) {
        this.shipped = shipped;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductDTO> products) {
        this.products = products;
    }
}
package application.dto;

public class OrderProductDTO {
    private String productName;
    private String category;
    private int quantity;

    public OrderProductDTO(String productName, String category, int quantity) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
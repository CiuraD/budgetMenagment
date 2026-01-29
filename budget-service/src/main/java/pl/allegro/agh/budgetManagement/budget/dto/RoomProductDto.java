package pl.allegro.agh.budgetManagement.budget.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomProductDto {
    private Long productId;
    private Long roomId;

    @NotBlank
    private String productName;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    @JsonProperty("paid")
    private boolean isPaid;

    public RoomProductDto() {
    }

    public RoomProductDto(Long productId, Long roomId, String productName, BigDecimal price, boolean isPaid) {
        this.productId = productId;
        this.roomId = roomId;
        this.productName = productName;
        this.price = price;
        this.isPaid = isPaid;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}

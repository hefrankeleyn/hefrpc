package cn.hefrankeleyn.hefrpc.demo.api;

/**
 * @Date 2024/3/8
 * @Author lifei
 */
public class Order {
    private Integer oid;
    private Double price;

    public Order() {
    }

    public Order(Integer oid, Double price) {
        this.oid = oid;
        this.price = price;
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Order{" +
                "oid=" + oid +
                ", price=" + price +
                '}';
    }
}

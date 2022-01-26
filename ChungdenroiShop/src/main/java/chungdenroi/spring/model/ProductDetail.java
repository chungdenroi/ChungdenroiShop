package chungdenroi.spring.model;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "product_detail")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Enter size of product!")
    @Column(name = "size", length = 100, nullable = false)
    private String size;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "int default 100")
    private int quantity;

    @NotEmpty(message = "Category URL can be not empty!")
    @Size(min=2, max = 100)
    @Column(length = 100, unique = true)
    private String url;

    @Size(max = 100)
    @Column(columnDefinition = "varchar(100) default 'Chungdenroi'")
    private String brand;

    @OneToOne
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
//    public String getImageByProductID(Product product) {
//        return image;
//    }
}

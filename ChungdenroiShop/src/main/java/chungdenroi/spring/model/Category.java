package chungdenroi.spring.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Category name can be not empty")
    @Size(min=2, max=255)
//    @Column(length = 255, nullable = false,insertable = false)
    private String name;

    @Column(name = "description" )
    private String description;

    @NotEmpty(message = "Category URL can be not empty!")
    @Size(min=2, max = 100)
    @Column(name = "url", unique = true, length = 100, nullable = false)
    private String url;

    @Size(min=5)
    @Column(name = "image", nullable = false)
    @ColumnDefault("'unknown.png'")
    private String image;

//    @OneToOne(mappedBy = "category")
//    private CategoryDetail categoryDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

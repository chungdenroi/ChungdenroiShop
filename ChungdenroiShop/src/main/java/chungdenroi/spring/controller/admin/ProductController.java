package chungdenroi.spring.controller.admin;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Customer;
import chungdenroi.spring.model.Product;
import chungdenroi.spring.model.ProductDetail;
import chungdenroi.spring.repository.CategoryRepository;
import chungdenroi.spring.repository.ProductDetailRepository;
import chungdenroi.spring.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/product/")
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    DashboardController dashboardController;

    @RequestMapping("")
    public String getAllProduct(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                Model model, HttpSession session) {
        if(dashboardController.checkLoginAdmin(session)) {
            Pageable pageable = PageRequest.of(page,4);
            Page<Product> pageTotal = productRepository.findProductsBy(pageable);
            int paging = pageTotal.getTotalPages();
            model.addAttribute("pageTotal", paging);

            List<ProductDetail> productDetails = productDetailRepository.findAll();
            model.addAttribute("products", pageTotal);
            model.addAttribute("productDetails", productDetails);
            System.out.println("Send data success");

            return "admin/product/productList";
        }else  return "redirect:/login";

    }

    @RequestMapping("/{id}")
    public String getProductByID(Model model,
                                  @PathVariable(value = "id") Long id) {
        Product product = productRepository.getById(id);
        ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(product.getId());
        System.out.println(product.getId());
        if(product.getId() == null || productDetail == null) {
            System.out.println("Product or product detail is null");
            return "redirect:/admin/product/";

        }
        model.addAttribute("product", product);
        model.addAttribute("productDetail", productDetail);
        return "admin/product/productDetail";
    }

    @RequestMapping("/add")
    public String addProduct (Model model) {
        Product product = new Product();
        ProductDetail productDetail = new ProductDetail();
        List<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("product", product);
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("categoryList", categoryList);
        return "admin/product/productAdd";
    }

    @RequestMapping("/update/{id}")
    public String updateProduct(
            @PathVariable (value = "id") Long id, Model model)  {
        Product product = productRepository.getById(id);
        ProductDetail productDetail = productDetailRepository.getById(product.getId());
        List<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("product", product);
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("categoryList", categoryList);
        return "admin/product/productUpdate";
    }
    @RequestMapping("/updateDetail/{id}")
    public String updateProductDetail(
            @PathVariable (value = "id") Long id, Model model)  {
//        Product product = productRepository.getById(id);
        ProductDetail productDetail = productDetailRepository.getById(id);
//        model.addAttribute("product", product);
        model.addAttribute("productDetail", productDetail);
        return "admin/product/productDetailUpdate";
    }


    @RequestMapping("/saveAdd")
    public String saveProductAdd (
            @Valid Product product,
            @Valid ProductDetail productDetail,
            BindingResult result,
            Model model) {
        System.out.println("Call save add method");
        if(result.hasErrors()) {
            productRepository.delete(product);
            return "redirect:/admin/product/add";
        } else {
            if(!productDetailRepository.existsByUrl(productDetail.getUrl())) {
                productRepository.save(product);
                productDetail.setProduct(product);
                productDetailRepository.save(productDetail);
                System.out.println("Save success");
                return "redirect:/admin/product/";

            } else {
                model.addAttribute("messagePd", "Product url is already");
                return "admin/product/productAdd";
            }
        }
    }


    @RequestMapping("/saveUpdate")
    public String saveProductUpdate (
            @Valid Product product,
            @RequestParam(value = "id", required = false) Long id,
            BindingResult result) {
        System.out.println("Call save update method");
        if(result.hasErrors()) {
            return "redirect:/admin/product/update/"+id;
        } else {
            product.setId(id);
            productRepository.save(product);
            System.out.println("Save success");
        }
        return "redirect:/admin/product/";
    }
    @RequestMapping("/saveUpdateDetail")
    public String saveProductDetailUpdate (
            @Valid ProductDetail productDetail,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "product", required = false) Long product_id,
            BindingResult result) {
        System.out.println("id" + id);
        System.out.println("Call save update detial method");
        productDetail.setId(id);
        productDetailRepository.save(productDetail);
        System.out.println("Save success");
        return "redirect:/admin/product/"+product_id;
    }

    @RequestMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable(value = "id") Long id) {
        Product product = productRepository.getById(id);
        ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(id);
        productDetailRepository.delete(productDetail);
        productRepository.delete(product);
        return "redirect:/admin/product/";
    }

    @RequestMapping("/search")
    public String searchCategory(
            Model model,
            @RequestParam(value = "name") String name) {
        System.out.println("call search medthod");
        List<Product> products  = productRepository.findProductByNameIsLike("%"+name+"%");
        model.addAttribute("products", products);
        System.out.println("Name search: " +  name);
        return "admin/product/productList";
    }
    @RequestMapping("/asc")
    public String sortByAsc(Model model) {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));
        model.addAttribute("products", products);
        return "admin/product/productList";
    }
    @RequestMapping("/desc")
    public String sortByDESC(Model model) {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC,"name"));
        model.addAttribute("products", products);
        return "admin/product/productList";
    }
}

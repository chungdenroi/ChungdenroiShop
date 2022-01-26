package chungdenroi.spring.controller;

import chungdenroi.spring.model.*;
import chungdenroi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderProductsRepository orderProductsRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @RequestMapping("")
    public String index(Model model, HttpSession session) {
        List<Category> categoryDetails = categoryRepository.findAll();
        model.addAttribute("categoryDetails",categoryDetails);
        //show product has quantity > 0
//        List<Product> productList = productRepository.findTopBy();
        List<ProductDetail> productDetails = productDetailRepository.findAllByQuantityIsGreaterThan(0);
        model.addAttribute("productDetails", productDetails);
//        session.setAttribute("cartTotall",0);
        return "index";
    }
    @RequestMapping("/shop")
    public String shop(Model model,
                       @RequestParam( name = "page", required = false, defaultValue = "0") Integer page) {
        //show product has quantity > 0
//        List<ProductDetail> productDetails = productDetailRepository.findAllByQuantityIsGreaterThan(0);
//        List<ProductDetail> productDetailList = new ArrayList<>();
//        for (ProductDetail productDetail : productDetails) {
//            if(!productDetail.getProduct().getCategory().getName().equals("Other")) {
//                productDetailList.add(productDetail);
//            }
//        }
        Pageable pageable = PageRequest.of(page,12);
        Page<Product> pageTotal = productRepository.findProductsBy(pageable);
        int paging = pageTotal.getTotalPages();
        model.addAttribute("pageTotal", paging);
        Page<ProductDetail> productDetailPage = productDetailRepository.findProductDetailsByQuantityGreaterThan(pageable,0);
        model.addAttribute("productDetails", productDetailPage);
        shopSideBarTool(model);

        return "shop";
    }
    @RequestMapping("/{product_url}")
    public String viewProductByUrl(
            @PathVariable(name = "product_url",required = false) String url,
            Model model) {
        try {
            if(productDetailRepository.findByUrl(url).isPresent() & url.length() != 0) {
                ProductDetail productDetail = productDetailRepository.findProductDetailByUrl(url);
                Product product = productRepository.getById(productDetail.getProduct().getId());
                model.addAttribute("product", product);
                model.addAttribute("productDetail", productDetail);
                System.out.println("view product by url");
                shopSideBarTool(model);
                return "single-product";
            } else return "404";
        } catch (Exception e) {
            return "404";
        }
    }
    @RequestMapping("/search")
    public String searchProduct(@Valid @RequestParam(name = "name", required = false, defaultValue = "")  String name, Model model) {
        List<Product> productList = productRepository.findProductByNameIsLike("%" + name + "%");
        if (productList.size() < 1) {
            model.addAttribute("productDetails", "Không tìm thấy sản phẩm");
            return "redirect:";
        } else {
            List<ProductDetail> productDetailList = addToListProductDetailQuantityThan(productList);
            model.addAttribute("productDetails", productDetailList);
            shopSideBarTool(model);
            return "shop";
        }
    }
    @RequestMapping("/category/{url}")
    public String viewProductByCategory(@Valid@PathVariable(name = "url", required = false) String url,   Model model) {
        try {
            if(categoryRepository.existsByUrl(url)) {
                Category category = categoryRepository.findByUrl(url);
                List<Product> productList = productRepository.findAllByCategory(category);
                List<ProductDetail> productDetailList = addToListProductDetailQuantityThan(productList);
                model.addAttribute("productDetails", productDetailList);
                shopSideBarTool(model);

                return "shop";
            } else return "redirect:";
        } catch (Exception e) {
            return "redirect:";
        }
    }
    @RequestMapping("sort")
    public String sortProduct(
            @Valid @RequestParam(name =  "name", defaultValue = "new", required = false) String sort,
            Model model) {
        try {
            shopSideBarTool(model);

            if(sort.equals("new")) {
                List<ProductDetail> productDetails = productDetailRepository.findAllByQuantityIsGreaterThan(0);
                model.addAttribute("productDetails", productDetails);
                return "shop";
            } else if(sort.equals("lowtohigh")) {
                List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.ASC,"price"));
                List<ProductDetail> productDetails = addToListProductDetailQuantityThan(productList);
                model.addAttribute("productDetails", productDetails);
                return "shop";
            } else if(sort.equals("hightolow")) {
                List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC,"price"));
                List<ProductDetail> productDetails = addToListProductDetailQuantityThan(productList);
                model.addAttribute("productDetails", productDetails);
                return "shop";
            } else return "redierct:/shop";

        } catch (Exception e){
            return "redirect:/shop";
        }
    }

    @RequestMapping("filterPrice")
    public String filterPrice(@Valid @RequestParam(name = "price", required = false, defaultValue = "0") String price,
            Model model) {
        shopSideBarTool(model);
        try {
            if(!price.equals("0")) {
                if(price.equals("1000000")) {
                    double _price = Double.parseDouble(price);
                    List<Product> productList = productRepository.findAllByPriceGreaterThan(_price);
                    List<ProductDetail> productDetails = addToListProductDetailQuantityThan(productList);
                    model.addAttribute("productDetails",productDetails);
                    return "shop";

                } else {
                    double min = Double.parseDouble(price.split("-")[0]);
                    double max = Double.parseDouble(price.split("-")[1]);
                    System.out.println("Min: " + min + ", Max :  " + max);

                    List<Product> productList = productRepository.findAllByPriceBetween(min, max);

                    List<ProductDetail> productDetails = addToListProductDetailQuantityThan(productList);
                    model.addAttribute("productDetails",productDetails);

                    return "shop";
                }
            } else return "redirect:/shop";

        } catch (Exception e) {
            return "redirect:/shop";
        }

    }

    @RequestMapping("filterSize")
    public String filterSize(@Valid @RequestParam(name = "filterSize", required = false, defaultValue = "0") String size,
                              Model model) {
        shopSideBarTool(model);
        try {
            if(!size.equals("0")) {
                List<ProductDetail> productDetails = productDetailRepository.findAllBySizeAndQuantityGreaterThan(size,0);
                model.addAttribute("productDetails",productDetails);
                return "shop";
            } else return "redirect:/shop";

        } catch (Exception e) {
            return "redirect:/shop";
        }

    }
//    @RequestMapping("order-status")
//    public String searchOrderStatus(
//            Model model) {
//        return "orderStatus";
//    }
    @RequestMapping("order-status")
    public String searchOrderStatus(@RequestParam(name = "orderID", required = false, defaultValue = "0") Long id, Model model) {
        try{
            if(id.equals(null) || id == 0) {
                model.addAttribute("status","Chúng tôi không tìm thấy đơn hàng của bạn, Vui lòng thử lại!");
                return "orderStatus";
            } else {
                if(ordersRepository.existsById(id)) {
                    Orders orders = ordersRepository.getById(id);
                    model.addAttribute("orders", orders);
                    return "orderStatus";
                } else {
                    model.addAttribute("status","Chúng tôi không tìm thấy đơn hàng của bạn, Vui lòng thử lại!");
                    return "orderStatus";
                }
            }
        } catch (Exception e) {
            return "redirect:";
        }
    }

    public List<ProductDetail> addToListProductDetailQuantityThan(List<Product> products) {
        List<ProductDetail> productDetails = new ArrayList<ProductDetail>();
        for (Product product : products) {
            ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(product.getId());
            if(productDetail.getQuantity() > 0) {
                productDetails.add(productDetail);
            }
        }
        return productDetails;
    }
    public List<ProductDetail> showListProductBestSale(int total) {
        List<ProductDetail> productDetails = productDetailRepository.findAll(Sort.by(Sort.Direction.ASC,"quantity"));
        List<ProductDetail> listProductBestSale = new ArrayList<ProductDetail>(total);
        for (ProductDetail productDetail : productDetails) {
            if(productDetail.getQuantity() > 0) {
                listProductBestSale.add(productDetail);
//                System.out.println("Add to best sale");
                if(listProductBestSale.size() > total) {
                    break;
                }
            }
        }
        return listProductBestSale;
    }
    public void shopSideBarTool(Model model) {
        List<Category> categoryDetails = categoryRepository.findAll();
        model.addAttribute("categoryDetails",categoryDetails);
        List<ProductDetail> bestSale = showListProductBestSale(4);
        model.addAttribute("bestSale", bestSale);
    }
}

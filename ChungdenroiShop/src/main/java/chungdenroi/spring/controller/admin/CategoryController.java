package chungdenroi.spring.controller.admin;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Product;
import chungdenroi.spring.repository.CategoryRepository;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/category/")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DashboardController dashboardController;


    @RequestMapping("")
    public String getAllCategory(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            Model model, HttpSession session) {
        if(dashboardController.checkLoginAdmin(session)) {

            //pagination
            Pageable pageable = PageRequest.of(page,4);
            Page<Category> pageTotal = categoryRepository.findCategoriesBy(pageable);
            int paging = pageTotal.getTotalPages();

            model.addAttribute("pageTotal", paging);
            model.addAttribute("categorys", categoryRepository.findCategoriesBy(pageable));
            return "admin/category/categoryList";
        } else return "redirect:/login";

    }

    @RequestMapping("/{id}")
    public String getCategoryByID(Model model,
                                 @PathVariable(value = "id") Long id) {
        Category category = categoryRepository.findById(id).get();
        model.addAttribute("categorys", category);
        return "admin/category/categoryList";
    }

    @RequestMapping("/add")
    public String addCategory (Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        return "admin/category/categoryAdd";
    }

    @RequestMapping("/update/{id}")
    public String updateCategory(
            @PathVariable (value = "id") Long id, Model model)  {
        Category category = categoryRepository.getById(id);
        model.addAttribute("category", category);
        return "admin/category/categoryUpdate";
    }



    @RequestMapping("/saveAdd")
    public String saveCategoryAdd (
            @Valid Category category,
            BindingResult result,
            Model model)  {

        if(result.hasErrors()) {
            return "redirect:/admin/category/add";
        }
        if(!categoryRepository.existsByUrl(category.getUrl())){
            categoryRepository.save(category);
            System.out.println("Save success");
            return "redirect:/admin/category/";
        }else model.addAttribute("messageCat","Url is already"); return "admin/category/categoryAdd";
    }

    @RequestMapping("/saveUpdate")
    public String saveCategoryUpdate (
            @Valid Category category,
            @RequestParam(value = "id", required = false) Long id,
            BindingResult result) {
        System.out.println("Call save update method");

        if(result.hasErrors()) {
            return "admin/category/categoryUpdate";
        }
        category.setId(id);
        categoryRepository.save(category);
        System.out.println("Save success");
        return "redirect:/admin/category/";
    }


    @RequestMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable(value = "id") Long id) {
        Category category = categoryRepository.getById(id);
        List<Product> products = productRepository.findProductByCategory_Id(id);
        for (Product product : products) {
            System.out.println(product.getId());
            product.setCategory(categoryRepository.getByName("Other"));
        }
        categoryRepository.delete(category);

        return "redirect:/admin/category/";
    }

    @RequestMapping("/search")
    public String searchCategory(
            Model model,
            @RequestParam(value = "name") String name) {
        System.out.println("call search medthod");
        List<Category> categorys  = categoryRepository.findCategoryByNameIsLike("%"+name+"%");
        model.addAttribute("categorys", categorys);
        System.out.println("Name search: " +  name);
        return "admin/category/categoryList";
    }
    @RequestMapping("/asc")
    public String sortByAsc(Model model) {
        List<Category> categorys = categoryRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));
        model.addAttribute("categorys", categorys);
        return "admin/category/categoryList";
    }
    @RequestMapping("/desc")
    public String sortByDESC(Model model) {
        List<Category> categorys = categoryRepository.findAll(Sort.by(Sort.Direction.DESC,"name"));
        model.addAttribute("categorys", categorys);
        return "admin/category/categoryList";
    }
}

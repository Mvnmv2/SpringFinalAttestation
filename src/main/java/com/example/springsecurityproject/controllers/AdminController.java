package com.example.springsecurityproject.controllers;

import com.example.springsecurityproject.enums.Status;
import com.example.springsecurityproject.models.Image;
import com.example.springsecurityproject.models.Order;
import com.example.springsecurityproject.models.Person;
import com.example.springsecurityproject.models.Product;
import com.example.springsecurityproject.repositories.CategoryRepository;
import com.example.springsecurityproject.repositories.OrderRepository;
import com.example.springsecurityproject.security.PersonDetails;
import com.example.springsecurityproject.security.ProductService;
import com.example.springsecurityproject.services.OrderService;
import com.example.springsecurityproject.services.PersonService;
import com.example.springsecurityproject.util.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    //Получаем путь где будут храниться все фотографии
    @Value("${upload.path}")
    private String uploadPath;

    private final ProductService productService;

    private final ProductValidator productValidator;

    private final CategoryRepository categoryRepository;

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final PersonService personService;

    @Autowired
    public AdminController(ProductService productService, ProductValidator productValidator, CategoryRepository categoryRepository, OrderRepository orderRepository, OrderService orderService, PersonService personService) {
        this.productService = productService;
        this.productValidator = productValidator;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.personService = personService;
    }

    //Метод по отображению главной страницы администратора с выводом всех товаров
    @GetMapping()
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Преобразовываем объект аутентификации в специальный объект класса
        //по работе с пользователями
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        String role = personDetails.getPerson().getRole();

        if (!role.equals("ROLE_ADMIN")) {
            return "redirect:/index";
        }
        model.addAttribute("products", productService.getAllProduct());
        //Выводим все заказы на странице админа
        model.addAttribute("orders", orderRepository.findAll());
        return "/admin/admin";
    }

    //Метод по отображению формы добавления товара
    @GetMapping("/product/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    //Метод по добавлению объекта с формы в таблицу product
    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                             @RequestParam("file_one") MultipartFile file_one,
                             @RequestParam("file_two") MultipartFile file_two,
                             @RequestParam("file_three") MultipartFile file_three,
                             @RequestParam("file_four") MultipartFile file_four,
                             @RequestParam("file_five") MultipartFile file_five) throws IOException {


        //Кастомная валидация(если будут объекты с одинаковым title ->
        // будет ошибка(Данное наименование товара уже используется))
        productValidator.validate(product, bindingResult);

        //Валидация встроеная с помощью анотаций
        if (bindingResult.hasErrors()) {
            return "product/addProduct";
        }

        //РАБОТА С ЗАГРУЗКОЙ ФОТОГРАФИЙ
        //Проверка на пустоту файла
        if (file_one != null) {
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Создаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }

//        else {
//            ObjectError error = new ObjectError("error", "Должна быть загружена 1 фотография");
//            bindingResult.addError(error);
//
//            return "redirect:/admin";
//        }

        if (file_two != null) {
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Содаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }
        if (file_three != null) {
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Содаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_three.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }
        if (file_four != null) {
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Содаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_four.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }
        if (file_five != null) {
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Содаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_five.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }

        productService.saveProduct(product);
        return "redirect:/admin";
    }


    //Метод по удалению товара по id
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    //Метод по редактированию товара
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("editProduct", productService.getProductById(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";

    }

    @PostMapping("/product/edit/{id}")
    public String editProduct(@ModelAttribute("editProduct") Product product,
            /*@RequestParam("file_one") MultipartFile file_one,*/
                              @PathVariable("id") int id) /*throws IOException*/ {


       /* if (file_one != null) {
            System.out.println("Тут лежит" + file_one);
            //Объект по хранению пути к файлу
            File uploadDir = new File(uploadPath);
            //Если данный путь не существует
            if (!uploadDir.exists()) {
                //то его создаем
                uploadDir.mkdir();
            }
            //Содаем уникальное имя файла
            //  UUID - неизмененный универсальный уникальный идентификатор
            // Берем этот идентификатор и добавляем к нему наименование файла который пользователь выбрал
            String uuidFile = UUID.randomUUID().toString();
            //file_one.getOriginalFilename() - наименование файла с формы
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            //Загружаем файл по указанному пути
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageFromProduct(image);
        }*/

        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    /////////////ЗАКАЗЫ/////////////

    @GetMapping("/orders/info")
    public String showOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/infoOrders";
    }

    //   Метод возвращает страницу с формой редактирования заказа и помещает в модель объект редактируемого заказа по id
    @GetMapping("/orders/{id}")
    public String editOrder(@PathVariable("id")int id, Model model){
        model.addAttribute("info_order", orderService.getOrderById(id));
        return "/admin/editOrder";
    }
    //
    // Метод принимает объект с формы и обновляет заказы
    @PostMapping("/orders/{id}")
    public String changeStatus(@PathVariable("id") int id, @RequestParam("status") Status status) {
        Order order_status = orderService.getOrderById(id);
        order_status.setStatus(status);
        orderService.updateOrderStatus(order_status);
        return "redirect:/admin/orders/info";
    }
//
//    @GetMapping("/orderStatus/edit/{id}")
//    public String editOrderStatus(@PathVariable("id") int id, Model model) {
//        model.addAttribute("order", orderRepository.findById(id));
//        //model.addAttribute("status", Status.values());
//        return "admin/editOrder";
//    }
//
//    // Метод принимает объект с формы и обновляет заказы
//    @PostMapping("/orderStatus/edit/{id}")
//    public String editOrderStatus(@PathVariable("id") int id,
//                                  @RequestParam("status") Status status){
//        Order order_status=orderService.getOrderById(id);
//        order_status.setStatus(status);
//        orderService.updateOrderStatus(order_status);
//        return "admin/infoOrders";
//    }



    // Поиск по последним сомволам номера заказа
    @PostMapping("/orders/search")
    public String searchOrderByLastSymbols(@RequestParam("value") String value, Model model) {
        model.addAttribute("orderSearch", orderRepository.findByNumberEndingWith(value));
        return "admin/infoOrders";
    }


    @GetMapping("/order/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id, Model model) {

        orderRepository.deleteById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Получаем все заказы польз-ля по объекту
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());

        if (!orderList.isEmpty()) {
            return "redirect:/admin";
        } else {
            return "redirect:/admin/orders/info";
        }
    }


    /////////////ПОЛЬЗОВАТЕЛИ/////////////

    //Метод по отображению страницы с пользователями
    @GetMapping("/users/info")
    public String showUsers(Model model) {
        model.addAttribute("persons", personService.getAllPerson());
        return "admin/infoUsers";
    }


    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("editUser", personService.getPersonById(id));
        model.addAttribute("role", personService.getAllPerson());
        return "admin/editUser";
    }

    @PostMapping("/editUser/{id}")
    public String editUser(@ModelAttribute("editUser") @Valid Person person, BindingResult bindingResult,
                           @PathVariable("id") int id) {

        if (bindingResult.hasErrors()) {
            return "admin/editUser";
        }
        personService.updatePerson(id, person);
        return "redirect:/admin";

    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        personService.deletePerson(id);
        return "redirect:/admin";
    }


}






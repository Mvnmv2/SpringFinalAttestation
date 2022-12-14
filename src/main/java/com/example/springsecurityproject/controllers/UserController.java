package com.example.springsecurityproject.controllers;

import com.example.springsecurityproject.enums.Status;
import com.example.springsecurityproject.models.Cart;
import com.example.springsecurityproject.models.Order;
import com.example.springsecurityproject.models.Product;
import com.example.springsecurityproject.repositories.CartRepository;
import com.example.springsecurityproject.repositories.OrderRepository;
import com.example.springsecurityproject.repositories.ProductRepository;
import com.example.springsecurityproject.security.PersonDetails;
import com.example.springsecurityproject.security.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    @Autowired
    public UserController(OrderRepository orderRepository, ProductService productService, ProductRepository productRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/main")
    public String getMainPage() {
        return "main";
    }


    @GetMapping("/index")
    public String index(Model model) {
        //Получаем объект аутентификации -> с помощью SecurityContextHolder обращаемся к контексту
        //и на нем вызаваем метод аутентификации.
        //По сути из потока для текущего пользователя, мы получаем объект,
        //который был положен в сессию после аутентификации пользовтеля
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        //Преобразовываем объект аутентификации в специальный объект класса
//        // по работе с пользователями
//        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
//        System.out.println(personDetails.getPerson());
//        System.out.println("ID пользователя " +personDetails.getPerson().getId());
//        System.out.println("Логин пользователя " +personDetails.getPerson().getLogin());
//        System.out.println("Пароль пользователя " +personDetails.getPerson().getPassword());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Преобразовываем объект аутентификации в специальный объект класса
        //по работе с пользователями
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        String role = personDetails.getPerson().getRole();
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProduct());
        return "user/index";
    }

    @GetMapping("/info{id}")
    public String infoProducts(Model model, @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProductById(id));
        return "/user/infoProduct";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }


    @GetMapping("/cart")
    public String cart(Model model) {
        //Получаем пользователя из сесии по id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        //Получаем корзину этого пользователя
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        //Получаем продукты из полученной корзины
        List<Product> productList = new ArrayList<>();
        for (Cart cart : cartList) {
            productList.add(productService.getProductById(cart.getProductId()));
        }
        //Считаем общую стоимость товаров
        float price = 0;
        for (Product product : productList) {
            price += product.getPrice();
        }
        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();

        cartRepository.deleteCartById(id, id_person);

        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String createOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
        for (Cart cart : cartList) {
            productList.add(productService.getProductById(cart.getProductId()));
        }

        //Создаем уникальную последовательность для каждого заказа
        String uuid = UUID.randomUUID().toString();

        for (Product product : productList) {
            Order newOrder = new Order(uuid, 1, product.getPrice(), Status.Оформлен,
                    product, personDetails.getPerson());
            orderRepository.save(newOrder);
            cartRepository.deleteCartById(product.getId(), id_person);
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String ordersUser(Model model) {
        //При заходе на страницу заказов, берем id пользователя из сэссии
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Получаем все заказы польз-ля по объекту
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        //кладем все заказы в модель
        model.addAttribute("orders", orderList);
        return "/user/orders";

    }

    @GetMapping("/order/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id, Model model) {

        orderRepository.deleteById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Получаем все заказы польз-ля по объекту
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());

        if (orderList.isEmpty()) {
            return "redirect:/index";
        } else {
            return "redirect:/orders";
        }

    }

    @PostMapping("/index/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "category", required = false, defaultValue = "") String category, Model model) {
        System.out.println(search);
        System.out.println(ot);
        System.out.println(Do);
        System.out.println(price);
        System.out.println(category);
        // Если диапазон цен от и до не пустой
        if (!ot.isEmpty() & !Do.isEmpty()) {
            // Если сортировка по цене выбрана
            if (!price.isEmpty()) {
                // Если в качестве сортировки выбрана сортировкам по возрастанию
                if (price.equals("sorted_by_ascending_price")) {
                    // Если категория товара не пустая
                    if (!category.isEmpty()) {
                        // Если категория равная сноуборды
                        if (category.equals("snowboards")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            // Если категория равная горные лыжи
                        } else if (category.equals("ski")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                            // Если категория равная одежде
                        } else if (category.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                        // Если категория не выбрана
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }

                    // Если в качестве сортировки выбрана сортировкам по убыванию
                } else if (price.equals("sorted_by_descending_price")) {

                    // Если категория не пустая
                    if (!category.isEmpty()) {
                        // Если категория равная сноуборды
                        if (category.equals("snowboards")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            // Если категория равная горные лыжи
                        } else if (category.equals("ski")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                            // Если категория равная одежде
                        } else if (category.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                        // Если категория не выбрана
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThan(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }
        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        model.addAttribute("products", productService.getAllProduct());
        return "user/index";
    }


}

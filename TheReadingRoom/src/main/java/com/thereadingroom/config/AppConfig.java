package com.thereadingroom.config;

import com.thereadingroom.model.dao.book.BookDAO;
import com.thereadingroom.model.dao.book.IBookDAO;
import com.thereadingroom.service.book.BookService;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.service.user.UserService;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.order.OrderService;
import com.thereadingroom.service.payment.IPaymentService;
import com.thereadingroom.service.payment.PaymentService;
import com.thereadingroom.utils.auth.SessionManager;
import com.thereadingroom.utils.ui.SpringFXMLLoader;
import com.thereadingroom.utils.ui.UIUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class for managing Spring beans and dependency injection.
 * This class is marked with @Configuration to indicate that it contains
 * bean definitions and @ComponentScan to enable component scanning for Spring.
 */
@Configuration
@ComponentScan(basePackages = "com.thereadingroom")
public class AppConfig {

    /**
     * Bean definition for IUserService.
     *
     * @return a new instance of UserService implementing IUserService.
     */
    @Bean
    public IUserService userService() {
        return new UserService();  // Register IUserService bean
    }

    /**
     * Bean definition for SessionManager.
     *
     * @return a singleton instance of SessionManager.
     */
    @Bean
    public SessionManager sessionManager() {
        return new SessionManager();  // Spring will ensure singleton
    }

    /**
     * Bean definition for IBookService.
     *
     * @return a new instance of BookService implementing IBookService.
     */
    @Bean
    public IBookService bookService() {
        return new BookService();  // Register IBookService bean
    }

    /**
     * Bean definition for IOrderService.
     *
     * @return a new instance of OrderService implementing IOrderService.
     */
    @Bean
    public IOrderService orderService() {
        return new OrderService();  // Register IOrderService bean
    }

    /**
     * Bean definition for CartService.
     *
     * @return a new instance of CartService for managing cart-related operations.
     */
    @Bean
    public CartService cartService() {
        return new CartService();  // Register CartService bean
    }

    /**
     * Bean definition for IPaymentService.
     *
     * @return a new instance of PaymentService implementing IPaymentService.
     */
    @Bean
    public IPaymentService paymentService() {
        return new PaymentService();  // Register IPaymentService bean
    }

    /**
     * Bean definition for IBookDAO.
     *
     * @return a new instance of BookDAO implementing IBookDAO for database access.
     */
    @Bean
    public IBookDAO bookDAO() {
        return new BookDAO();  // Register IBookDAO bean
    }

    /**
     * Bean definition for SpringFXMLLoader.
     *
     * @param context ApplicationContext for Spring dependency injection.
     * @return a new instance of SpringFXMLLoader, which integrates Spring beans with JavaFX FXML loading.
     */
    @Bean
    public SpringFXMLLoader springFXMLLoader(ApplicationContext context) {
        return new SpringFXMLLoader(context);  // Register SpringFXMLLoader bean
    }

    /**
     * Bean definition for UIUtils.
     *
     * @param springFXMLLoader the FXML loader responsible for loading JavaFX components.
     * @param context ApplicationContext for Spring dependency injection.
     * @return a new instance of UIUtils to manage UI-related utilities.
     */
    @Bean
    public UIUtils uiUtils(SpringFXMLLoader springFXMLLoader, ApplicationContext context) {
        return new UIUtils(springFXMLLoader, context);  // Pass SpringFXMLLoader and ApplicationContext to UIUtils constructor
    }

    /**
     * Bean definition for ServiceManager.
     *
     * @return a new instance of ServiceManager for managing the service layer.
     */
    @Bean
    public ServiceManager serviceManager() {
        return new ServiceManager();  // Register ServiceManager bean
    }
}

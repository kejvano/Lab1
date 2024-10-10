package application;

import database.DB_Order;
import database.DB_Product;
import database.DB_User;
import database.Database;

import application.dto.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Communicates with database
 */
public class Model {

    static Database database;

    /**
     * Initialize database connection
     *
     * @return true if successfully initialized
     */
    public static boolean initialize() {
        database = new Database();
        try {
            return database.connect(Database.DefaultDatabase);
        } catch (SQLException se) {
            System.out.println("Failed to connect to database: " + se.getMessage());
            return false;
        }
    }

    /**
     * Shutdown database connection
     *
     * @return true if successfully shutdown
     */
    public static boolean shutdown() {
        try {
            database.disconnect();
        } catch (SQLException | IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Shutdown database connection
     *
     * @return true if successfully shutdown
     */
    public static UserDTO loginUser(String username, String password) {
        User user = null;
        try {
            user = DB_User.login(database.getConnection(), username, password);
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
        return Mapper.toDTO(user);
    }

    /**
     * Register a new user
     */
    public static UserDTO registerUser(String username, String password, PermissionLevel permissionLevel) {
        User user = null;
        try {
            user = DB_User.register(database.getConnection(), username, password, permissionLevel);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTO(user);
    }

    /**
     * Get all users as UserDTO
     */
    public static List<UserDTO> getAllUsers() {
        List<User> users = null;
        try {
            users = DB_User.getAll(database.getConnection());
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTOList(users);
    }

    /**
     * Update a user from a UserDTO
     */
    public static void updateUser(UserDTO userDTO) {
        if (userDTO == null) return;
        try {
            User user = new User(
                    userDTO.getUid(),
                    userDTO.getName(),
                    PermissionLevel.valueOf(userDTO.getPermissionLevel())
            );
            DB_User.update(database.getConnection(), user);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    /**
     * Get a product as ProductDTO
     */
    public static ProductDTO getProduct(int productId, boolean includeDeleted) {
        Product product = null;
        try {
            product = DB_Product.get(database.getConnection(), productId, includeDeleted);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTO(product);
    }

    /**
     * Get all products as ProductDTO
     */
    public static List<ProductDTO> getAllProducts(boolean includeDeleted) {
        List<Product> products = null;
        try {
            products = DB_Product.getAll(database.getConnection(), includeDeleted);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTOListProducts(products);
    }

    /**
     * Add a product to the database from a ProductDTO
     */
    public static ProductDTO addProduct(String name, int stock, int price, Category category) {
        Product product = null;
        try {
            product = DB_Product.add(database.getConnection(), name, stock, price, category);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTO(product);
    }

    /**
     * Update a product from a ProductDTO
     */
    public static void updateProduct(ProductDTO productDTO) {
        if (productDTO == null) return;
        try {
            // Konvertera ProductDTO till Product innan uppdatering
            Product product = new Product(
                    productDTO.getId(),
                    productDTO.getName(),
                    productDTO.getStock(),
                    productDTO.getPrice(),
                    Category.valueOf(productDTO.getCategory())
            );
            DB_Product.update(database.getConnection(), product.getId(), product);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    /**
     * Remove a product
     */
    public static void deleteProduct(int productId) {
        try {
            DB_Product.delete(database.getConnection(), productId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    /**
     * Get all orders as OrderDTO
     */
    public static List<OrderDTO> getAllOrders() {
        List<Order> orders = null;
        try {
            orders = DB_Order.getAll(database.getConnection());
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTOListOrders(orders);
    }

    /**
     * Get all orders for a specific user as OrderDTO
     */
    public static List<OrderDTO> getAllOrders(int userId) {
        List<Order> result = null;
        try {
            result = DB_Order.getAll(database.getConnection(), userId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return Mapper.toDTOListOrders(result);
    }

    /**
     * Add a new order to the database
     */
    public static boolean placeOrder(HashMap<Integer, Integer> cart, int userId) {
        try {
            DB_Order.add(database.getConnection(), cart, userId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Mark a specific order as packed
     */
    public static void packOrder(int orderId) {
        try {
            DB_Order.dateMark(database.getConnection(), "packtime", orderId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    /**
     * Mark a specific order as shipped
     */
    public static void shipOrder(int orderId) {
        try {
            DB_Order.dateMark(database.getConnection(), "shiptime", orderId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    /**
     * Controller method for user permissions
     */
    public static boolean userAtLeast(HttpSession session, PermissionLevel permissionLevel) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        return userAtLeast(user, permissionLevel);
    }

    /**
     * Controller method for user permissions
     */
    public static boolean userAtLeast(UserDTO user, PermissionLevel permissionLevel) {
        if (user == null) return false;
        PermissionLevel userLevel = PermissionLevel.valueOf(user.getPermissionLevel());
        return userLevel.ordinal() <= permissionLevel.ordinal();
    }

    /**
     * Controlling if a user is logged in
     */
    public static boolean loggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    /**
     * Add a product to the cart
     */
    public static void addToCart(HttpSession session, int productId) {
        HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            cart.put(productId, 1);
        } else {
            if (cart.containsKey(productId)) {
                ProductDTO product = getProduct(productId, true);
                if (product != null && cart.get(productId) < product.getStock()) {
                    cart.put(productId, cart.get(productId) + 1);
                }
            } else {
                cart.put(productId, 1);
            }
        }
        session.setAttribute("cart", cart);
    }

    /**
     * Remove a product from the cart
     */
    public static void removeFromCart(HttpSession session, int productId) {
        HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }
    }
}
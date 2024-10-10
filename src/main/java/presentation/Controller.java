package presentation;

import application.*;
import application.dto.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "Controller", value = "/controller")
public class Controller extends HttpServlet {

    @Override
    public void init() throws ServletException {

        System.out.println("INITIALIZE\n\n\n\n\n");

        Model.initialize();
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        Model.shutdown();

        System.out.println("SHUTDOWN");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    /**
     * Handles both GET and POST requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getParameter("page");
        String action = request.getParameter("action");

        if (page == null) {
            page = "";
        }

        switch (page) {
            case "admin": {
                doAdminRequest(action, request, response);
                break;
            }
            case "orders": {
                doOrdersRequest(action, request, response);
                break;
            }
            default: {
                doActionRequest(action, request, response);
                break;
            }
        }


    }

    private void doActionRequest(String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "login": {
                UserDTO user = login(request.getParameter("username"), request.getParameter("password"));
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "logout": {
                session.setAttribute("user", null);
                session.setAttribute("users", null);
                session.setAttribute("orders", null);
                session.setAttribute("products", null);
                response.sendRedirect("index.jsp");
                break;
            }
            case "register": {
                UserDTO user = register(request.getParameter("username"), request.getParameter("password"));
                if (user != null) {
                    session.setAttribute("user", user);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "checkout": {
                if (Model.loggedIn(session)) {
                    HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) session.getAttribute("cart");
                    UserDTO user = (UserDTO) session.getAttribute("user");
                    if (cart != null && user != null) {
                        boolean orderSuccess = placeOrder(cart, user.getUid());
                        if (orderSuccess) {
                            session.setAttribute("cart", null);
                        }
                        response.sendRedirect("orders.jsp");
                    } else {
                        response.sendError(403);
                    }
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartAdd": {
                if (Model.loggedIn(session)) {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    Model.addToCart(session, productId);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartEmpty": {
                if (Model.loggedIn(session)) {
                    request.getSession().setAttribute("cart", null);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartRemove": {
                if (Model.loggedIn(session)) {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    Model.removeFromCart(session, productId);
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productCreate": {
                if (Model.userAtLeast(session, PermissionLevel.Admin)) {
                    String name = request.getParameter("name");
                    int stock = Integer.parseInt(request.getParameter("stock"));
                    int price = Integer.parseInt(request.getParameter("price"));
                    Category category = Category.valueOf(request.getParameter("category"));
                    ProductDTO productDTO = addProduct(name, stock, price, category);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productDelete": {
                if (Model.userAtLeast(session, PermissionLevel.Admin)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    deleteProduct(id);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productEdit": {
                if (Model.userAtLeast(session, PermissionLevel.Admin)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    int stock = Integer.parseInt(request.getParameter("stock"));
                    int price = Integer.parseInt(request.getParameter("price"));
                    Category category = Category.valueOf(request.getParameter("category"));
                    ProductDTO productDTO = new ProductDTO(id, name, stock, price, category.toString());

                    updateProduct(productDTO);

                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
        }

    }

    private void doAdminRequest(String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Model.userAtLeast(request.getSession(), PermissionLevel.Admin)) {
            response.sendError(401);
            return;
        }

        if (action == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

        HttpSession session = request.getSession();

        switch (action) {
            case "editUser": {
                if (Model.userAtLeast(session, PermissionLevel.Admin)) {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    String name = request.getParameter("name");
                    PermissionLevel permissionLevel = PermissionLevel.valueOf(request.getParameter("permissionLevel"));
                    UserDTO userDTO = new UserDTO(userId, name, permissionLevel.toString());

                    Model.updateUser(userDTO);

                    response.sendRedirect("admin.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
        }
    }

    private void doOrdersRequest(String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Model.loggedIn(request.getSession())) {
            response.sendError(401);
            return;
        }

        HttpSession session = request.getSession();
        UserDTO currentUser = (UserDTO) session.getAttribute("user");

        if (currentUser != null) {
            if (PermissionLevel.valueOf(currentUser.getPermissionLevel()).ordinal() <= PermissionLevel.Worker.ordinal()) {
                List<OrderDTO> orders = Model.getAllOrders();
                session.setAttribute("orders", orders);
            } else {
                List<OrderDTO> orders = Model.getAllOrders(currentUser.getUid());
                session.setAttribute("orders", orders);
            }
        }

        if (action == null) {
            response.sendRedirect("orders.jsp");
            return;
        }

        switch (action) {
            case "orderPack": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Worker)) {
                    pack(Integer.parseInt(request.getParameter("orderId")));
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "orderShip": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Worker)) {
                    ship(Integer.parseInt(request.getParameter("orderId")));
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }

        }
    }

    static public UserDTO login(String username, String password) {
        return Model.loginUser(username, password);
    }

    static public UserDTO register(String username, String password) {
        return Model.registerUser(username, password, PermissionLevel.Customer);
    }

    static public ProductDTO getProduct(int productId) {
        return Model.getProduct(productId, true);
    }

    static public List<ProductDTO> getAllProducts(UserDTO user) {
        if (user != null && PermissionLevel.valueOf(user.getPermissionLevel()).ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllProducts(true);
        } else if (user != null) {
            return Model.getAllProducts(false);
        } else {
            return new ArrayList<>();
        }
    }

    static public ProductDTO addProduct(String name, int stock, int price, Category category) {
        return Model.addProduct(name, stock, price, category);
    }

    static public void updateProduct(ProductDTO productDTO) {
        Model.updateProduct(productDTO);
    }

    static public void deleteProduct(int productId) {
        Model.deleteProduct(productId);
    }

    static public boolean placeOrder(HashMap<Integer, Integer> cart, int uid) {
        return Model.placeOrder(cart, uid);
    }

    static public List<OrderDTO> getAllOrders(UserDTO user) {
        if (user != null && PermissionLevel.valueOf(user.getPermissionLevel()).ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllOrders();
        } else if (user != null) {
            return Model.getAllOrders(user.getUid());
        } else {
            return new ArrayList<>();
        }
    }

    static public List<UserDTO> getAllUsers(UserDTO user) {
        if (user != null && PermissionLevel.valueOf(user.getPermissionLevel()).ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllUsers();
        } else {
            return new ArrayList<>();
        }
    }

    static public void pack(int orderId) {
        Model.packOrder(orderId);
    }

    static public void ship(int orderId) {
        Model.shipOrder(orderId);
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.bookhop.controller.admin;

import com.bookhop.dal.impl.BookDAO;
import com.bookhop.entity.Book;
import java.io.File;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 *
 * @author ADMIN
 */
@MultipartConfig
public class BookAdminServlet extends HttpServlet {

    BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //set enconding UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        //khai báo list, session, url
        HttpSession session = request.getSession();
        List<Book> listBooks = null;
        String url;
        //get action
        String action = request.getParameter("action") == null
                ? ""
                : request.getParameter("action");
        //dựa trên action chuyển trang
        switch (action) {
            case "add-book":
                url = "../views/admin/add-Book.jsp";
                break;
            default:
                listBooks = bookDAO.findAll();
                url = "../views/admin/admin-books.jsp";
                break;
        }
        //set attribute
        session.setAttribute("listBook", listBooks);
        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //set enconding UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        //get về action
        String action = request.getParameter("action") == null
                ? ""
                : request.getParameter("action");
        //dựa theo action
        switch (action) {
            case "add-book":
                addBook(request);
                break;
            case "edit-book":
                editBook(request);
                break;
            case "delete-book":
                deleteBook(request);
                break;

        }
        response.sendRedirect("book");
    }

    private void addBook(HttpServletRequest request) {
        String name = request.getParameter("name");
        int price = Integer.parseInt(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String description = request.getParameter("description");
        String imagePath = null;
        try {
            Part part = request.getPart("image");

            //đường dẫn lưu ảnh
            String path = request.getServletContext().getRealPath("/imagesProduct");
            File dir = new File(path);
            //ko có đường dẫn => tự tạo ra
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File image = new File(dir, part.getSubmittedFileName());
            part.write(image.getAbsolutePath());
            imagePath = "/BookShop/imagesProduct/" + image.getName();
        } catch (IOException | ServletException e) {
            System.out.println(e.getMessage());
        }
        Book book = Book.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .description(description)
                .image(imagePath)
                .build();
        //insert book vào DB
        bookDAO.insert(book);
    }

    private void editBook(HttpServletRequest request) {
        //get information
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int price = Integer.parseInt(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String description = request.getParameter("description");

        String imagePath = null;
        try {
            Part part = request.getPart("image");
            if (part.getSubmittedFileName() == null || part.getSubmittedFileName().trim().isEmpty()) {
                imagePath = request.getParameter("imagePath");
            } else {
                //đường dẫn lưu ảnh
                String path = request.getServletContext().getRealPath("/imagesProduct");
                File dir = new File(path);
                //ko có đường dẫn => tự tạo ra
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File image = new File(dir, part.getSubmittedFileName());
                part.write(image.getAbsolutePath());
                imagePath = "/BookShop/imagesProduct/" + image.getName();
            }
        } catch (IOException | ServletException e) {
            System.out.println(e.getMessage());
        }

        Book book = Book.builder()
                .id(id)
                .name(name)
                .price(price)
                .quantity(quantity)
                .description(description)
                .image(imagePath)
                .build();
        //update data
        bookDAO.updateBook(book);
    }

    private void deleteBook(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        bookDAO.deleteBook(id);
    }

}

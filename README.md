# 📚 Book Network Project

The **Book Network Project** is a web application that allows users to manage their book collections and borrow books from other users. It offers features like user registration, login, book management, and borrowing/returning books.

---

## 🌟 Features

### 1. User Management
- **Register**: Create a new user account.
- **Login**: Access the application securely.

### 2. Book Management
- **Add Books**: Add new books to your collection.
- **Update Books**: Modify details of books in your collection.
- **Delete Books**: Remove books from your collection.

### 3. Borrowing and Returning Books
- **Borrow Books**: Request to borrow books from other users.
- **Return Books**: Return borrowed books to the original owner.

---

## 🛠️ Technical Details

- **Backend**: Powered by **Spring Boot**.
- **Database**: Uses **PostgreSQL** for data storage.
- **Frontend**: Built with **Angular**.
- **Security**: Secured with **Spring Security** for authentication and authorization.

---

## 📋 API Endpoints

### User Endpoints
- `POST /register`: Register a new user.
- `POST /login`: Login to the application.

### Book Endpoints
- `GET /books`: Retrieve all books in the user's collection.
- `POST /books`: Add a new book to the user's collection.
- `PUT /books/{id}`: Update details of a book in the user's collection.
- `DELETE /books/{id}`: Delete a book from the user's collection.

### Borrowing Endpoints
- `GET /borrowed-books`: Retrieve all borrowed books.
- `POST /borrow-book`: Borrow a book from another user.
- `POST /return-book`: Return a borrowed book.

---

## 🚀 Running the Application

Follow these steps to run the application locally:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/book-network.git
   cd book-network

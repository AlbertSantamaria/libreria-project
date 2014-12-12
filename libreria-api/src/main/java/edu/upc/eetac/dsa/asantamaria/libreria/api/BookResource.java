package edu.upc.eetac.dsa.asantamaria.libreria.api;

import javax.sql.DataSource;
import javax.ws.rs.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.digest.DigestUtils;

import com.mysql.jdbc.Statement;

import edu.upc.eetac.dsa.asantamaria.libreria.api.model.Book;
import edu.upc.eetac.dsa.asantamaria.libreria.api.model.BookCollection;

@Path("/books")
public class BookResource {
	
	@Context
	private SecurityContext security;
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_BOOKS_QUERY = "select * from books where bookid > ifnull(?, 1) order by bookid asc limit ?";
	private String GET_BOOKS_QUERY_FROM_LAST = "select * from books where bookid < ifnull(?, 10) order by bookid desc limit ?";
	private String GET_BOOK_BY_TITLE = "select * from books where title=?";
	private String GET_BOOKS_BY_TITLE = "select * from books where title like ?";
	private String GET_BOOKS_BY_AUTHOR = "select * from books where author like ?";
	private String GET_BOOKS_BY_TITLE_AND_AUTHOR = "select * from books where title like ? and author like ?";
	private String INSERT_BOOK_QUERY = "insert into books (title, author, language, edition, editionDate, printingDate, publisher) values (?,?,?,?,?,?,?)";
	private String UPDATE_BOOK_QUERY = "update books set title=ifnull(?, title), author=ifnull(?, author), language=ifnull(?, language), edition=ifnull(?, 1), editionDate=ifnull(?, editionDate), printingDate=ifnull(?, printingDate), publisher=ifnull(?, publisher) where title=?";
	private String DELETE_TITLE_QUERY = "delete from books where title=?";
	private String GET_AUTHOR_BY_NAME = "select * from authors where name = ?";

	//Falta ordenar y quitar los println
	
	
	// Obtener colección de libros (completa/por autor/por título)
	@GET
	@Produces(MediaType.LIBRERIA_API_BOOK_COLLECTION)
	public BookCollection getBooks(@QueryParam("length") int length,
			@QueryParam("before") int before, @QueryParam("after") int after,
			@QueryParam("title") String title,
			@QueryParam("author") String author) {
		BookCollection books = new BookCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PreparedStatement stmt = null;
		try {
			if (title != null && author == null) {
				stmt = conn.prepareStatement(GET_BOOKS_BY_TITLE);
				stmt.setString(1, "%" + title + "%");

			} else if (title == null && author != null) {
				stmt = conn.prepareStatement(GET_BOOKS_BY_AUTHOR);
				stmt.setString(1, "%" + author + "%");

			} else if (title != null && author != null) {
				stmt = conn.prepareStatement(GET_BOOKS_BY_TITLE_AND_AUTHOR);
				stmt.setString(1, "%" + title + "%");
				stmt.setString(2, "%" + author + "%");

			} else {
				if (before > 0) {
					stmt = conn.prepareStatement(GET_BOOKS_QUERY_FROM_LAST);
					stmt.setInt(1, before);
				} else {
					stmt = conn.prepareStatement(GET_BOOKS_QUERY);
					if (after > 0)
						stmt.setInt(1, after);
					else
						stmt.setInt(1, 1);
				}
				if (length > 0)
					stmt.setInt(2, length);
				else
					stmt.setInt(2, 3);
			}

			ResultSet rs = stmt.executeQuery();
			int lastBook = 0;
			boolean first = true;

			System.out.println(rs.next());

			while (rs.next()) {
				Book book = new Book();
				
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getInt("edition"));
				book.setEditionDate(rs.getString("editionDate"));
				book.setPublisher(rs.getString("publisher"));
				book.setPrintingDate(rs.getString("printingDate"));		
				lastBook = rs.getInt("bookid");
				
				if (first) {
					first = false;
					books.setFirstBook(lastBook);
				}

				books.addBook(book);
			}
			books.setLastBook(lastBook);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return books;
	}

	/*
	 * // Obtener libro (por título) - no cacheable
	 * 
	 * @GET
	 * 
	 * @Path("/{booktitle}")
	 * 
	 * @Produces(MediaType.LIBRERIA_API_BOOK) public Book
	 * getBook(@PathParam("booktitle") String booktitle) { Book book = new
	 * Book();
	 * 
	 * Connection conn = null; try { conn = ds.getConnection(); } catch
	 * (SQLException e) { e.printStackTrace(); }
	 * 
	 * PreparedStatement stmt = null; try { stmt =
	 * conn.prepareStatement(GET_BOOK_BY_TITLE); stmt.setString(1, booktitle);
	 * 
	 * ResultSet rs = stmt.executeQuery(); while (rs.next()) {
	 * book.setAuthor(rs.getString("author"));
	 * book.setEdition(rs.getString("edition"));
	 * book.setEditionDate(rs.getString("editionDate"));
	 * book.setLanguage(rs.getString("language"));
	 * book.setPrintingDate(rs.getString("printingDate"));
	 * book.setPublisher(rs.getString("publisher"));
	 * book.setTitle(rs.getString("title")); } } catch (SQLException e) {
	 * e.printStackTrace(); } finally { try { if (stmt != null) stmt.close();
	 * conn.close(); } catch (SQLException e) { } }
	 * 
	 * return book; }
	 */

	// Obtener libro (por título) - cacheable
	@GET
	@Path("/{title}")
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Response getBook(@PathParam("title") String title,
			@Context Request request) {
		// Como se devuelve un Response, construimos nosotros la respueta
		CacheControl cc = new CacheControl();

		Book book = getBookFromDatabase(title);

		// Calcular el ETag del recurso
		// Como en este caso no hay un last_modified que pueda variar, se hace
		// un hash md5 con los parámetros que se pueden modificar:
		String eTagDigest = DigestUtils.md5Hex(book.getTitle()
				+ book.getAuthor() + book.getEdition() + book.getEditionDate()
				+ book.getLanguage() + book.getPrintingDate()
				+ book.getPublisher());

		EntityTag eTag = new EntityTag(eTagDigest);

		// Verificar si coincide con el etag de la peticion http
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		rb = Response.ok(book).cacheControl(cc).tag(eTag); // ok = status 200OK

		return rb.build();
	}

	// Crear ficha de libro
	@POST
	@Consumes(MediaType.LIBRERIA_API_BOOK)
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Book createBook(Book book) {

		validateBook(book);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(INSERT_BOOK_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getAuthor());
			stmt.setString(3, book.getLanguage());
			stmt.setInt(4, book.getEdition());
			stmt.setString(5, book.getEditionDate());
			stmt.setString(6, book.getPrintingDate());
			stmt.setString(7, book.getPublisher());
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				String title = rs.getString(1);

				book = getBookFromDatabase(title);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return book;
	}

	// Modificar ficha de libro
	@PUT
	@Path("/{title}")
	@Consumes(MediaType.LIBRERIA_API_BOOK)
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Book updateBook(@PathParam("title") String title, Book book) {
		// book.setEditonDate("1/13"); // Viene como "null" desde Postman, no sé
		// por qué
		// validateBook(book);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_BOOK_QUERY);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getAuthor());
			stmt.setString(3, book.getLanguage());
			stmt.setInt(4, book.getEdition());
			stmt.setString(5, book.getEditionDate());
			stmt.setString(6, book.getPrintingDate());
			stmt.setString(7, book.getPublisher());
			stmt.setString(8, title);

			int rows = stmt.executeUpdate();

			if (rows == 1) {
				book = getBookFromDatabase(title);
			} else {
				throw new NotFoundException("There's no book with title="
						+ title);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return book;
	}

	// Borrar la ficha de un libro
	@DELETE
	@Path("/{title}")
	public void deleteSting(@PathParam("title") String title) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			// Desactivamos la comprobación de FK para que no marque error
			stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
			stmt.executeUpdate();

			stmt = conn.prepareStatement(DELETE_TITLE_QUERY);
			stmt.setString(1, title);
			int rows = stmt.executeUpdate();
			if (rows == 0) {
				throw new NotFoundException("There's no book with title="
						+ title);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
				stmt.executeUpdate();
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	// Método para validar los datos del libro
	private void validateBook(Book book) {
		
		//Compuebo parametros no null
		if (book.getAuthor() == null)
			throw new BadRequestException("Author can't be null");
		if (book.getEdition() <= 0)
			throw new BadRequestException("Edition can't be null");
		if (book.getEditionDate() == null)
			throw new BadRequestException("EditionDate can't be null");
		if (book.getLanguage() == null)
			throw new BadRequestException("Language can't be null");
		if (book.getPrintingDate() == null)
			throw new BadRequestException("PrintingDate can't be null");
		if (book.getPublisher() == null)
			throw new BadRequestException("Publisher can't be null");
		if (book.getTitle() == null)
			throw new BadRequestException("Title can't be null");

		if (checkAuthorFromDatabase(book.getAuthor())) {
			// Compruebo si el autor esta en la BBDD
			if (checkBookFromDatabase(book.getTitle(), book.getAuthor())) {
				// Compruebo si el libro esta en la BBDD
				throw new BadRequestException("This book is already in the DB ");
			}
		} else {
			throw new BadRequestException("The author is not in the DB");

		}
	}

	// Método para ver si el autor ya tiene ficha
	private boolean checkAuthorFromDatabase(String name) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_AUTHOR_BY_NAME);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true; // esta en la bbdd
			} else {
				return false;// no esta en la base de datos
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	//en desuso
	private boolean checkTitleFromDatabase(String title) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_TITLE);
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery();

			System.out.println(stmt);

			if (rs.next()) {
				return true; // esta en la bbdd
			} else {
				return false;// no esta en la base de datos
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

	}

	private boolean checkBookFromDatabase(String title, String author) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOKS_BY_TITLE_AND_AUTHOR);
			stmt.setString(1, title);
			stmt.setString(2, author);
			ResultSet rs = stmt.executeQuery();

			System.out.println(stmt);

			if (rs.next()) {
				return true; // esta en la bbdd
			} else {
				return false;// no esta en la base de datos
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

	}

	// Método para recuperar libro de la BD
	private Book getBookFromDatabase(String title) {
		Book book = new Book();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_TITLE);
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getInt("edition"));
				book.setEditionDate(rs.getString("editionDate"));
				book.setPrintingDate(rs.getString("printingDate"));
				book.setPublisher(rs.getString("publisher"));

			} else {
				throw new NotFoundException("There's no book with title="
						+ title);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return book;
	}


}
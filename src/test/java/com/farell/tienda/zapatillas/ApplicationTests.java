package com.farell.tienda.zapatillas;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.farell.tienda.zapatillas.model.Categoria;
import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioZapatillas;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ServicioZapatillas servicioZapatillas;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@PersistenceContext
	private EntityManager entityManager;

	private Categoria categoriaPrueba;

	// cada zapatilla necesita una categoria, asi que creamos una solo para los tests
	@BeforeEach
	void crearCategoriaPrueba() {
		new TransactionTemplate(transactionManager).executeWithoutResult(estado -> {
			categoriaPrueba = new Categoria("Prueba", "Categoria solo para tests");
			entityManager.persist(categoriaPrueba);
		});
	}

	@AfterEach
	void borrarCategoriaPrueba() {
		new TransactionTemplate(transactionManager).executeWithoutResult(estado ->
				entityManager.remove(entityManager.find(Categoria.class, categoriaPrueba.getId())));
	}

	private Zapatilla zapatillaPrueba() {
		Zapatilla zapatilla =
				new Zapatilla("Nike", "Air Max", 100, 42, "Negro", "Unisex", "Prueba");
		zapatilla.setIdCategoria(categoriaPrueba.getId());
		return zapatilla;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void adminPagesRequireAuthentication() throws Exception {
		mockMvc.perform(get("/admin/gestionarZapatillas"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/loginAdmin?error"));
	}

	@Test
	void validPasswordOpensAdminHome() throws Exception {
		mockMvc.perform(post("/admin/inicioAdmin")
						.param("pass-login-admin", "L@vidap-05"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/inicio_admin"));
	}

	@Test
	void loggingOutRequiresPasswordAgain() throws Exception {
		MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/admin/inicioAdmin")
						.param("pass-login-admin", "L@vidap-05"))
				.andReturn()
				.getRequest()
				.getSession(false);

		mockMvc.perform(get("/cerrarSesionAdmin").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/loginAdmin"));

		mockMvc.perform(get("/admin/gestionarZapatillas").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/loginAdmin?error"));
	}

	@Test
	void productAcceptsAtMostFiveImages() throws Exception {
		Zapatilla zapatilla = zapatillaPrueba();
		zapatilla.setFotosSubidas(List.of(foto(1)));

		servicioZapatillas.registrarZapatilla(zapatilla);

		Zapatilla editarZapatilla =
				zapatillaPrueba();
		editarZapatilla.setId(zapatilla.getId());
		editarZapatilla.setFotosSubidas(List.of(foto(2), foto(3), foto(4), foto(5)));
		servicioZapatillas.guardarCambiosZapatilla(editarZapatilla);

		assertEquals(5, servicioZapatillas.obtenerNumerosImagenes(zapatilla.getId()).size());
		assertEquals(5, servicioZapatillas.obtenerImagenZapatilla(zapatilla.getId(), 5)[0]);

		Zapatilla sextaImagen =
				zapatillaPrueba();
		sextaImagen.setId(zapatilla.getId());
		sextaImagen.setFotosSubidas(List.of(foto(6)));
		assertThrows(IllegalArgumentException.class,
				() -> servicioZapatillas.guardarCambiosZapatilla(sextaImagen));
		assertEquals(5, servicioZapatillas.obtenerNumerosImagenes(zapatilla.getId()).size());

		mockMvc.perform(get("/restZapatillas/obtener-zapatilla")
						.param("id", String.valueOf(zapatilla.getId())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numeroImagenes").value(5));

		MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/admin/inicioAdmin")
						.param("pass-login-admin", "L@vidap-05"))
				.andReturn()
				.getRequest()
				.getSession(false);
		mockMvc.perform(get("/admin/editarZapatilla")
						.param("id", String.valueOf(zapatilla.getId()))
						.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/editar_producto"));
		servicioZapatillas.borrarZapatilla(zapatilla.getId());

		Zapatilla productoConDemasiadasFotos =
				zapatillaPrueba();
		productoConDemasiadasFotos.setFotosSubidas(List.of(
				foto(1), foto(2), foto(3), foto(4), foto(5), foto(6)));

		assertThrows(IllegalArgumentException.class,
				() -> servicioZapatillas.registrarZapatilla(productoConDemasiadasFotos));
	}

	@Test
	void registrationFormAcceptsFiveUploadedImages() throws Exception {
		MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/admin/inicioAdmin")
						.param("pass-login-admin", "L@vidap-05"))
				.andReturn()
				.getRequest()
				.getSession(false);

		mockMvc.perform(get("/admin/registrarZapatilla").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/registro_producto"));

		mockMvc.perform(multipart("/admin/guardarZapatilla")
						.file(foto(1)).file(foto(2)).file(foto(3)).file(foto(4)).file(foto(5))
						.param("marca", "Nike")
						.param("modelo", "Upload Test")
						.param("precio", "100")
						.param("talla", "42")
						.param("color", "Negro")
						.param("genero", "Unisex")
						.param("descripcion", "Prueba de cinco imágenes")
						.param("idCategoria", String.valueOf(categoriaPrueba.getId()))
						.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/registro_producto_ok"));

		Zapatilla registrada = servicioZapatillas.obtenerZapatillas().stream()
				.filter(z -> "Upload Test".equals(z.getModelo()))
				.findFirst()
				.orElseThrow();
		assertEquals(5, servicioZapatillas.obtenerNumerosImagenes(registrada.getId()).size());
		servicioZapatillas.borrarZapatilla(registrada.getId());
	}

	private MockMultipartFile foto(int numero) {
		return new MockMultipartFile("fotosSubidas", "foto" + numero + ".jpg",
				"image/jpeg", new byte[]{(byte) numero});
	}
}

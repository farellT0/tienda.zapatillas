package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.constantesSQL.ConstantesSQL;
import com.farell.tienda.zapatillas.model.Categoria;
import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioZapatillas;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service //marcas que la clase actua como servicio de datos
@Transactional // los metodos de la clase se ejecutan como una unica transaccion
// Transaccion:
public class ServicioZapatillasImplJPA implements ServicioZapatillas {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrarZapatilla(Zapatilla z) {
        try {
            List<byte[]> fotos = leerFotos(z.getFotosSubidas());
            fotos = conservarUltimasFotos(fotos);
            validarCantidadFotos(fotos.size(), 0, true);
            z.setImagenPortada(fotos.get(0));
            z.setImagenesAdicionales(new ArrayList<>(fotos.subList(1, fotos.size())));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron procesar las imágenes subidas", e);
        }
        // de la idCategoria recibida, hay que asignar la categoria de la base datos
        // idCategoria es la seleccionada en el desplegable de categorias
        Categoria c = entityManager.find(Categoria.class, z.getIdCategoria());
        z.setCategoria(c);
        entityManager.persist(z);
    }

    @Override
    public List<Zapatilla> obtenerZapatillas() {
        // lanzar JPQL
        // la consulta lanzada equivale al sql: select * from tabla_zapatillas as z
        return entityManager.createQuery("select z from Zapatilla z").getResultList();
    }

    @Override
    public List<Zapatilla> obtenerZapatillasPorTexto(String texto, int comienzo, int resultadosPorPagina) {
        String filtro = texto == null ? "" : texto.trim();
        int pagina = Math.max(comienzo, 0);
        int limite = Math.max(resultadosPorPagina, 1);
        return entityManager.createQuery(
                        "select z from Zapatilla z where lower(z.marca) like lower(:texto) " +
                                "or lower(z.modelo) like lower(:texto) order by z.id",
                        Zapatilla.class)
                .setParameter("texto", "%" + filtro + "%")
                .setFirstResult(pagina)
                .setMaxResults(limite)
                .getResultList();
    }

    @Override
    public int obtenerTotalZapatillas(String texto) {
        String filtro = texto == null ? "" : texto.trim();
        Long total = entityManager.createQuery(
                        "select count(z) from Zapatilla z where lower(z.marca) like lower(:texto) " +
                                "or lower(z.modelo) like lower(:texto)",
                        Long.class)
                .setParameter("texto", "%" + filtro + "%")
                .getSingleResult();
        return total.intValue();
    }

    @Override
    public Zapatilla obtenerZapatillaPorId(long id) {
        return entityManager.find(Zapatilla.class, id);
    }

    @Override
    public byte[] obtenerImagenZapatilla(long id, int numero) {
        Zapatilla zapatilla = entityManager.find(Zapatilla.class, id);
        if (zapatilla == null || numero < 1 || numero > zapatilla.getNumeroImagenes()) {
            return null;
        }
        if (numero == 1) {
            return zapatilla.getImagenPortada();
        }
        return zapatilla.getImagenesAdicionales().get(numero - 2);
    }

    @Override
    public List<Integer> obtenerNumerosImagenes(long id) {
        Zapatilla zapatilla = entityManager.find(Zapatilla.class, id);
        if (zapatilla == null) {
            return List.of();
        }
        return IntStream.rangeClosed(1, zapatilla.getNumeroImagenes()).boxed().toList();
    }

    @Override
    public void borrarZapatilla(long id) throws Exception {
        // En JPA la forma mas comun de borrar un registro,
        // Es pedirlo, comprobar que existe y luego borrarlo
        Zapatilla zapatillaAborrar = entityManager.find(Zapatilla.class, id);
        if (zapatillaAborrar != null) {
            // primero se quita de los carritos donde este, si no la base de datos no deja borrarla
            entityManager.createQuery("delete from ProductoCarrito pc where pc.zapatilla.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            entityManager.createQuery("delete from Comentario c where c.zapatilla.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            entityManager.remove(zapatillaAborrar);
        } else {
            throw new Exception("id no encontrada");
        }
    }

    @Override
    public void guardarCambiosZapatilla(Zapatilla zapatillaEditar) throws Exception {
        Zapatilla original = entityManager.find(Zapatilla.class, zapatillaEditar.getId());
        if (original == null) {
            throw new IllegalArgumentException("No existe la zapatilla que se quiere editar");
        }
        List<byte[]> fotosNuevas = conservarUltimasFotos(leerFotos(zapatillaEditar.getFotosSubidas()));

        original.setMarca(zapatillaEditar.getMarca());
        original.setModelo(zapatillaEditar.getModelo());
        original.setPrecio(zapatillaEditar.getPrecio());
        original.setTalla(zapatillaEditar.getTalla());
        original.setColor(zapatillaEditar.getColor());
        original.setGenero(zapatillaEditar.getGenero());
        original.setDescripcion(zapatillaEditar.getDescripcion());
        //usando la id seleccionada en el desplegable de categorias
        //tengo que asociar la categoria correspondiente a la zapatilla
        original.setCategoria(
                entityManager.find(Categoria.class, zapatillaEditar.getIdCategoria()));

        if (!fotosNuevas.isEmpty()) {
            List<byte[]> imagenesActuales = new ArrayList<>();
            if (original.getImagenPortada() != null && original.getImagenPortada().length > 0) {
                imagenesActuales.add(original.getImagenPortada());
            }
            imagenesActuales.addAll(original.getImagenesAdicionales());

            int actualesAConservar = Math.max(0, Zapatilla.MAXIMO_IMAGENES - fotosNuevas.size());
            List<byte[]> imagenesFinales = new ArrayList<>(
                    imagenesActuales.subList(0, Math.min(actualesAConservar, imagenesActuales.size())));
            imagenesFinales.addAll(fotosNuevas);
            original.setImagenPortada(imagenesFinales.remove(0));
            original.setImagenesAdicionales(imagenesFinales);
        }
    }

    @Override
    public List<Map<String, Object>> obtenerJSONzapatillas() {
        // con JPA hemos asociado categoria a producto
        // problema: el json recibido en portada no es correcto
        // solucion: dejar claro en una consulta a base de datos que informacion
        // va a formar el json que necesitamos en portada
        // para tener el control total, vamos a usar sql
        // asi preparo una sql para JPA
        Query consulta =
                entityManager.createNativeQuery(
                        ConstantesSQL.SQL_OBTENER_ZAPATILLAS_JSON
                );
        //usar la consulta directamente es mucho mas lioso
        //que lo siguiente
        NativeQueryImpl nativeQuery = (NativeQueryImpl) consulta;
        nativeQuery.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return nativeQuery.getResultList();
    }

    @Override
    public Map<String, Object> obtenerJSONZapatillaPorId(Long id) {
        return (Map<String, Object>) ((NativeQueryImpl) entityManager.createNativeQuery(ConstantesSQL.SQL_OBTENER_ZAPATILLA_JSON)).setParameter("id", id).setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE).getResultList().get(0);
    }

    private List<byte[]> leerFotos(List<MultipartFile> fotos) throws IOException {
        if (fotos == null) {
            return List.of();
        }
        List<byte[]> resultado = new ArrayList<>();
        for (var foto : fotos) {
            if (foto != null && !foto.isEmpty()) {
                resultado.add(foto.getBytes());
            }
        }
        return resultado;
    }

    private List<byte[]> conservarUltimasFotos(List<byte[]> fotos) {
        if (fotos.size() <= Zapatilla.MAXIMO_IMAGENES) {
            return fotos;
        }
        return new ArrayList<>(fotos.subList(
                fotos.size() - Zapatilla.MAXIMO_IMAGENES,
                fotos.size()));
    }

    private void validarCantidadFotos(int nuevas, int existentes, boolean requiereUna) {
        if (requiereUna && nuevas == 0) {
            throw new IllegalArgumentException("Debes seleccionar al menos una imagen");
        }
        if (nuevas + existentes > Zapatilla.MAXIMO_IMAGENES) {
            throw new IllegalArgumentException(
                    "Cada zapatilla admite un máximo de " + Zapatilla.MAXIMO_IMAGENES + " imágenes");
        }
    }
}

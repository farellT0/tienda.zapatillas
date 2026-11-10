package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.model.Categoria;
import com.farell.tienda.zapatillas.servicios.ServicioCategorias;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioCategoriasImplJPA implements ServicioCategorias {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Categoria> obtenerCategorias() {
        return entityManager.createQuery("select c from Categoria c").getResultList();
    }

    @Override
    public void registrarCategoria(Categoria categoria) {
        entityManager.persist(categoria);
    }

    @Override
    public boolean borrarCategoria(int id) {
        Categoria categoria = entityManager.find(Categoria.class, id);
        if (categoria == null || !categoria.getZapatillas().isEmpty()) {
            return false;
        }
        entityManager.remove(categoria);
        return true;
    }
}

package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.servicios.ServicioUsuarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioUsuariosImplJPA implements ServicioUsuarios {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Usuario> obtenerUsuarios() {
        return entityManager.createQuery("select u from Usuario u", Usuario.class).getResultList();
    }

    @Override
    public void registrarUsuario(Usuario u) {
        entityManager.persist(u);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return entityManager.find(Usuario.class, id);
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        List<Usuario> usuarios = entityManager.createQuery(
                        "select u from Usuario u where u.email = :email", Usuario.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    @Override
    public Usuario obtenerUsuarioPorEmailYpass(String email, String pass) {
        Query query =
                entityManager.createQuery(
                "select u from Usuario u where u.email = :email and u.pass = :pass");
        //la anterior consulta de jpa seria como la sql:
        // select * from usuario where email = ? and pass = ?
        query.setParameter("email", email);//asi asigno el :email
        query.setParameter("pass", pass); // asi asigno el :pass
        List<Usuario> res = query.getResultList();

        if( res.size() == 0 ){
            return null;
        }
        return res.get(0);
    }
}

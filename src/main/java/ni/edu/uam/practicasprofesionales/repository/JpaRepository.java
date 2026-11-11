package ni.edu.uam.practicasprofesionales.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import java.util.List;

/** Acceso CRUD genérico con transacciones; no usa JDBC directo. */
public final class JpaRepository {
 public <T> List<T> listar(Class<T> tipo) {
  EntityManager em=JpaUtil.abrir();
  try{return em.createQuery("select x from "+tipo.getSimpleName()+" x",tipo).getResultList();}
  finally{em.close();}
 }
 public <T> T guardar(T entidad) {
  EntityManager em=JpaUtil.abrir(); EntityTransaction tx=em.getTransaction();
  try{tx.begin();T guardada=em.merge(entidad);tx.commit();return guardada;}
  catch(RuntimeException ex){if(tx.isActive())tx.rollback();throw ex;}
  finally{em.close();}
 }
 public <T> void eliminar(Class<T> tipo,Long id) {
  if(id==null)throw new IllegalArgumentException("Seleccione un registro para eliminar.");
  EntityManager em=JpaUtil.abrir();EntityTransaction tx=em.getTransaction();
  try{tx.begin();T actual=em.find(tipo,id);if(actual!=null)em.remove(actual);tx.commit();}
  catch(RuntimeException ex){if(tx.isActive())tx.rollback();throw ex;}
  finally{em.close();}
 }
}

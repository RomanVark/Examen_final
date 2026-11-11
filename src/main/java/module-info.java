module ni.edu.uam.practicasprofesionales {
 requires javafx.controls;
 requires javafx.fxml;
 requires jakarta.persistence;
 requires org.hibernate.orm.core;
 requires org.postgresql.jdbc;
 requires java.sql;
 requires java.naming;
 requires java.logging;
 exports ni.edu.uam.practicasprofesionales.app;
 opens ni.edu.uam.practicasprofesionales.controller to javafx.fxml;
 opens ni.edu.uam.practicasprofesionales.model.academico to org.hibernate.orm.core;
}

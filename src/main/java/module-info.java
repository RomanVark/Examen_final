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
    opens ni.edu.uam.practicasprofesionales.app to javafx.graphics;
    opens ni.edu.uam.practicasprofesionales.controller to javafx.fxml;
    opens ni.edu.uam.practicasprofesionales.model.academico to org.hibernate.orm.core;
    opens ni.edu.uam.practicasprofesionales.model.alianzas to org.hibernate.orm.core;
    opens ni.edu.uam.practicasprofesionales.model.asignaciones to org.hibernate.orm.core;
    opens ni.edu.uam.practicasprofesionales.model.seguimiento to org.hibernate.orm.core;
}

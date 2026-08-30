module hr.algebra.aisi.aisijavafxclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens hr.algebra.aisi.aisijavafxclient to javafx.fxml;
    exports hr.algebra.aisi.aisijavafxclient;

    opens hr.algebra.aisi.aisijavafxclient.model to com.fasterxml.jackson.databind, javafx.base;
    exports hr.algebra.aisi.aisijavafxclient.model;
}
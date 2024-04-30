module PharmaClient {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires java.sql;
	requires transitive javafx.base;
	requires java.net.http;
	requires transitive PharmaClasses;
	requires itextpdf;
	requires java.desktop;
	exports pharmaClient;
}
package ca.n4dev.redshift.persistence.api;

public interface DatabaseConnection {
	public void openDb();
	public void closeDb();
}

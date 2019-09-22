package domaci;

public class Main0609 {

	public static void main(String[] args) {

		DBFudbalskiSavez db = new DBFudbalskiSavez(
				"jdbc:sqlite:C:\\Users\\tribu\\Desktop\\IT BOOTCAMP\\Domaci\\My homework\\09062019\\FudbalskiSavez.db");
		
		db.connect();
		
		db.ispisFudbaleraITimova();
		db.ispisFudbaleraMU();
		db.brojGolova();
		db.maxBrGolova();
		db.ispisGradova(1);
		db.dodajFudbalera("Luis Suarez", "Barselona");
		db.dodajTim("Real Madrid", "Madrid");
		db.dodajUtakmicu(2, 4, 1, "X", 2011);
		
		db.disconnect();
	}
}
